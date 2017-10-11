package de.beckers.members.api;

import java.security.SecureRandom;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import de.beckers.members.api.LoginResult.State;
import de.beckers.members.model.Password;
import de.beckers.members.model.Person;
import de.beckers.members.model.Password.Type;
import de.beckers.members.repository.PersonRepository;

@RestController
public class AuthServiceImpl implements AuthService {
	@Autowired
	private PersonRepository rep;

	private String appKey;

	public AuthServiceImpl() {
		appKey = Long.toHexString(new SecureRandom().nextLong());
	}

	@Override
	public LoginResult login(@RequestBody LoginRequest req, HttpServletRequest rq) {
		LoginResult res = new LoginResult();
		Person p = rep.findByFullName(req.getUsername());
		if (p == null) {
			res.setState(State.FAILED);
			return res;
		}
		Password password = p.getPassword();
		if (password == null) {
			res.setState(State.FAILED);
			return res;
		}
		String pwd = password.getType() == Type.MD5 ? DigestUtils.sha256Hex(req.getPassword())
				: DigestUtils.sha512Hex(req.getPassword());
		if (!pwd.equals(password.getPassword())) {
			res.setState(State.FAILED);
			return res;
		}
		res.setKey(buildKey(req.getUsername(), p.getId(), rq.getRemoteAddr()));
		res.setUserId(p.getId());
		res.setRoles(p.getRoles());
		res.setState(State.OK);
		return res;
	}

	@Override
	public boolean validate(String key, String user, String userId, String addr) {
		return buildKey(user, userId, addr).equals(key);
	}

	private String buildKey(String user, String userId, String addr) {
		return DigestUtils.sha512Hex(user + ":" + userId + ":" + addr + ":" + appKey);
	}
}
