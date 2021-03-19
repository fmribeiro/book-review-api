package com.resenha.microserviceresenha.data.repositories;

import com.resenha.microserviceresenha.data.model.LikedReview;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LikedReviewRepository extends MongoRepository<LikedReview, String> {

    LikedReview findByReviewIdAndUserId(ObjectId reviewId, ObjectId userId);

}