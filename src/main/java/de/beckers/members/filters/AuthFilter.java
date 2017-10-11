package de.beckers.members.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import de.beckers.members.UserSession;
import de.beckers.members.api.AuthService;

@WebFilter(urlPatterns = "/backend/*")
public class AuthFilter implements Filter {
	@Autowired
	private AuthService auth;

	@Override
	public void destroy() {
		// noop
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest hrq = (HttpServletRequest) req;
		String user = headerOrParam("X-Auth-User", hrq);
		String userId = headerOrParam("X-Auth-UserId", hrq);
		if (!Boolean.getBoolean("test")) {
			String uri = hrq.getRequestURI();
			if (isSecure(uri)) {
				String key = headerOrParam("X-Auth-Key", hrq);
				String addr = hrq.getRemoteHost();
				if (!valid(key, user, userId, addr)) {
					((HttpServletResponse) res).sendError(HttpStatus.UNAUTHORIZED.value());
					return;
				}
				UserSession.setUser(userId, user);
			}
		} else {
			UserSession.setUser(userId, user);
		}
		chain.doFilter(req, res);
	}

	private String headerOrParam(String name, HttpServletRequest hrq) {
		String r = hrq.getHeader(name);
		if (r == null) {
			r = hrq.getParameter(name);
		}
		return r;
	}

	private boolean valid(String key, String user, String userId, String addr) {
		if (key == null || user == null || userId == null) {
			return false;
		}
		return auth.validate(key, user, userId, addr);
	}

	private boolean isSecure(String pathInfo) {
		return pathInfo.startsWith("/api/") && (!pathInfo.contains("/login") && !pathInfo.contains("/registration"));
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// noop
	}
}
