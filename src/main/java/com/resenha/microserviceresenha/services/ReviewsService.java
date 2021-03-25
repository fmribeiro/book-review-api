package com.resenha.microserviceresenha.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.client.model.Facet;
import com.resenha.microserviceresenha.data.model.Review;
import com.resenha.microserviceresenha.data.repositories.ReviewRepository;
import com.resenha.microserviceresenha.dto.PageableResults;
import com.resenha.microserviceresenha.dto.ReviewDTO;
import com.resenha.microserviceresenha.dto.model.ReviewModelDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

@AllArgsConstructor
@Service
@Slf4j
public class ReviewsService {

    private ReviewRepository reviewRepository;
    private MongoTemplate mongoTemplate;
    private ObjectMapper objectMapper;

    public PageableResults findFirst10ReviewsOrderByCreationDate(int page, int size) {
        LookupOperation lookup = LookupOperation.newLookup()
                .from("users")
                .localField("userId")
                .foreignField("_id")
                .as("user");
        AggregationOperation unwind = Aggregation.unwind("$user");

        SortOperation sort = sort(Sort.by(Sort.Direction.DESC, "_id"));
        LimitOperation limitOperation = new LimitOperation(size);

        CountOperation countOperation = new CountOperation.CountOperationBuilder().as("total");
        AddFieldsOperation addFieldsOperation = AddFieldsOperation.builder().build().addField("page", page);
        SkipOperation skipOperation = new SkipOperation(page * size);

        FacetOperation facetOperation = new FacetOperation()
                .and(countOperation, addFieldsOperation).as("metadata")
                .and(skipOperation, limitOperation).as("data");

        AggregationOperation unwind2 = Aggregation.unwind("$metadata");

        Aggregation aggregation = Aggregation.newAggregation(lookup, unwind, sort, facetOperation, unwind2);
        List<PageableResults> aggregatedResults = mongoTemplate
                .aggregate(aggregation, "reviews", PageableResults.class)
                .getMappedResults();

        return aggregatedResults.get(0);
    }

    public List<ReviewDTO> findFavoritesReviews() {
        LookupOperation lookup = LookupOperation.newLookup()
                .from("users")
                .localField("userId")
                .foreignField("_id")
                .as("user");
        AggregationOperation unwind = Aggregation.unwind("$user");

        SortOperation sort = sort(Sort.by(Sort.Direction.DESC, "likes"));
        LimitOperation limitOperation = new LimitOperation(10);

        Aggregation aggregation = Aggregation.newAggregation(lookup, unwind, limitOperation, sort);
        List<ReviewDTO> results = mongoTemplate.aggregate(aggregation, "reviews", ReviewDTO.class).getMappedResults();
        return results;
    }

    public Review findById(String id) {
        Optional<Review> byId = reviewRepository.findById(id);
        if (byId.isPresent()) {
            return byId.get();
        }
        return null;
    }

    public List<ReviewModelDTO> findByUserId(String userId) {
        Query query = new Query(Criteria
                .where("userId").is(new ObjectId(userId)));
        query.with(Sort.by(Sort.Direction.DESC, "likes"));
        query.with(Sort.by(Sort.Direction.DESC, "_id"));
        query.limit(10);

        List<ReviewModelDTO> results = mongoTemplate.find(query, ReviewModelDTO.class, "reviews");

        return results;
    }

    public List<Review> findByBookTitle(String bookTitle) {
        return reviewRepository.findByBookTitleRegex(bookTitle);
    }

    public Optional<Review> saveUpdateReview(ReviewModelDTO reviewModelDTO) {
        Optional optionalReview = Optional.empty();
        try {
            Review review = objectMapper.convertValue(reviewModelDTO, Review.class);
            return optionalReview.of(reviewRepository.save(review));
        } catch (Exception e) {
            log.error("Erro ao inserir ou atuaizar a resenha", e);
        }
        return optionalReview;
    }

    public void deleteReviewById(String id) {
        reviewRepository.deleteById(id);
    }

}
