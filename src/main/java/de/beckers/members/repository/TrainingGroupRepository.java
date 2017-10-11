package de.beckers.members.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import de.beckers.members.model.TrainingGroup;

@RepositoryRestResource(collectionResourceRel = "traininggroup", path = "traininggroup")
public interface TrainingGroupRepository extends PagingAndSortingRepository<TrainingGroup, String> {
	TrainingGroup findById(@Param("id") String id);
}
