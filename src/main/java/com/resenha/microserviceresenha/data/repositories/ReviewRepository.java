package com.resenha.microserviceresenha.data.repositories;

import com.resenha.microserviceresenha.data.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ReviewRepository extends MongoRepository<Review, String> {

    @Query(value = "{'bookTitle': {$regex : ?0, $options: 'i'}}")
    List<Review> findByBookTitleRegex(String bookTitle);
}
