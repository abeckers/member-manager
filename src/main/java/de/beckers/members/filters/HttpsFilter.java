/*
 * (c) Copyright 2014 Troisdorf Jets
 * All Rights Reserved.
 *
 * created 12.12.2014 by Andreas Beckers
 */
package de.beckers.members.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

/**
 * Redirect from http to https.
 * 
 * @author Andreas Beckers
 */
public class HttpsFilter implements Filter {
    @Autowired
    private Environment _env;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // noop
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (_env.acceptsProfiles("production")) {
            if (request instanceof HttpServletRequest) {
                HttpServletRequest hr = (HttpServletRequest) request;
                if (!"https".equals(hr.getHeader("x-forwarded-proto"))) {
                    ((HttpServletResponse) response).sendRedirect("https://" + hr.getHeader("x-forwarded-server"));
                    return;
                }
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // noop
    }
}
