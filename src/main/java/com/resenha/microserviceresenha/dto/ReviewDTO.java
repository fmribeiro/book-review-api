package com.resenha.microserviceresenha.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.resenha.microserviceresenha.dto.model.UserModelDTO;
import org.springframework.boot.jackson.JsonComponent;

import java.time.LocalDateTime;

@JsonComponent
public class ReviewDTO {

    @JsonProperty
    private String id;

    @JsonProperty
    private String review;

    @JsonProperty()
    private LocalDateTime insertDate;

    @JsonProperty()
    private LocalDateTime updateDate;

    @JsonProperty()
    private String userId;

    @JsonProperty()
    private String bookTitle;

    @JsonProperty()
    private Integer likes;

    @JsonProperty
    private UserModelDTO user;
}
