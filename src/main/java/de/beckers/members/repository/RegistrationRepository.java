package de.beckers.members.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import de.beckers.members.model.Registration;
import de.beckers.members.model.RegistrationState;

@RepositoryRestResource(collectionResourceRel = "registration", path = "registration")
public interface RegistrationRepository extends PagingAndSortingRepository<Registration, String> {
	Page<Registration> findByState(@Param("state") RegistrationState state, Pageable page);

	Registration findById(@Param("id") String id);

	List<Registration> findByNameContainingIgnoreCase(String q);
}
