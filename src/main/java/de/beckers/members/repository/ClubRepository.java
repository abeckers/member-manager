package de.beckers.members.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.Description;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import de.beckers.members.model.Club;

@RepositoryRestResource(collectionResourceRel = "club", path = "club", collectionResourceDescription = @Description("The Clubs") )
public interface ClubRepository extends PagingAndSortingRepository<Club, String> {
	Page<Club> findByDefaultFlag(@Param("default") Boolean defaultFlag, Pageable page);

	Club findById(@Param("id") String id);

	List<Club> findByNameContainingIgnoreCase(String q);
}
