package de.beckers.members.model;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class TrainingParticipation extends AEntity {
    enum Type {
        OK, EXCUSED, INJURED, MISSING
    };

    @ManyToOne
    @NotNull
    private Training training;

    @ManyToOne
    @NotNull
    private Person person;

    @Enumerated
    @NotNull
    private Type type;
}
