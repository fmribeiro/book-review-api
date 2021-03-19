package com.resenha.microserviceresenha.data.model;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "users")
@JsonComponent
@Setter
@Getter
public class User {

    @Id
    private String id;
    private String name;
    private String nickname;
    private String idToken;
    private List<ObjectId> following;

}
