package com.resenha.microserviceresenha.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.resenha.microserviceresenha.dto.model.UserModelDTO;
import lombok.Getter;
import org.springframework.boot.jackson.JsonComponent;

import java.time.LocalDateTime;

@JsonComponent
@Getter
public class LikedReviewDTO {
    @JsonProperty
    private String id;

    @JsonProperty
    private String reviewId;

    @JsonProperty
    private String userId;

    @JsonProperty
    private LocalDateTime insertDate;

    @JsonProperty
    private ReviewDTO review;

    @JsonProperty
    private UserModelDTO user;
}
