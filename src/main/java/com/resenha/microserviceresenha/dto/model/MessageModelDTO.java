package com.resenha.microserviceresenha.dto.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.jackson.JsonComponent;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonComponent
public class MessageModelDTO {

    private String id;
    private String message;
    private String sender;
    private String receiver;
    private LocalDateTime insertDate;
}