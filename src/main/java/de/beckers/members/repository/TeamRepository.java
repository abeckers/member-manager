package de.beckers.members.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import de.beckers.members.model.Team;

@RepositoryRestResource(collectionResourceRel = "team", path = "team")
public interface TeamRepository extends PagingAndSortingRepository<Team, String> {
    Page<Team> findByClubDefaultFlag(@Param("default") Boolean defaultFlag, Pageable page);

    Team findById(@Param("id") String id);

	List<Team> findByNameContainingIgnoreCase(String q);
}
