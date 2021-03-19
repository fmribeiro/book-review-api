package com.resenha.microserviceresenha.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "likedReviews")
@JsonComponent
@Setter
@Getter
public class LikedReview {

    @Id
    private String id;
    @JsonProperty
    private ObjectId reviewId;
    @JsonProperty
    private ObjectId userId;
}
