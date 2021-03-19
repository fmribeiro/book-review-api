package com.resenha.microserviceresenha.data.repositories;

import com.resenha.microserviceresenha.data.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
}
