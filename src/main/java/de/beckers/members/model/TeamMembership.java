package de.beckers.members.model;

import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class TeamMembership extends AEntity {
    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    @ElementCollection
    @CollectionTable(name = "TeamMembership_roles", joinColumns = @JoinColumn(name = "TeamMembership_id"))
    private List<MemberRole> roles;
}
