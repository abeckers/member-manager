/*
 * (c) Copyright 2014 Troisdorf Jets
 * All Rights Reserved.
 *
 * created 12.01.2014 by Andreas Beckers
 */
package de.beckers.members.model;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.joda.time.LocalDate;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Andreas Beckers
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Document extends ANamedEntity {
    @NotNull
    private LocalDate issueDate;

    private LocalDate expireDate;

    @Enumerated
    @NotNull
    private DocumentType type;

    @ManyToOne(optional = true)
    @JoinColumn(name = "team_id", nullable = true)
    private Team team;

    @ManyToOne(optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;
}
