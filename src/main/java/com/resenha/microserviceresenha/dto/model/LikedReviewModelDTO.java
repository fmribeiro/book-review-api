package com.resenha.microserviceresenha.dto.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.boot.jackson.JsonComponent;

import java.time.LocalDateTime;

@Getter
@JsonComponent
public class LikedReviewModelDTO {
    @JsonProperty
    private String id;
    @JsonProperty
    private String reviewId;
    @JsonProperty
    private String userId;
    @JsonProperty
    private LocalDateTime insertDate;
}
