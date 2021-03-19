package com.resenha.microserviceresenha.data.repositories;

import com.resenha.microserviceresenha.data.model.Book;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface BookRepository extends MongoRepository<Book, String>{

    @Query(value = "{'title': {$regex : ?0, $options: 'i'}}")
    List<Book> findByTitleRegexOrderByIdDesc(String title);
    List<Book> findByUserIdOrderByIdDesc(ObjectId userId);

}