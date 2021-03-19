package com.resenha.microserviceresenha.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "books")
@JsonComponent
@Setter
@Getter
public class Book {

    @Id
    private String id;

    @JsonProperty
    private String title;

    @JsonProperty
    private String isbn;

    @JsonProperty()
    private Integer pageCount;

    @JsonProperty
    private String[] authors;

    @JsonProperty()
    private ObjectId userId;

    @JsonProperty()
    private String readStatus;

    @JsonProperty()
    private String imagePath;

    @JsonProperty()
    private String description;

    @JsonProperty()
    private String publisher;
}
