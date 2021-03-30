package com.resenha.microserviceresenha.dto.projection;

import com.resenha.microserviceresenha.dto.BookDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.jackson.JsonComponent;

import java.util.List;

@Getter
@Setter
@JsonComponent
public class BookProjectionDTO {

    private List<BookDTO> data;
    private Metadata metadata;
}
