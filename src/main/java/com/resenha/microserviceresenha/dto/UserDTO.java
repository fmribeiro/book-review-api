package com.resenha.microserviceresenha.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.boot.jackson.JsonComponent;

import java.time.LocalDateTime;
import java.util.List;

@JsonComponent
public class UserDTO {

    @JsonProperty
    private String id;
    @JsonProperty
    private String name;
    @JsonProperty
    private String nickname;
    @JsonProperty
    private LocalDateTime insertDate;
    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<UserDTO> usersFollowing;
    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> following;
    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<BookDTO> books;
    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ReviewDTO> reviews;
}
