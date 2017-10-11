package de.beckers.members;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.beckers.members.model.Club;
import de.beckers.members.model.Contact;
import de.beckers.members.model.DocumentType;
import de.beckers.members.model.MemberRole;
import de.beckers.members.model.Password;
import de.beckers.members.model.Person;
import de.beckers.members.model.Registration;
import de.beckers.members.model.RegistrationState;
import de.beckers.members.model.Relation;
import de.beckers.members.model.RelationType;
import de.beckers.members.model.Sex;
import de.beckers.members.model.Team;
import de.beckers.members.model.TeamMembership;
import de.beckers.members.model.Password.Type;
import de.beckers.members.repository.ClubRepository;
import de.beckers.members.repository.DocumentRepository;
import de.beckers.members.repository.PasswordRepository;
import de.beckers.members.repository.PersonRepository;
import de.beckers.members.repository.RegistrationRepository;
import de.beckers.members.repository.RelationRepository;
import de.beckers.members.repository.TeamMembershipRepository;
import de.beckers.members.repository.TeamRepository;

@RestController
public class Importer {
	@Autowired
	private ClubRepository clubRep;

	@Autowired
	private RegistrationRepository regRep;

	@Autowired
	private PersonRepository perRep;

	@Autowired
	private TeamRepository teamRep;

	@Autowired
	private PasswordRepository pwRep;

	@Autowired
	private DocumentRepository docRep;

	@Autowired
	private RelationRepository relRep;

	@Autowired
	private TeamMembershipRepository tmRep;

	@Value("${jets.import.file.name}")
	private String importFileName;

	private Map<String, String> idMap = new HashMap<>();

	private XPathFactory xp = XPathFactory.instance();

	private PrintWriter out;

	@RequestMapping(value = "/import", method = RequestMethod.GET)
	public void doImport(HttpServletResponse resp) throws IOException {
		out = resp.getWriter();
		try {
			SAXBuilder r = new SAXBuilder();
			Document doc = r.build(importFileName);
			importRegistrations(doc);
			importClubs(doc);
			importPersons(doc);
			importClubMemberships(doc);
			importClubMembershipRoles(doc);
			importTeams(doc);
			importTeamMemberships(doc);
			importTeamMembershipRoles(doc);
			importDocuments(doc);
			importPasswords(doc);
			importRelations(doc);
			importTasks(doc);
		} catch (JDOMException | IOException | ParseException e) {
			e.printStackTrace(out);
		}
	}

	private void importTeamMembershipRoles(Document doc) {
		XPathExpression<Element> exp = xp.compile("//table_data[@name='TeamMembership_roles']/row", Filters.element());
		List<Element> elements = exp.evaluate(doc);
		for (Element element : elements) {
			importTeamMembershipRole(element);
		}
	}

	private void importTeamMembershipRole(Element element) {
		List<Element> fields = element.getChildren("field");
		String cmid = null;
		MemberRole mr = null;
		for (Element field : fields) {
			String val = field.getTextTrim();
			String name = field.getAttributeValue("name");
			switch (name) {
			case "TeamMembership_id":
				cmid = getId("TeamMembership", val);
				break;
			case "roles":
				int idx = Integer.parseInt(val);
				switch (idx) {
				case 0:
					mr = MemberRole.COACH;
					break;
				case 1:
					mr = MemberRole.STAFF;
					break;
				case 2:
					mr = MemberRole.PLAYER;
					break;
				default:
					System.err.println("no role for " + idx);
					return;

				}
				break;
			}
		}
		if (cmid != null) {
			TeamMembership tm = tmRep.findOne(cmid);
			Person person = tm.getPerson();
			Set<MemberRole> cr = person.getRoles();
			if (cr == null) {
				cr = new HashSet<>();
				person.setRoles(cr);
			}
			cr.add(mr);
			List<MemberRole> roles = tm.getRoles();
			if (roles == null) {
				roles = new ArrayList<>();
				tm.setRoles(roles);
			}
			roles.add(mr);
			perRep.save(person);
			tmRep.save(tm);
		}
	}

	private void importTeamMemberships(Document doc) {
		XPathExpression<Element> exp = xp.compile("//table_data[@name='TeamMembership']/row", Filters.element());
		List<Element> elements = exp.evaluate(doc);
		for (Element element : elements) {
			importTeamMembership(element);
		}
	}

