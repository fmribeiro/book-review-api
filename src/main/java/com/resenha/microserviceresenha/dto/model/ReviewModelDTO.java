package com.resenha.microserviceresenha.dto.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@JsonComponent
@Setter
@Getter
public class ReviewModelDTO {

    @Id
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
    private Integer likes = 0;
}
