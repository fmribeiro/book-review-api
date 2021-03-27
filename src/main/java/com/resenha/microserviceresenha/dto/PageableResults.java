package com.resenha.microserviceresenha.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.jackson.JsonComponent;

import java.util.List;

@Getter
@Setter
@JsonComponent
public class PageableResults<T> {

    private List<T> data;
    private Metadata metadata;
}
