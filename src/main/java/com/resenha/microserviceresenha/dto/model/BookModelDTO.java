package com.resenha.microserviceresenha.dto.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.jackson.JsonComponent;

import java.time.LocalDateTime;

@JsonComponent
@Setter
@Getter
public class BookModelDTO {

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
}
