package com.resenha.microserviceresenha.controllers.assemblers;

import com.resenha.microserviceresenha.controllers.UserController;
import com.resenha.microserviceresenha.data.model.User;
import com.resenha.microserviceresenha.dto.model.UserModelDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserModelAssembler implements RepresentationModelAssembler<UserModelDTO, EntityModel<UserModelDTO>> {

    @Override
    public EntityModel<UserModelDTO> toModel(UserModelDTO user) {
        return EntityModel.of(user,
                linkTo(methodOn(UserController.class).findById(user.getId())).withSelfRel());
    }
}