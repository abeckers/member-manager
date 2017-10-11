package de.beckers.members.api;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import de.beckers.members.model.Club;
import de.beckers.members.model.MemberRole;
import de.beckers.members.model.Person;
import de.beckers.members.model.Relation;
import de.beckers.members.model.TeamMembership;
import de.beckers.members.repository.ClubRepository;
import de.beckers.members.repository.PersonRepository;
import de.beckers.members.repository.RelationRepository;

@BasePathAwareController
@ResponseBody
public class PersonServiceImpl implements PersonService {
	@Autowired
	private RelationRepository rep;

	@Autowired
	private PersonRepository perRep;

	@Autowired
	private PagedResourcesAssembler<Relation> ass;
	
	@Autowired
	private ClubRepository clubRep;

	@Override
	public PagedResources<PersistentEntityResource> getRelations(@PathVariable("id") String id,
			PersistentEntityResourceAssembler assx) {
		return ass.toResource(new PageImpl<>(rep.findByPerson(id)),
				new ResourceAssembler<Relation, PersistentEntityResource>() {
					@Override
					public PersistentEntityResource toResource(Relation entity) {
						return assx.toFullResource(entity);
					}
				});
	}
	
	@Override
	public void fixRoles(Person p) {
		Set<MemberRole> s = new HashSet<>();
		for (TeamMembership tm : p.getTeammemberships()) {
			s.addAll(tm.getRoles());
		}
		if (!s.equals(p.getRoles())) {
			p.setRoles(s);
			perRep.save(p);
		}
	}
}
