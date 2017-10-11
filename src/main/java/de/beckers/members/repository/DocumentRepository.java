package de.beckers.members.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import de.beckers.members.model.Document;

@RepositoryRestResource(collectionResourceRel = "document", path = "document")
public interface DocumentRepository extends PagingAndSortingRepository<Document, String> {

}
