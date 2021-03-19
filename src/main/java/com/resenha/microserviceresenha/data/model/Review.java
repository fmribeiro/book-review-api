package com.resenha.microserviceresenha.data.model;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "reviews")
@JsonComponent
@Setter
@Getter
public class Review {

    @Id
    private String id;

    private String review;

    private ObjectId userId;

    private String bookTitle;

    private Integer likes = 0;

}