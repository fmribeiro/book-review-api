package com.resenha.microserviceresenha.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resenha.microserviceresenha.data.model.Message;
import com.resenha.microserviceresenha.data.repositories.MessageRepository;
import com.resenha.microserviceresenha.dto.MessageDTO;
import com.resenha.microserviceresenha.dto.model.MessageModelDTO;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

@AllArgsConstructor
@Service
public class MessageService {
    
    private MessageRepository messageRepository;
    private ObjectMapper objectMapper;
    private MongoTemplate mongoTemplate;

    public Message saveUpdateMessage(MessageModelDTO messageModelDTO){
        Message message = objectMapper.convertValue(messageModelDTO, Message.class);
        return messageRepository.save(message);
    }

    public void deleteMessageById(String MessageId){
        messageRepository.deleteById(MessageId);
    }

    public List<MessageDTO> getReceivedMessages(String receiverId){
        LookupOperation lookup = LookupOperation.newLookup()
                .from("users")
                .localField("sender")
                .foreignField("_id")
                .as("user");

        ObjectId objectId = new ObjectId(receiverId);

        SortOperation sort = sort(Sort.by(Sort.Direction.DESC, "_id"));
        LimitOperation limitOperation = new LimitOperation(10);

        AggregationOperation match1 = Aggregation.match(Criteria.where("receiver").is(objectId));
        AggregationOperation unwind = Aggregation.unwind("user");

        Aggregation aggregation = Aggregation.newAggregation(lookup, match1, unwind, sort, limitOperation);
        List<MessageDTO> results = mongoTemplate.aggregate(aggregation, "messages", MessageDTO.class).getMappedResults();
        return results;
    }

    public List<MessageDTO> getSendedMessages(String senderId){
        LookupOperation lookup = LookupOperation.newLookup()
                .from("users")
                .localField("receiver")
                .foreignField("_id")
                .as("user");

        ObjectId objectId = new ObjectId(senderId);

        SortOperation sort = sort(Sort.by(Sort.Direction.DESC, "_id"));
        LimitOperation limitOperation = new LimitOperation(10);

        AggregationOperation match1 = Aggregation.match(Criteria.where("sender").is(objectId));
        AggregationOperation unwind = Aggregation.unwind("user");

        Aggregation aggregation = Aggregation.newAggregation(lookup, match1, unwind, sort, limitOperation);
        List<MessageDTO> results = mongoTemplate.aggregate(aggregation, "messages", MessageDTO.class).getMappedResults();
        return results;
    }

    public List<Message> getAllMessages(){
        return messageRepository.findAll();
    }

    public Message findById(String id){
        Optional<Message> byId = messageRepository.findById(id);
        if(byId.isPresent()){
            return byId.get();
        }
        return null;
    }
}
