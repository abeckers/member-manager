package de.beckers.members.api;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.beckers.members.model.Password;
import de.beckers.members.model.Password.Type;
import de.beckers.members.repository.PasswordRepository;
import de.beckers.members.repository.PersonRepository;
import lombok.Data;

@RestController
@RequestMapping("api/password")
public class PasswordServiceImpl {
	@Data
	public static class PasswordRequest {
		private String personId;

		private String password;
	}

	@Autowired
	private PasswordRepository pwRep;

	@Autowired
	private PersonRepository pRep;

	@RequestMapping(value = "/set", method = RequestMethod.POST)
	public void setPassword(@RequestBody PasswordRequest req) {
		Password password = pwRep.findByPersonId(req.getPersonId());
		if (password == null) {
			password = new Password();
			password.setPerson(pRep.findById(req.getPersonId()));
			password.setPassword(DigestUtils.sha512Hex(req.getPassword()));
			password.setType(Type.SHA512);
			pwRep.save(password);
		} else {
			password.setPassword(DigestUtils.sha512Hex(req.getPassword()));
			password.setType(Type.SHA512);
			pwRep.save(password);
		}
	}
}
