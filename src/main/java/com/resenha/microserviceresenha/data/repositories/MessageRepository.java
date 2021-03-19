package com.resenha.microserviceresenha.data.repositories;

import com.resenha.microserviceresenha.data.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepository extends MongoRepository<Message, String> {

}
