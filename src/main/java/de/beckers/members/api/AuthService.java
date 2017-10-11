package de.beckers.members.api;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value = "/api/auth")
public interface AuthService {
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	LoginResult login(@RequestBody LoginRequest req, HttpServletRequest rq);

	boolean validate(String key, String user, String userId, String addr);
}
