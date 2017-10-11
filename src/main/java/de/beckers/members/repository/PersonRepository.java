package de.beckers.members.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import de.beckers.members.model.MemberRole;
import de.beckers.members.model.Person;

@RepositoryRestResource(collectionResourceRel = "person", path = "person")
public interface PersonRepository extends PagingAndSortingRepository<Person, String> {
	@Query("select p from Person p where p.active = true and p.club.defaultFlag = true and 0 member of p.roles")
	Page<Person> findActive(Pageable page);

	@Query("select p from Person p, TeamMembership m where p = m.person and m.team.id = :teamId and p.active = true and 0 member of m.roles")
	Page<Person> findActiveInTeam(@Param("teamId") String teamId, Pageable page);

	@Query("select p from Person p where p.club.defaultFlag = true and :role member of p.roles")
	Page<Person> findByRole(@Param("role") MemberRole role, Pageable page);

	@Query("select p from Person p, TeamMembership m where p = m.person and m.team.id = :teamId and :role member of m.roles")
	Page<Person> findInTeamByRole(@Param("teamId") String teamId, @Param("role") MemberRole role, Pageable page);

	@Query("select p from Person p where p.active = false and p.club.defaultFlag = true and 0 member of p.roles")
	Page<Person> findInActive(Pageable page);

	@Query("select p from Person p, TeamMembership m where p = m.person and m.team.id = :teamId and p.active = false and 0 member of m.roles")
	Page<Person> findInActiveInTeam(@Param("teamId") String teamId, Pageable page);

	@Query("select p from Person p where p.active = false and p.club.defaultFlag = true and size(p.roles) = 0")
	Page<Person> findNoRole(Pageable page);

	@Query("select p from Person p where p in (select r.person2 from Relation r where r.person1.active = true and r.person1.club.defaultFlag = true and 0 member of r.person1.roles) or p in (select r.person1 from Relation r where r.person2.active = true and r.person2.club.defaultFlag = true and 0 member of r.person2.roles)")
	Page<Person> findParents(Pageable page);

	@Query("select p from Person p where p in (select r.person2 from Relation r, TeamMembership m where r.person1 = m.person and m.team.id = :teamId and r.person1.active = true and 0 member of m.roles) or p in (select r.person1 from Relation r, TeamMembership m where r.person2 = m.person and m.team.id = :teamId and r.person2.active = true and 0 member of m.roles)")
	Page<Person> findParentsInTeam(@Param("teamId") String teamId, Pageable page);

	List<Person> findByNameAndFirstName(String name, String firstName);

	Person findById(@Param("id") String id);

	@Query("select p from Person p where concat(p.firstName, ' ', p.name) = :name")
	Person findByFullName(@Param("name") String name);

	List<Person> findByNameContainingIgnoreCase(String q);
}
