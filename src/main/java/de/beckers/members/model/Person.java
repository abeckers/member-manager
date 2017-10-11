package de.beckers.members.model;

import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Period;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true, exclude = { "club", "documents", "teammemberships", "password", "trainings", "relations" })
public class Person extends ANamedEntity {
	@ManyToOne
	private Club club;

	@ElementCollection
	@Enumerated
	@CollectionTable(name = "Club_roles", joinColumns = @JoinColumn(name = "ClubMembership_id"))
	private Set<MemberRole> roles;

	@NotNull
	@Size(max = 32)
	private String firstName;

	@Embedded
	private Contact contact;

	private LocalDate birthDate;

	@Size(max = 255)
	private String birthPlace;

	@Size(max = 32)
	private String nationality;

	@Size(max = 255)
	private String note;

	@Enumerated
	private Sex sex;

	private Boolean active;

	@OneToMany(mappedBy = "person")
	private Set<Document> documents;

	@OneToMany(mappedBy = "person")
	private Set<TeamMembership> teammemberships;

	@OneToOne(mappedBy = "person")
	private Password password;

	@OneToMany(mappedBy = "person")
	private Set<TrainingParticipation> trainings;
	
	@Transient
	public String getFullAddress() {
		return contact == null ? null : contact.getFullAddress();
	}
	
	@Transient
	public String getFullName() {
		return getName() + ", " + getFirstName();
	}

	@Transient
	public String getStreet() {
		return contact == null ? null : contact.getAddress();
	}

	@Transient
	public String getZip() {
		return contact == null ? null : contact.getZip();
	}

	@Transient
	public String getPlace() {
		return contact == null ? null : contact.getPlace();
	}

	@Transient
	public LocalDate getEntry() {
		Set<Document> docs = getDocuments();
		if (docs != null) {
			for (Document document : docs) {
				if (document.getType() == DocumentType.REGISTRATION) {
					return document.getIssueDate();
				}
			}
		}
		return null;
	}

	public boolean hasPhoto() {
		Set<Document> docs = getDocuments();
		if (docs != null) {
			for (Document document : docs) {
				if (document.getType() == DocumentType.PHOTO) {
					return true;
				}
			}
		}
		return false;
	}

	@Transient
	public String getIdState() {
		if (hasId()) {
			return "x";
		}
		if (hasExpiredId()) {
			return "(x)";
		}
		return "";
	}

	@Transient
	public String getPassState() {
		if (hasPass()) {
			return "x";
		}
		return "";
	}

	private boolean hasId() {
		Set<Document> docs = getDocuments();
		if (docs != null) {
			for (Document doc : docs) {
				if (doc.getType() == DocumentType.ID && isValid(doc)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean hasPass() {
		Set<Document> docs = getDocuments();
		if (docs != null) {
			for (Document doc : docs) {
				if (doc.getType() == DocumentType.PASS && isValid(doc)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean hasExpiredId() {
		Set<Document> docs = getDocuments();
		if (docs != null) {
			for (Document doc : docs) {
				if (doc.getType() == DocumentType.ID && isExpired(doc)) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean isValid(Document doc) {
		return doc.getIssueDate().isBefore(new LocalDate())
				&& (doc.getExpireDate() == null || doc.getExpireDate().isAfter(new LocalDate()));
	}

	private static boolean isExpired(Document doc) {
		return doc.getExpireDate() != null && doc.getExpireDate().isBefore(new LocalDate());
	}

	public String getCertificateFlag() {
		if (hasCertificate()) {
			return "x";
		}
		if (!needsCert()) {
			return "-";
		}
		return null;
	}

	public String getAttachmentFlag() {
		if (hasAttachment()) {
			return "x";
		}
		if (!needsAttachment()) {
			return "-";
		}
		return null;
	}
	
	public boolean hasCertificate() {
		Set<Document> docs = getDocuments();
		if (docs != null) {
			for (Document doc : docs) {
				if (doc.getType() == DocumentType.CERTIFICATE && isValid(doc)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean needsCert() {
		if (getBirthDate() != null) {
			DateTime now = new DateTime();
			DateTime lt = getBirthDate().toDateTime(new LocalTime(DateTimeZone.forID("Europe/Berlin")));
			Period p = new Period(lt, now);
			return p.getYears() < 18;
		}
		return false;
	}

	public boolean hasAttachment() {
		Set<Document> docs = getDocuments();
		if (docs != null) {
			for (Document doc : docs) {
				if (doc.getType() == DocumentType.ATTACHMENT && isValid(doc)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean needsAttachment() {
		Set<TeamMembership> tms = getTeammemberships();
		if (tms == null || tms.isEmpty()) {
			return false;
		}
		TeamMembership tm = getTeammemberships().iterator().next();
		Integer minYear = tm.getTeam().getMinYear();
		if (getBirthDate() != null) {
			int year = getBirthDate().getYear();
			if (minYear == year) {
				return true; 
			}
		}
		return false;
	}
}
