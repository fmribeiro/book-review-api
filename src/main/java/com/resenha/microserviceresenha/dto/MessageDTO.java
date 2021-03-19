package com.resenha.microserviceresenha.dto;

import com.resenha.microserviceresenha.dto.model.UserModelDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.jackson.JsonComponent;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonComponent
public class MessageDTO {

    private String id;
    private String message;
    private String sender;
    private String receiver;
    private LocalDateTime insertDate;
    private UserModelDTO user;
}