	private void importTeamMembership(Element element) {
		List<Element> fields = element.getChildren("field");
		String oldId = null;
		Person person = null;
		Team club = null;
		for (Element field : fields) {
			String val = field.getTextTrim();
			String name = field.getAttributeValue("name");
			switch (name) {
			case "id":
				oldId = val;
				break;
			case "team_id":
				club = teamRep.findOne(getId("team", val));
				break;
			case "person_id":
				String pid = getId("person", val);
				if (pid == null) {
					System.err.println("can't find person " + val);
					return;
				}
				person = perRep.findOne(pid);
				break;
			}
		}
		if (person != null && club != null) {
			TeamMembership tm = new TeamMembership();
			tm.setPerson(person);
			tm.setTeam(club);
			TeamMembership n = tmRep.save(tm);
			saveId("TeamMembership", oldId, n.getId());
		}
	}

	private void importTeams(Document doc) {
		XPathExpression<Element> exp = xp.compile("//table_data[@name='Team']/row", Filters.element());
		List<Element> elements = exp.evaluate(doc);
		for (Element element : elements) {
			importTeam(element);
		}
	}

	private void importTeam(Element element) {
		Team c = new Team();
		String oldId = null;
		List<Element> fields = element.getChildren("field");
		for (Element field : fields) {
			String val = field.getTextTrim();
			switch (field.getAttributeValue("name")) {
			case "id":
				oldId = val;
				break;
			case "name":
				c.setName(val);
				break;
			case "maxYear":
				c.setMaxYear(Integer.valueOf(val));
				break;
			case "mixYear":
				c.setMinYear(Integer.valueOf(val));
				break;
			case "sex":
				c.setSex(Sex.values()[val.isEmpty() ? 0 : Integer.parseInt(val)]);
				break;
			case "club_id":
				c.setClub(clubRep.findOne(getId("club", val)));
				break;
			}
		}
		Team cn = teamRep.save(c);
		saveId("team", oldId, cn.getId());
		out.println("team imported: " + cn.getName());
	}

	private String getId(String type, String val) {
		return idMap.get(type + "#" + val);
	}

	private void importTasks(Document doc) {
		// TODO Auto-generated method stub

	}

	private void importRelations(Document doc) {
		XPathExpression<Element> exp = xp.compile("//table_data[@name='Relation']/row", Filters.element());
		List<Element> elements = exp.evaluate(doc);
		for (Element element : elements) {
			importRelation(element);
		}
	}

	private void importRelation(Element element) {
		Relation c = new Relation();
		String oldId = null;
		List<Element> fields = element.getChildren("field");
		for (Element field : fields) {
			String val = field.getTextTrim();
			switch (field.getAttributeValue("name")) {
			case "id":
				oldId = val;
				break;
			case "person1_id":
				String p1id = getId("person", val);
				if (p1id == null) {
					System.err.println("can't find person " + val);
					return;
				}
				c.setPerson1(perRep.findOne(p1id));
				break;
			case "person2_id":
				String p2id = getId("person", val);
				if (p2id == null) {
					System.err.println("can't find person " + val);
					return;
				}
				c.setPerson2(perRep.findOne(p2id));
				break;
			case "type":
				c.setType(RelationType.values()[Integer.parseInt(val)]);
				break;
			}
		}
		Relation cn = relRep.save(c);
		saveId("relation", oldId, cn.getId());
	}

	private void importRegistrations(Document doc) throws ParseException {
		XPathExpression<Element> exp = xp.compile("//table_data[@name='Registration']/row", Filters.element());
		List<Element> clubElements = exp.evaluate(doc);
		for (Element ce : clubElements) {
			importRegistration(ce);
		}
	}

	private void importRegistration(Element ce) throws ParseException {
		Registration c = new Registration();
		String oldId = null;
		List<Element> fields = ce.getChildren("field");
		for (Element field : fields) {
			String val = field.getTextTrim();
			switch (field.getAttributeValue("name")) {
			case "id":
				oldId = val;
				break;
			case "state":
				c.setState(RegistrationState.values()[Integer.parseInt(val)]);
				if (c.getState() == RegistrationState.NEW) {
					c.setState(RegistrationState.EMAIL_SENT);
				}
				break;
			case "name":
				c.setName(val);
				break;
			case "birthDate":
				c.setBirthDate(LocalDate.parse(val, ISODateTimeFormat.yearMonthDay()));
				break;
			case "birthPlace":
				c.setBirthPlace(val);
				break;
			case "sex":
				c.setSex(Sex.values()[Integer.parseInt(val)]);
				break;
			case "email":
				c.setEmail(val);
				break;
			case "emailParent":
				c.setEmailParent(val);
				break;
			case "firstName":
				c.setFirstName(val);
				break;
			case "iban":
				c.setIban(val);
				break;
			case "mobileParent":
				c.setMobileParent(val);
				break;
			case "nationality":
				c.setNationality(val);
				break;
			case "notes":
				c.setNotes(val);
				break;
			case "phone":
				c.setPhone(val);
				break;
			case "place":
				c.setPlace(val);
				break;
			case "profession":
				c.setProfession(val);
				break;
			case "street":
				c.setStreet(val);
				break;
			case "team":
				break;
			case "type":
				c.setType(val);
				break;
			case "zip":
				c.setZip(val);
				break;
			case "nameParent":
				c.setNameParent(val);
				break;
			case "firstNameParent":
				c.setFirstNameParent(val);
				break;
			case "nameParent2":
				c.setNameParent2(val);
				break;
			case "firstNameParent2":
				c.setFirstNameParent2(val);
				break;
			case "mobileParent2":
				c.setMobileParent2(val);
				break;
			case "mobile":
				c.setMobile(val);
				break;
			case "emailParent2":
				c.setEmailParent2(val);
				break;
			}
		}
		Registration cn = regRep.save(c);
		saveId("registration", oldId, cn.getId());
		out.println("registration imported: " + cn.getName());
	}

