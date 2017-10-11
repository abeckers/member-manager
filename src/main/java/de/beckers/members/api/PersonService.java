package de.beckers.members.api;

import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.beckers.members.model.Person;

public interface PersonService {
    @RequestMapping(method = RequestMethod.GET, value = "/person/{id}/relations")
    PagedResources<PersistentEntityResource> getRelations(@PathVariable("id") String id, PersistentEntityResourceAssembler ass);
    
    void fixRoles(Person p);
}
