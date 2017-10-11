package de.beckers.members.model;

import java.util.Set;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true, exclude = { "teams", "people" })
public class Club extends ANamedEntity {
    private Boolean defaultFlag;

    @Embedded
    private Contact contact;

    @OneToMany(mappedBy = "club")
    private Set<Team> teams;

    @OneToMany(mappedBy = "club")
    private Set<Person> people;
}
