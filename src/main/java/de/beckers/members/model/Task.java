/*
 * (c) Copyright 2014 Troisdorf Jets
 * All Rights Reserved.
 *
 * created 12.01.2014 by Andreas Beckers
 */
package de.beckers.members.model;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Andreas Beckers
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Task extends AEntity {
    @ManyToOne
    private Person person;

    @Enumerated
    private MemberRole teamRole;

    @Enumerated
    private MemberRole clubRole;

    @Enumerated
    private TaskType type;

    @Enumerated
    private TaskState state;

    @Size(max = 36)
    private String refId;
}
