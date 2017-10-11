package de.beckers.members.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import de.beckers.members.model.TeamMembership;

@RepositoryRestResource(collectionResourceRel = "teammembership", path = "teammembership")
public interface TeamMembershipRepository extends PagingAndSortingRepository<TeamMembership, String> {

}
