package com.resenha.microserviceresenha.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resenha.microserviceresenha.data.model.User;
import com.resenha.microserviceresenha.data.repositories.UserRepository;
import com.resenha.microserviceresenha.dto.UserDTO;
import com.resenha.microserviceresenha.dto.model.UserModelDTO;
import com.resenha.microserviceresenha.dto.projection.UserProjectionDTO;
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

    public Optional<UserModelDTO> saveOrUpdate(UserModelDTO userModelDTO) {
        Optional optionalUser = Optional.empty();
        try{
            User user = objectMapper.convertValue(userModelDTO, User.class);
            userRepository.save(user);
            return optionalUser.of(objectMapper.convertValue(user, UserModelDTO.class));
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


    public UserProjectionDTO getAllUsers(int page, int size) {
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
        LimitOperation limitOperation = new LimitOperation(size);

        CountOperation countOperation = new CountOperation.CountOperationBuilder().as("total");
        AddFieldsOperation addFieldsOperation = AddFieldsOperation.builder().build().addField("page", page);
        SkipOperation skipOperation = new SkipOperation(page * size);

        FacetOperation facetOperation = new FacetOperation()
                .and(countOperation, addFieldsOperation).as("metadata")
                .and(skipOperation, limitOperation).as("data");
        AggregationOperation unwind2 = Aggregation.unwind("$metadata");

        Aggregation aggregation = Aggregation.newAggregation(lookup1, lookup2, lookup3, sort, facetOperation, unwind2);
        UserProjectionDTO mappedResult = mongoTemplate
                .aggregate(aggregation, "users", (UserProjectionDTO.class))
                .getUniqueMappedResult();
        return mappedResult;
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
