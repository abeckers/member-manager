package de.beckers.members.api;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import de.beckers.members.api.PrepareResult.MatchingParent;
import de.beckers.members.api.PrepareResult.NewParent;
import de.beckers.members.api.PrepareResult.TeamInfo;
import de.beckers.members.api.RegistrationInfo.ParentState;
import de.beckers.members.model.Club;
import de.beckers.members.model.Contact;
import de.beckers.members.model.Document;
import de.beckers.members.model.DocumentType;
import de.beckers.members.model.MemberRole;
import de.beckers.members.model.Person;
import de.beckers.members.model.Registration;
import de.beckers.members.model.Relation;
import de.beckers.members.model.RelationType;
import de.beckers.members.model.Team;
import de.beckers.members.model.TeamMembership;
import de.beckers.members.repository.ClubRepository;
import de.beckers.members.repository.DocumentRepository;
import de.beckers.members.repository.PersonRepository;
import de.beckers.members.repository.RegistrationRepository;
import de.beckers.members.repository.RelationRepository;
import de.beckers.members.repository.TeamMembershipRepository;
import de.beckers.members.repository.TeamRepository;

@RepositoryRestController
@ResponseBody
public class RegistrationServiceImpl implements RegistrationService {
	@Autowired
	private RegistrationRepository rep;

	@Autowired
	private RelationRepository relRep;

	@Autowired
	private ClubRepository clubRep;

	@Autowired
	private TeamRepository teamRep;

	@Autowired
	private PersonRepository perRep;

	@Autowired
	private DocumentRepository docRep;

	@Autowired
	private TeamMembershipRepository tmRep;

	@Override
	public StringValue acceptRegistration(@PathVariable("id") String id, @RequestBody RegistrationInfo info) {
		Registration reg = rep.findOne(id);
		Person member = extractPerson(reg);
		member.setClub(getDefaultClub());
		member.setRoles(Collections.singleton(MemberRole.PLAYER));
		Person parent1 = evalParent1(info, reg);
		Person parent2 = evalParent2(info, reg);
		member = perRep.save(member);
		createRelation(member, parent1);
		createRelation(member, parent2);
		addTeamMembershipAsPlayer(member, info.getTeamId());
		addRegistrationDate(member, info.getRegistrationDate());
		rep.delete(reg);
		return new StringValue(member.getId());
	}

	private void addRegistrationDate(Person member, LocalDate registrationDate) {
		Document doc = new Document();
		doc.setType(DocumentType.REGISTRATION);
		doc.setIssueDate(registrationDate);
		String prefix = member.getName() + " " + member.getFirstName();
		if (prefix.length() > 28) {
			prefix = prefix.substring(0, 28);
		}
		doc.setName(prefix + " REG");
		doc.setPerson(member);
		docRep.save(doc);
	}

	private void addTeamMembershipAsPlayer(Person member, String teamId) {
		if (teamId == null) {
			return;
		}
		Team team = teamRep.findOne(teamId);
		TeamMembership membership = new TeamMembership();
		membership.setPerson(member);
		membership.setTeam(team);
		membership.setRoles(Collections.singletonList(MemberRole.PLAYER));
		tmRep.save(membership);
	}

	private Person evalParent2(RegistrationInfo info, Registration reg) {
		return evalParent(info.getState2(), extractParent2(reg), info.getParent2Id());
	}

	private Person evalParent1(RegistrationInfo info, Registration reg) {
		return evalParent(info.getState1(), extractParent1(reg), info.getParent1Id());
	}

	private Person evalParent(ParentState state, NewParent parent, String parent1Id) {
		Person parent1 = null;
		switch (state) {
		case NEW:
			parent1 = createNewPerson(parent);
			break;
		case SET:
			parent1 = mergePerson(perRep.findOne(parent1Id), parent);
			break;
		default:
			break;
		}
		return parent1;
	}

	private void createRelation(Person player, Person parent) {
		if (parent == null) {
			return;
		}
		Relation rel = new Relation();
		rel.setPerson1(player);
		rel.setPerson2(parent);
		rel.setType(RelationType.CHILD);
		relRep.save(rel);
	}

