package de.beckers.members.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.QueryParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.beckers.members.model.Club;
import de.beckers.members.model.Person;
import de.beckers.members.model.Registration;
import de.beckers.members.model.Team;
import de.beckers.members.model.TeamMembership;
import de.beckers.members.repository.ClubRepository;
import de.beckers.members.repository.PersonRepository;
import de.beckers.members.repository.RegistrationRepository;
import de.beckers.members.repository.RelationRepository;
import de.beckers.members.repository.TeamRepository;
import lombok.Data;

@RequestMapping("/api")
@RestController
public class SearchServiceImpl {
	@Data
	public static class SearchResult {
		private String category;
		
		private String id;
		
		private String text;
	}
	
	@Autowired
	private RegistrationRepository regRep;
	
	@Autowired
	private PersonRepository perRep;
	
	@Autowired
	private TeamRepository tRep;
	
	@Autowired
	private ClubRepository cRep;
	
	@Transactional
    @RequestMapping(method = RequestMethod.GET, value = "/person/{id}/addAsMember")
	public void addMember(@PathVariable("id") String id) {
		Page<Club> clubs = cRep.findByDefaultFlag(true, null);
		Club club = clubs.getContent().get(0);
		Person person = perRep.findOne(id);
		person.setClub(club);
		perRep.save(person);
	}

	@Transactional
    @RequestMapping(method = RequestMethod.GET, value = "/person/{id}/removeMember")
	public void removeMember(@PathVariable("id") String id) {
		Person person = perRep.findOne(id);
		person.setClub(null);
		person.getTeammemberships().clear();
		person.getRoles().clear();
		perRep.save(person);
	}

	@Transactional
    @RequestMapping(method = RequestMethod.GET, value = "/person/{id}/fix")
	public void fixMember(@PathVariable("id") String id) {
		Person person = perRep.findOne(id);
		Set<TeamMembership> ms = person.getTeammemberships();
		person.getRoles().clear();
		for (TeamMembership m : ms) {
			person.getRoles().addAll(m.getRoles());
		}
		perRep.save(person);
	}

	@RequestMapping("/search")
	public List<SearchResult> search(@QueryParam("q") String q, @QueryParam("c") String cat) {
		List<SearchResult> res = new ArrayList<>();
		List<Registration> regs = regRep.findByNameContainingIgnoreCase(q);
		for (Registration reg : regs) {
			SearchResult x = new SearchResult();
			x.setCategory("registration");
			x.setId(reg.getId());
			x.setText(reg.getName() + ", " + reg.getFirstName());
			res.add(x);
		}
		List<Person> pers = perRep.findByNameContainingIgnoreCase(q);
		for (Person p : pers) {
			Club c = p.getClub();
			SearchResult x = new SearchResult();
			x.setCategory(c == null || !c.getDefaultFlag() ? "person" : "member");
			x.setId(p.getId());
			x.setText(p.getName() + ", " + p.getFirstName());
			res.add(x);
		}
		List<Team> ts = tRep.findByNameContainingIgnoreCase(q);
		for (Team team : ts) {
			SearchResult x = new SearchResult();
			x.setCategory("team");
			x.setId(team.getId());
			x.setText(team.getName());
			res.add(x);
		}
		List<Club> cls = cRep.findByNameContainingIgnoreCase(q);
		for (Club club : cls) {
			SearchResult x = new SearchResult();
			x.setCategory("club");
			x.setId(club.getId());
			x.setText(club.getName());
			res.add(x);
		}
		return res;
		
	}
}
