package com.resenha.microserviceresenha.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.boot.jackson.JsonComponent;

import java.time.LocalDateTime;

@JsonComponent
public class BookDTO {

    @JsonProperty
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
    private String userId;

    @JsonProperty()
    private String readStatus;

    @JsonProperty()
    private String imagePath;

    @JsonProperty()
    private String description;

    @JsonProperty()
    private String publisher;

    @JsonProperty()
    private LocalDateTime insertDate;

    @JsonProperty()
    private LocalDateTime updateDate;

    @JsonProperty()
    private UserDTO user;
}
