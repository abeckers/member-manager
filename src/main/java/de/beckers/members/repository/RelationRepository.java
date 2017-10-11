package de.beckers.members.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import de.beckers.members.model.Relation;

@RepositoryRestResource(collectionResourceRel = "relation", path = "relation")
public interface RelationRepository extends PagingAndSortingRepository<Relation, String> {
	@Query("select r from Relation r where r.person1.id = :personId or r.person2.id = :personId")
	List<Relation> findByPerson(@Param("personId") String id);
}
