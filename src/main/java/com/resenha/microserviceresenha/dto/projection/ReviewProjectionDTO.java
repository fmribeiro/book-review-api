package com.resenha.microserviceresenha.dto.projection;

import com.resenha.microserviceresenha.dto.ReviewDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.jackson.JsonComponent;

import java.util.List;

@Getter
@Setter
@JsonComponent
public class ReviewProjectionDTO {

    private List<ReviewDTO> data;
    private Metadata metadata;
}
