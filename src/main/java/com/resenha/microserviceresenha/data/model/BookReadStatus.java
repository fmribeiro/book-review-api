package com.resenha.microserviceresenha.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "bookReadStatus")
@JsonComponent
@Setter
@Getter
public class BookReadStatus {

    @Id
    @JsonProperty
    private String id;
    @JsonProperty
    private String status;
}