	private Person mergePerson(Person person, NewParent parent) {
		Contact contact = person.getContact();
		if (contact == null) {
			contact = new Contact();
			person.setContact(contact);
		}
		contact.setMobile(parent.getMobile());
		contact.setEmail(parent.getEmail());
		perRep.save(person);
		return person;
	}

	private Person extractPerson(Registration reg) {
		Person p = new Person();
		p.setActive(true);
		p.setName(reg.getName());
		p.setFirstName(reg.getFirstName());
		p.setBirthDate(reg.getBirthDate());
		p.setBirthPlace(reg.getBirthPlace());
		p.setNationality(reg.getNationality());
		p.setNote(reg.getNotes());
		p.setSex(reg.getSex());
		Contact c = new Contact();
		c.setAddress(reg.getStreet());
		c.setEmail(reg.getEmail());
		c.setMobile(reg.getMobile());
		c.setPhone(reg.getPhone());
		c.setPlace(reg.getPlace());
		c.setZip(reg.getZip());
		p.setContact(c);
		return p;
	}

	private Person createNewPerson(NewParent parent) {
		Person p = new Person();
		p.setName(parent.getName());
		p.setFirstName(parent.getFirstName());
		Contact c = new Contact();
		c.setEmail(parent.getEmail());
		c.setMobile(parent.getMobile());
		p.setContact(c);
		perRep.save(p);
		return p;
	}

	private Club getDefaultClub() {
		Page<Club> page = clubRep.findByDefaultFlag(Boolean.TRUE, new PageRequest(0, 1));
		return page.getContent().get(0);
	}

	@Override
	public PrepareResult prepareRegistration(@PathVariable("id") String id) {
		PrepareResult res = new PrepareResult();
		Registration reg = rep.findOne(id);
		if (reg.hasParent1()) {
			res.setNewParent1(extractParent1(reg));
			res.setMatchingParent1(findMatchingParent(res.getNewParent1()));
		}
		if (reg.hasParent2()) {
			res.setNewParent2(extractParent2(reg));
			res.setMatchingParent2(findMatchingParent(res.getNewParent2()));
		}
		res.setTeams(findTeamsFitting(getBirthYear(reg)));
		return res;
	}

	private MatchingParent findMatchingParent(NewParent newParent) {
		List<Person> persons = perRep.findByNameAndFirstName(newParent.getName(), newParent.getFirstName());
		if (!persons.isEmpty()) {
			return extractParent(persons.get(0));
		}
		return null;
	}

	private MatchingParent extractParent(Person p0) {
		MatchingParent p = new MatchingParent();
		p.setId(p0.getId());
		p.setName(p0.getName());
		p.setFirstName(p0.getFirstName());
		if (p0.getContact() != null) {
			p.setMobile(p0.getContact().getMobile());
			p.setEmail(p0.getContact().getEmail());
		}
		return p;
	}

	private Set<TeamInfo> findTeamsFitting(int birthYear) {
		Page<Team> tp = teamRep.findByClubDefaultFlag(true, new PageRequest(0, 1000));
		Set<TeamInfo> ts = new HashSet<>();
		tp.forEach(t -> {
			if (isFitting(t, birthYear)) {
				ts.add(new TeamInfo(t.getId(), t.getName()));
			}
		});
		return ts;
	}

	private boolean isFitting(Team t, int birthYear) {
		return t.getMaxYear() <= birthYear && t.getMinYear() >= birthYear;
	}

	private int getBirthYear(Registration reg) {
		return reg.getBirthDate().getYear();
	}

	private NewParent extractParent1(Registration reg) {
		NewParent p = new NewParent();
		p.setName(reg.getNameParent());
		p.setFirstName(reg.getFirstNameParent());
		p.setMobile(reg.getMobileParent());
		p.setEmail(reg.getEmailParent());
		return p;
	}

	private NewParent extractParent2(Registration reg) {
		NewParent p = new NewParent();
		p.setName(reg.getNameParent2());
		p.setFirstName(reg.getFirstNameParent2());
		p.setMobile(reg.getMobileParent2());
		p.setEmail(reg.getEmailParent2());
		return p;
	}
}
