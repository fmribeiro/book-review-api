package com.resenha.microserviceresenha.data.repositories;

import com.resenha.microserviceresenha.data.model.BookReadStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "livros-status", path = "livros-status")
public interface BookReadStatusRepository extends MongoRepository<BookReadStatus, String> {

}