	private void importPersons(Document doc) throws ParseException {
		XPathExpression<Element> exp = xp.compile("//table_data[@name='Person']/row", Filters.element());
		List<Element> clubElements = exp.evaluate(doc);
		for (Element ce : clubElements) {
			importPerson(ce);
		}
	}

	private void importPerson(Element ce) throws ParseException {
		Person c = new Person();
		Contact cc = new Contact();
		c.setContact(cc);
		String oldId = null;
		List<Element> fields = ce.getChildren("field");
		for (Element field : fields) {
			String val = field.getTextTrim();
			switch (field.getAttributeValue("name")) {
			case "id":
				oldId = val;
				break;
			case "name":
				c.setName(val);
				break;
			case "active":
				c.setActive(val.equals("1"));
				break;
			case "birthDate":
				if (StringUtils.hasText(val)) {
					c.setBirthDate(LocalDate.parse(val, ISODateTimeFormat.yearMonthDay()));
				}
				break;
			case "birthPlace":
				c.setBirthPlace(val);
				break;
			case "address":
				cc.setAddress(val);
				break;
			case "zip":
				cc.setZip(val);
				break;
			case "place":
				cc.setPlace(val);
				break;
			case "email":
				cc.setEmail(val);
				break;
			case "fax":
				cc.setFax(val);
				break;
			case "mobile":
				cc.setMobile(val);
				break;
			case "phone":
				cc.setPhone(val);
				break;
			case "firstName":
				c.setFirstName(val);
				break;
			case "nationality":
				c.setNationality(val);
				break;
			case "note":
				c.setNote(val);
				break;
			case "sex":
				if (StringUtils.hasText(val)) {
					c.setSex(Sex.values()[Integer.parseInt(val)]);
				}
				break;
			}
		}
		Person cn = perRep.save(c);
		saveId("person", oldId, cn.getId());
		out.println("person imported: " + cn.getName());
	}

	private void importPasswords(Document doc) {
		XPathExpression<Element> exp = xp.compile("//table_data[@name='Password']/row", Filters.element());
		List<Element> elements = exp.evaluate(doc);
		for (Element element : elements) {
			importPassword(element);
		}
	}

	private void importPassword(Element element) {
		Password c = new Password();
		c.setType(Type.MD5);
		String oldId = null;
		List<Element> fields = element.getChildren("field");
		for (Element field : fields) {
			String val = field.getTextTrim();
			String name = field.getAttributeValue("name");
			switch (name) {
			case "id":
				oldId = val;
				break;
			case "password":
				c.setPassword(val);
				break;
			case "person_id":
				String newId = getId("person", val);
				Person p = perRep.findById(newId);
				if (p == null) {
					System.err.println("can't find person " + val + " " + newId);
				}
				c.setPerson(p);
				break;
			}
		}
		if (c.getPerson() != null) {
			Password cn = pwRep.save(c);
			saveId("password", oldId, cn.getId());
			out.println("password imported: " + cn.getPerson().getName());
		}
	}

	private void saveId(String type, String oldId, String newId) {
		System.out.println(type + " ->" + oldId + " = " + newId);
		idMap.put(type + "#" + oldId, newId);
	}

	private void importDocuments(Document doc) throws ParseException {
		XPathExpression<Element> exp = xp.compile("//table_data[@name='Document']/row", Filters.element());
		List<Element> elements = exp.evaluate(doc);
		for (Element element : elements) {
			importDocument(element);
		}
	}

