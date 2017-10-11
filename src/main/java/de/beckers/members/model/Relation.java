/*
 * (c) Copyright 2013 Troisdorf Jets
 * All Rights Reserved.
 *
 * created 20.11.2013 by Andreas Beckers
 */
package de.beckers.members.model;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Andreas Beckers
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Relation extends AEntity {
    @ManyToOne
    @JoinColumn(name = "person1_id")
    private Person person1;
    
    @ManyToOne
    @JoinColumn(name = "person2_id")
    private Person person2;
    
    @Enumerated
    private RelationType type;

}
