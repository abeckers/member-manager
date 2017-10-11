package de.beckers.members.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import de.beckers.members.model.Password;

@RepositoryRestResource(collectionResourceRel = "password", path = "password")
public interface PasswordRepository extends PagingAndSortingRepository<Password, String> {

	Password findByPersonId(String personId);

}
