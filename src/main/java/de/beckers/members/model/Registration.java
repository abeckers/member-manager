/*
 * (c) Copyright 2013 Troisdorf Jets
 * All Rights Reserved.
 *
 * created 24.11.2013 by Andreas Beckers
 */
package de.beckers.members.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.LocalDate;
import org.springframework.util.StringUtils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Andreas Beckers
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class Registration extends ANamedEntity {
	@NotBlank(message = "Bitte wählen Sie die Mitgliedsart")
	private String type;

	@NotBlank
	@Size(max = 32)
	@Valid
	private String firstName;

	@Enumerated
	@NotNull
	private Sex sex;

	@NotNull
	private LocalDate birthDate;

	@NotBlank
	@Size(max = 64)
	private String birthPlace;

	@NotBlank
	@Size(max = 64)
	private String nationality;

	@NotBlank
	@Size(max = 64)
	private String street;

	@NotBlank
	@Size(max = 5)
	private String zip;

	@NotBlank
	@Size(max = 64)
	private String place;

	@NotBlank
	@Size(max = 64)
	private String phone;

	@Size(max = 255)
	private String mobile;

	@NotBlank
	@Size(max = 64)
	private String profession;

	@NotBlank
	@Email
	@Size(max = 255)
	private String email;

	@NotBlank
	@Size(min = 22, max = 22, message = "Der IBAN muss genau 22 Zeichen enhalten!")
	@Pattern(regexp = "DE\\d{20}", message = "Der IBAN muss mit DE anfangen, dann müssen 20 Ziffern folgen!")
	private String iban;

	@Size(max = 32)
	private String nameParent;

	@Size(max = 32)
	private String firstNameParent;

	@Size(max = 255)
	private String mobileParent;

	@Email
	@Size(max = 255)
	private String emailParent;

	@Size(max = 32)
	private String nameParent2;

	@Size(max = 32)
	private String firstNameParent2;

	@Size(max = 255)
	private String mobileParent2;

	@Email
	@Size(max = 255)
	private String emailParent2;

	@Size(max = 255)
	private String notes;

	@Enumerated
	@NotNull
	private RegistrationState state;

	@Transient
	public List<String> getIbanChars() {
		List<String> r = new ArrayList<>();
		char[] charArray = iban.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			r.add(String.valueOf(charArray, i, 1));
		}
		return r;
	}

	public boolean hasParent1() {
		return StringUtils.hasText(nameParent) || StringUtils.hasText(firstNameParent)
				|| StringUtils.hasText(mobileParent) || StringUtils.hasText(emailParent);
	}

	public boolean hasParent2() {
		return StringUtils.hasText(nameParent2) || StringUtils.hasText(firstNameParent2)
				|| StringUtils.hasText(mobileParent2) || StringUtils.hasText(emailParent2);
	}
}
