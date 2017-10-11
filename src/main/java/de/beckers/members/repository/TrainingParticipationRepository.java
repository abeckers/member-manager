package de.beckers.members.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import de.beckers.members.model.TrainingParticipation;

@RepositoryRestResource(collectionResourceRel = "trainingparticipation", path = "trainingparticipation")
public interface TrainingParticipationRepository extends PagingAndSortingRepository<TrainingParticipation, String> {

}
