package com.resenha.microserviceresenha.data.model;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "messages")
@Getter
@Setter
@JsonComponent
public class Message {

    @Id
    private String id;
    private String message;
    private ObjectId sender;
    private ObjectId receiver;
}
