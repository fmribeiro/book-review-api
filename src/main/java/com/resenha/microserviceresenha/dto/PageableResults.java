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

    public static void main(String[] args) {
        PageableResults<ReviewDTO>  pageableResults = new PageableResults<>();
        pageableResults.setData(null);

    }

    @Getter
    @Setter
    @JsonComponent
    public class Metadata{
        Integer total;
        Integer page;
    }
}
