package de.beckers.members.api;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.beckers.members.model.Contact;

@RequestMapping(value = "/api/settings")
public interface SettingsService {
	@RequestMapping(value = "/contact", method = RequestMethod.GET)
	Contact getContact();

	@RequestMapping(value = "/contact", method = RequestMethod.POST)
	void updateContact(@RequestBody Contact contact);

	@RequestMapping(value = "/password", method = RequestMethod.POST)
	void updataPassword(@RequestBody PasswordChangeRequest req);
}
