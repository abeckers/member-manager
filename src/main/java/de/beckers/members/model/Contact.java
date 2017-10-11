/*
 * (c) Copyright 2013 Troisdorf Jets
 * All Rights Reserved.
 *
 * created 20.11.2013 by Andreas Beckers
 */
package de.beckers.members.model;

import javax.persistence.Embeddable;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import lombok.Data;

import org.hibernate.validator.constraints.Email;

/**
 * @author Andreas Beckers
 */
@Embeddable
@Data
public class Contact implements Cloneable {
	@Size(max = 255)
	private String address;

	@Size(max = 5)
	private String zip;

	@Size(max = 255)
	private String place;

	@Size(max = 32)
	private String phone;

	@Size(max = 32)
	private String mobile;

	@Size(max = 32)
	private String fax;

	@Size(max = 255)
	@Email
	private String email;

	@Transient
	public String getFullAddress() {
		return address + ", " + zip + " " + place;
	}
}
