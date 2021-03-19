package com.resenha.microserviceresenha.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resenha.microserviceresenha.data.model.User;
import com.resenha.microserviceresenha.data.repositories.UserRepository;
import com.resenha.microserviceresenha.dto.UserDTO;
import com.resenha.microserviceresenha.dto.model.UserModelDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

@AllArgsConstructor
@Service
@Slf4j
public class UserService {

    private UserRepository userRepository;
    private MongoTemplate mongoTemplate;
    private ObjectMapper objectMapper;

    public UserModelDTO findByIdToken(String idToken) {
        Query query = new Query(Criteria
                .where("idToken").is(idToken));

        UserModelDTO one = mongoTemplate.findOne(query, UserModelDTO.class, "users");
        return one;
    }

    public Optional<User> saveOrUpdate(UserModelDTO userModelDTO) {
        Optional optionalUser = Optional.empty();
        try{
            User user = objectMapper.convertValue(userModelDTO, User.class);
            return optionalUser.of(userRepository.save(user));
        }
        catch (Exception e){
            log.error("Erro ao criar ou atualizar o usuario", e);
        }
        return optionalUser;
    }

    public void deleteUserById(String userId) {
        userRepository.deleteById(userId);
    }

    public UserModelDTO findById(String id) {
        Query query = new Query(Criteria
                .where("_id").is(new ObjectId(id)));

        UserModelDTO one = mongoTemplate.findOne(query, UserModelDTO.class, "users");
        return one;
    }


    public List<UserDTO> getAllUsers() {
        LookupOperation lookup1 = LookupOperation.newLookup()
                .from("users")
                .localField("following")
                .foreignField("_id")
                .as("usersFollowing");

        LookupOperation lookup2 = LookupOperation.newLookup()
                .from("books")
                .localField("_id")
                .foreignField("userId")
                .as("books");

        LookupOperation lookup3 = LookupOperation.newLookup()
                .from("reviews")
                .localField("_id")
                .foreignField("userId")
                .as("reviews");
        SortOperation sort = sort(Sort.by(Sort.Direction.DESC, "_id"));

        LimitOperation limitOperation = new LimitOperation(10);

        Aggregation aggregation = Aggregation.newAggregation(lookup1, lookup2, lookup3, limitOperation, sort);
        List<UserDTO> results = mongoTemplate.aggregate(aggregation, "users", UserDTO.class).getMappedResults();
        return results;
    }

    public List<UserDTO> mountSingleUserProfile(String userId) {
        List<ObjectId> userIds = new ArrayList<>();
        userIds.add(new ObjectId(userId));
        return mountUserProfile(userIds);
    }

    public List<UserDTO> mountFollowersProfile(String userId) {
        User user = userRepository.findById(userId).get();
        return mountUserProfile(user.getFollowing());
    }


    //TODO AJUSTAR O RETORNO DOS USUARIOS QUE EU SIGO(FOLLOWING)
    private List<UserDTO> mountUserProfile(List<ObjectId> userIds) {
        List<UserDTO> results = new ArrayList<>();
        if (Objects.isNull(userIds)) {
            return results;
        }
        LookupOperation lookup1 = LookupOperation.newLookup()
                .from("users")
                .localField("following")
                .foreignField("_id")
                .as("usersFollowing");

        LookupOperation lookup2 = LookupOperation.newLookup()
                .from("books")
                .localField("_id")
                .foreignField("userId")
                .as("books");

        LookupOperation lookup3 = LookupOperation.newLookup()
                .from("reviews")
                .localField("_id")
                .foreignField("userId")
                .as("reviews");
        SortOperation sort = sort(Sort.by(Sort.Direction.ASC, "name"));
        LimitOperation limitOperation = new LimitOperation(10);
        AggregationOperation match = Aggregation.match(Criteria.where("_id").in(userIds));

        Aggregation aggregation = Aggregation.newAggregation(match, lookup1, lookup2, lookup3, limitOperation, sort);
        results = mongoTemplate.aggregate(aggregation, "users", UserDTO.class).getMappedResults();
        return results;
    }

}
