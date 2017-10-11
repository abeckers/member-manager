package de.beckers.members.api;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import de.beckers.members.UserSession;
import de.beckers.members.model.Contact;
import de.beckers.members.model.Password;
import de.beckers.members.model.Person;
import de.beckers.members.model.Password.Type;
import de.beckers.members.repository.PasswordRepository;
import de.beckers.members.repository.PersonRepository;

@RestController
public class SettingsServiceImpl implements SettingsService {
	@Autowired
	private PasswordRepository pwRep;

	@Autowired
	private PersonRepository perRep;

	@Override
	public Contact getContact() {
		String userId = UserSession.getUserId();
		Person p = perRep.findOne(userId);
		return p.getContact();
	}

	@Override
	public void updateContact(@RequestBody Contact contact) {
		String userId = UserSession.getUserId();
		Person p = perRep.findOne(userId);
		p.setContact(contact);
		perRep.save(p);
	}

	@Override
	public void updataPassword(@RequestBody PasswordChangeRequest req) {
		String userId = UserSession.getUserId();
		Person p = perRep.findOne(userId);
		Password pw = p.getPassword();
		pw.setPassword(DigestUtils.sha512Hex(req.getNewPassword()));
		pw.setType(Type.SHA512);
		pwRep.save(pw);
	}

}
