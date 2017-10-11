package de.beckers.members.api;

import java.util.Set;

import org.springframework.hateoas.Link;

import de.beckers.members.model.Person;
import lombok.Data;

@Data
public class RegistrationResult {
    private Person member;

    private Link parent1found;

    private Person parent1entered;

    private Link parent2found;

    private Person parent2entered;
    
    private Set<Link> teams;
}
