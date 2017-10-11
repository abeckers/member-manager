/*
 * (c) Copyright 2014 Troisdorf Jets
 * All Rights Reserved.
 *
 * created 21.12.2014 by Andreas Beckers
 */
package de.beckers.members;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.beckers.members.model.Registration;
import de.beckers.members.model.RegistrationState;
import de.beckers.members.repository.RegistrationRepository;

/**
 * @author Andreas Beckers
 */
@RestController
public class RestFwd {
	@Autowired
	private RegistrationRepository regRep;

	@RequestMapping(value = "/confirm/{id}", method = RequestMethod.GET)
	public void login(@PathVariable("id") String id, HttpServletResponse res) throws IOException {
		Registration reg = regRep.findOne(id);
		if (reg == null) {
			res.sendRedirect("/frontend/index.html#/confirmation-error");
			return;
		}
		reg.setState(RegistrationState.EMAIL_CONFIRMED);
		regRep.save(reg);
		res.sendRedirect("/frontend/minimal.html#/confirmation/" + id);
	}
}
