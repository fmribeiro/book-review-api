package com.resenha.microserviceresenha.dto.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.jackson.JsonComponent;

import java.time.LocalDateTime;
import java.util.List;

@JsonComponent
@Setter
@Getter
public class UserModelDTO {

    @JsonProperty
    private String id;
    @JsonProperty
    private String name;
    @JsonProperty
    private String nickname;
    @JsonProperty
    private String idToken;
    @JsonProperty
    private LocalDateTime insertDate;
    @JsonProperty
    private List<String> following;

}
