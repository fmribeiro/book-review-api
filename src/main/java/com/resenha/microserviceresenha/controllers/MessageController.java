package com.resenha.microserviceresenha.controllers;

import com.resenha.microserviceresenha.controllers.assemblers.MessageModelAssembler;
import com.resenha.microserviceresenha.data.model.Message;
import com.resenha.microserviceresenha.dto.MessageDTO;
import com.resenha.microserviceresenha.dto.model.MessageModelDTO;
import com.resenha.microserviceresenha.exceptions.RecordNotFoundException;
import com.resenha.microserviceresenha.services.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Api(value="MessageController")
@AllArgsConstructor
@RestController
public class MessageController {

    private final MessageService messageService;
    private final MessageModelAssembler assembler;

    @ApiOperation(value = "Fetch all messages",response = Message.class)
    @GetMapping(value = "/messages")
    public ResponseEntity<List<Message>> findAllMessages(){
        List<Message> allMessages = messageService.getAllMessages();
        if(allMessages.isEmpty()){
            throw new RecordNotFoundException("sem filtro");
        }

        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .body(allMessages);
    }

    @ApiOperation(value = "Fetch all sended messages",response = MessageDTO.class)
    @GetMapping(value = "/messages/sended/{senderId}")
    public ResponseEntity<List<MessageDTO>> findSendedMessages(@PathVariable String senderId){
        List<MessageDTO> allMessages = messageService.getSendedMessages(senderId);
        if(allMessages.isEmpty()){
            throw new RecordNotFoundException("sem filtro");
        }

        return ResponseEntity
                .ok()
//                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .body(allMessages);
    }

    @ApiOperation(value = "Fetch all received messages",response = MessageDTO.class)
    @GetMapping(value = "/messages/received/{receiverId}")
    public ResponseEntity<List<MessageDTO>> findReceivedMessages(@PathVariable String receiverId){
        List<MessageDTO> allMessages = messageService.getReceivedMessages(receiverId);
        if(allMessages.isEmpty()){
            throw new RecordNotFoundException("sem filtro");
        }

        return ResponseEntity
                .ok()
//                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .body(allMessages);
    }


    @ApiOperation(value = "Search a message by id",response = Message.class)
    @GetMapping(value = "/messages/{id}")
    public ResponseEntity<Message> findById(@PathVariable String id){
        Message byId = messageService.findById(id);
        if(Objects.isNull(byId)){
            throw new RecordNotFoundException(id);
        }

        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.maxAge(60, TimeUnit.MINUTES))
                .body(byId);
    }

    @ApiOperation(value = "Create a message",response = Message.class)
    @PostMapping(value = "/messages")
    public ResponseEntity<?> createMessage(@RequestBody MessageModelDTO message){
        EntityModel<Message> savedMessage = assembler.toModel(messageService.saveUpdateMessage(message));

        return ResponseEntity
                .created(savedMessage.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(savedMessage);
    }

    @ApiOperation(value = "Delete a book message")
    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable String messageId) {

        messageService.deleteMessageById(messageId);

        return ResponseEntity.noContent().build();
    }
}