	private void importDocument(Element element) throws ParseException {
		de.beckers.members.model.Document c = new de.beckers.members.model.Document();
		String oldId = null;
		List<Element> fields = element.getChildren("field");
		for (Element field : fields) {
			String val = field.getTextTrim();
			String name = field.getAttributeValue("name");
			switch (name) {
			case "id":
				oldId = val;
				break;
			case "name":
				c.setName(val);
				break;
			case "issueDate":
				c.setIssueDate(LocalDate.parse(val, ISODateTimeFormat.yearMonthDay()));
				break;
			case "type":
				c.setType(DocumentType.values()[Integer.parseInt(val)]);
				break;
			case "team_id":
				if (StringUtils.hasText(val)) {
					c.setTeam(teamRep.findOne(getId("team", val)));
				}
				break;
			case "person_id":
				if (StringUtils.hasText(val)) {
					String pid = getId("person", val);
					if (pid == null) {
						System.err.println("can't find person " + pid);
						return;
					}
					c.setPerson(perRep.findOne(pid));
				}
				break;
			case "expireDate":
				if (StringUtils.hasText(val)) {
					c.setExpireDate(LocalDate.parse(val, ISODateTimeFormat.yearMonthDay()));
				}
				break;
			}
		}
		de.beckers.members.model.Document cn = docRep.save(c);
		saveId("document", oldId, cn.getId());
		out.println("Document imported: " + cn.getName());
	}

	private void importClubMembershipRoles(Document doc) {
		XPathExpression<Element> exp = xp.compile("//table_data[@name='ClubMembership_roles']/row", Filters.element());
		List<Element> elements = exp.evaluate(doc);
		for (Element element : elements) {
			importClubMembershipRole(element);
		}
	}

	private void importClubMembershipRole(Element element) {
		List<Element> fields = element.getChildren("field");
		String cmid = null;
		MemberRole mr = null;
		for (Element field : fields) {
			String val = field.getTextTrim();
			String name = field.getAttributeValue("name");
			switch (name) {
			case "ClubMembership_id":
				cmid = getId("ClubMembership", val);
				break;
			case "roles":
				if (val.equals("YOUTHDIRECTOR") || val.equals("PRESIDENT")) {
					val = "STAFF";
				}
				if (val.equals("PARENT")) {
					return;
				}
				mr = MemberRole.valueOf(val);
				break;
			}
		}
		if (cmid != null) {
			Person person = perRep.findOne(cmid);
			Set<MemberRole> roles = person.getRoles();
			if (roles == null) {
				roles = new HashSet<>();
				person.setRoles(roles);
			}
			roles.add(mr);
			perRep.save(person);
		}
	}

	private void importClubMemberships(Document doc) {
		XPathExpression<Element> exp = xp.compile("//table_data[@name='ClubMembership']/row", Filters.element());
		List<Element> elements = exp.evaluate(doc);
		for (Element element : elements) {
			importClubMembership(element);
		}
	}

	private void importClubMembership(Element element) {
		List<Element> fields = element.getChildren("field");
		String oldId = null;
		Person person = null;
		Club club = null;
		for (Element field : fields) {
			String val = field.getTextTrim();
			String name = field.getAttributeValue("name");
			switch (name) {
			case "id":
				oldId = val;
				break;
			case "club_id":
				club = clubRep.findOne(getId("club", val));
				break;
			case "person_id":
				person = perRep.findOne(getId("person", val));
				break;
			}
		}
		if (person != null && club != null) {
			person.setClub(club);
			Person n = perRep.save(person);
			saveId("ClubMembership", oldId, n.getId());
		}
	}

	private void importClubs(Document doc) {
		XPathExpression<Element> exp = xp.compile("//table_data[@name='Club']/row", Filters.element());
		List<Element> elements = exp.evaluate(doc);
		for (Element element : elements) {
			importClub(element);
		}
	}

	private void importClub(Element element) {
		Club c = new Club();
		Contact cc = new Contact();
		c.setContact(cc);
		String oldId = null;
		List<Element> fields = element.getChildren("field");
		for (Element field : fields) {
			String val = field.getTextTrim();
			String name = field.getAttributeValue("name");
			switch (name) {
			case "id":
				c.setDefaultFlag("defaultClub".equals(val));
				oldId = val;
				break;
			case "name":
				c.setName(val);
				break;
			case "address":
				cc.setAddress(val);
				break;
			case "zip":
				cc.setZip(val);
				break;
			case "place":
				cc.setPlace(val);
				break;
			case "email":
				cc.setEmail(val);
				break;
			case "fax":
				cc.setFax(val);
				break;
			case "mobile":
				cc.setMobile(val);
				break;
			case "phone":
				cc.setPhone(val);
				break;
			}
		}
		Club cn = clubRep.save(c);
		saveId("club", oldId, cn.getId());
		out.println("club imported: " + cn.getName());
	}
}
