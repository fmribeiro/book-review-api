package com.resenha.microserviceresenha.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resenha.microserviceresenha.data.model.LikedReview;
import com.resenha.microserviceresenha.data.model.Review;
import com.resenha.microserviceresenha.data.repositories.LikedReviewRepository;
import com.resenha.microserviceresenha.data.repositories.ReviewRepository;
import com.resenha.microserviceresenha.dto.LikedReviewDTO;
import com.resenha.microserviceresenha.dto.model.LikedReviewModelDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

@AllArgsConstructor
@Service
@Slf4j
public class LikedReviewService {

    private ReviewRepository reviewRepository;
    private LikedReviewRepository likedReviewRepository;
    private MongoTemplate mongoTemplate;
    private ObjectMapper objectMapper;

    public Optional<LikedReview> saveUpdateLikedReview(LikedReviewModelDTO likedReviewModelDTO) {
        Optional optionalBook = Optional.empty();
        try{
            LikedReview likedReview = objectMapper.convertValue(likedReviewModelDTO, LikedReview.class);
            boolean removed = removeOrIncrementLikedReview(likedReview);
            if(removed){
                return optionalBook;
            }
            return Optional.of(likedReviewRepository.save(likedReview));
        }
        catch (Exception e){
            log.error("Erro ao inserir um novo likedReview", e);
        }
        return optionalBook;
    }

    public boolean removeOrIncrementLikedReview(LikedReview likedReview) {
        boolean wasRemoved = false;
        LikedReview byReviewIdAndUserId = likedReviewRepository.findByReviewIdAndUserId(likedReview.getReviewId(), likedReview.getUserId());
        Review review = reviewRepository.findById(likedReview.getReviewId().toHexString()).get();
        Integer likeTotal = review.getLikes();

        if (Objects.nonNull(byReviewIdAndUserId)) {
            likeTotal -= 1;
            review.setLikes(likeTotal);
            reviewRepository.save(review);

            likedReviewRepository.delete(byReviewIdAndUserId);
            wasRemoved = true;
        } else {
            likeTotal += 1;
            review.setLikes(likeTotal);
            reviewRepository.save(review);
        }
        return wasRemoved;
    }

    public void deleteLikedReviewById(String reviewId) {
        likedReviewRepository.deleteById(reviewId);
    }

    public List<LikedReview> getAllLikedReviews() {
        return likedReviewRepository.findAll();
    }

    public LikedReview findById(String id) {
        Optional<LikedReview> byId = likedReviewRepository.findById(id);
        if (byId.isPresent()) {
            return byId.get();
        }
        return null;
    }

    public List<LikedReviewDTO> findFavoritesReviews(String userId) {
        LookupOperation lookup = LookupOperation.newLookup()
                .from("reviews")
                .localField("reviewId")
                .foreignField("_id")
                .as("review");

        LookupOperation lookup2 = LookupOperation.newLookup()
                .from("users")
                .localField("review.userId")
                .foreignField("_id")
                .as("user");

        ObjectId objectId = new ObjectId(userId);

        SortOperation sort = sort(Sort.by(Sort.Direction.DESC, "_id"));
        LimitOperation limitOperation = new LimitOperation(10);

        AggregationOperation match1 = Aggregation.match(Criteria.where("userId").is(objectId));
        AggregationOperation unwind = Aggregation.unwind("review");
        AggregationOperation unwind2 = Aggregation.unwind("user");

        Aggregation aggregation = Aggregation.newAggregation(lookup, match1, unwind, lookup2, unwind2, limitOperation, sort);
        List<LikedReviewDTO> results = mongoTemplate.aggregate(aggregation, "likedReviews", LikedReviewDTO.class).getMappedResults();
        return results;
    }
}
