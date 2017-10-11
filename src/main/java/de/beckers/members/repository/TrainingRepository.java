package de.beckers.members.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import de.beckers.members.model.Training;

@RepositoryRestResource(collectionResourceRel = "training", path = "training")
public interface TrainingRepository extends PagingAndSortingRepository<Training, String> {
	Page<Training> findByGroupId(@Param("groupId") String groupId, Pageable page);

	Training findById(@Param("id") String id);
}
