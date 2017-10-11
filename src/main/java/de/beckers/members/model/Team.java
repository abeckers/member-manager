package de.beckers.members.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true, exclude = "memberships")
public class Team extends ANamedEntity {
    @ManyToOne
    private Club club;

    @ManyToOne
    private TrainingGroup group;

    private Integer minYear;

    private Integer maxYear;

    private Sex sex;
    
    @Size(max = 10)
    private String shortName;
    
    @Size(max = 128)
    private String email;
    
    @OneToMany(mappedBy = "team")
    private Set<TeamMembership> memberships;
}
