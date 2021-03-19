package com.resenha.microserviceresenha.controllers.assemblers;

import com.resenha.microserviceresenha.controllers.MessageController;
import com.resenha.microserviceresenha.data.model.Message;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class MessageModelAssembler implements RepresentationModelAssembler<Message, EntityModel<Message>> {

    @Override
    public EntityModel<Message> toModel(Message message) {
        return EntityModel.of(message,
                linkTo(methodOn(MessageController.class).findById(message.getId())).withSelfRel(),
                linkTo(methodOn(MessageController.class).findSendedMessages(message.getId())).withRel("messages/"));
    }
}