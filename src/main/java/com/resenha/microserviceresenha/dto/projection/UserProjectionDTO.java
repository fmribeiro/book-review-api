package com.resenha.microserviceresenha.dto.projection;

import com.resenha.microserviceresenha.dto.UserDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.jackson.JsonComponent;

import java.util.List;

@Getter
@Setter
@JsonComponent
public class UserProjectionDTO {

    private List<UserDTO> data;
    private Metadata metadata;
}
