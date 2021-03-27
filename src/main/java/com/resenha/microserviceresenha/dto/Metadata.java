package com.resenha.microserviceresenha.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.jackson.JsonComponent;

@Getter
@Setter
@JsonComponent
public class Metadata {
    Integer total;
    Integer page;
}