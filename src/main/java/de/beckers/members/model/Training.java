package de.beckers.members.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.joda.time.LocalDate;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true, exclude = "participations")
public class Training extends AEntity {
    private LocalDate begin;

    @ManyToOne
    private TrainingGroup group;
    
    @OneToMany(mappedBy = "training")
    private Set<TrainingParticipation> participations;
}
