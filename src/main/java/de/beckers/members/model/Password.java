/*
 * (c) Copyright 2013 Troisdorf Jets
 * All Rights Reserved.
 *
 * created 19.11.2013 by Andreas Beckers
 */
package de.beckers.members.model;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Andreas Beckers
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Password extends AEntity {
	public static enum Type {
		MD5,
		SHA512
	}
	
    @Size(max = 128)
    @NotBlank
    private String password;

    @OneToOne
    @JoinColumn(name = "person_id")
    @NotNull
    private Person person;
    
    @Enumerated
    @NotNull
    private Type type;
}
