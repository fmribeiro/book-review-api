package com.resenha.microserviceresenha.controllers;

import com.resenha.microserviceresenha.controllers.assemblers.ReviewModelAssembler;
import com.resenha.microserviceresenha.data.model.Review;
import com.resenha.microserviceresenha.dto.ReviewDTO;
import com.resenha.microserviceresenha.dto.model.ReviewModelDTO;
import com.resenha.microserviceresenha.dto.projection.ReviewProjectionDTO;
import com.resenha.microserviceresenha.exceptions.RecordNotFoundException;
import com.resenha.microserviceresenha.services.ReviewsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Api(value="ResenhasController")
@AllArgsConstructor
@RestController
public class ReviewsController {

    private final ReviewsService reviewsService;
    private final ReviewModelAssembler assembler;

    @ApiOperation(value = "Fetch the recent reviews",response = ReviewProjectionDTO.class)
    @GetMapping(value = "/reviews/page/{page}/size/{size}")
    public ResponseEntity<Map<String, Object>> findRecentReviews(@PathVariable(name = "page") Integer page,
                                                                 @PathVariable(name = "size") Integer size){
        final ReviewProjectionDTO first10ReviewsOrderByCreationDate = reviewsService.findRecentReviews(page, size);
        final List<ReviewDTO> data = first10ReviewsOrderByCreationDate.getData();
        final int totalItems = first10ReviewsOrderByCreationDate.getMetadata().getTotal();
        if(data.isEmpty()){
            throw new RecordNotFoundException("sem filtro");
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("currentPage", page);
        response.put("totalItems", totalItems);
        response.put("totalReturned", data.size());
        response.put("totalPages", ( totalItems % size == 0 ? totalItems / size : (totalItems/ size) + 1));
        response.put("reviews", data);

        return ResponseEntity
                .ok()
                .body(response);
    }



    @ApiOperation(value = "Search a review by id",response = Review.class)
    @GetMapping(value = "/reviews/{id}")
    public ResponseEntity<Review> findById(@PathVariable String id){
        Review byReviewId = reviewsService.findById(id);
        if(Objects.isNull(byReviewId)){
            throw new RecordNotFoundException(id);
        }

        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.maxAge(60, TimeUnit.MINUTES))
                .body(byReviewId);
    }

    @ApiOperation(value = "Search a review by user_id",response = ReviewModelDTO.class)
    @GetMapping(value = "/reviews/user/{userId}")
    public ResponseEntity<List<ReviewModelDTO>> findByUserId(@PathVariable String userId){
        List<ReviewModelDTO> byUserId = reviewsService.findByUserId(userId);
        if(byUserId.isEmpty()){
            throw new RecordNotFoundException(userId);
        }

        return ResponseEntity
                .ok()
//                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .body(byUserId);
    }

    @ApiOperation(value = "Search a review by book_title",response = Review.class)
    @GetMapping(value = "/reviews/book/{bookTitle}")
    public ResponseEntity<List<Review>> findByBookTitle(@PathVariable String bookTitle){
        List<Review> first10ByOrderByInsertDate = reviewsService.findByBookTitle(bookTitle);
        if(first10ByOrderByInsertDate.isEmpty()){
            throw new RecordNotFoundException(bookTitle);
        }

        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .body(first10ByOrderByInsertDate);
    }

    @ApiOperation(value = "Search most liked reviews",response = ReviewProjectionDTO.class)
    @GetMapping(value = "/reviews/favorites/page/{page}/size/{size}")
    public ResponseEntity<Map<String, Object>> findFavoritesReviews(@PathVariable(name = "page") Integer page,
                                                                @PathVariable(name = "size") Integer size){
        final ReviewProjectionDTO first10ReviewsOrderByCreationDate = reviewsService.findFavoritesReviews(page, size);
        final List<ReviewDTO> data = first10ReviewsOrderByCreationDate.getData();
        final int totalItems = first10ReviewsOrderByCreationDate.getMetadata().getTotal();
        if(data.isEmpty()){
            throw new RecordNotFoundException("sem filtro");
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("currentPage", page);
        response.put("totalItems", totalItems);
        response.put("totalReturned", data.size());
        response.put("totalPages", ( totalItems % size == 0 ? totalItems / size : (totalItems/ size) + 1));
        response.put("reviews", data);

        return ResponseEntity
                .ok()
                .body(response);
    }

    @ApiOperation(value = "Create a review",response = Review.class)
    @PostMapping(value = "/reviews")
    public ResponseEntity<?>  createReview(@RequestBody ReviewModelDTO reviewModelDTO){
        Optional<Review> review = reviewsService.saveUpdateReview(reviewModelDTO);
        if(review.isPresent()){
            EntityModel<Review> savedBook = assembler.toModel(review.get());
            return ResponseEntity
                    .created(savedBook.getRequiredLink(IanaLinkRelations.SELF).toUri())
                    .body(savedBook);
        }else{
            return new ResponseEntity(HttpStatus.CONFLICT);
        }
    }

    @ApiOperation(value = "Update a review",response = Review.class)
    @PutMapping(value = "/reviews")
    public ResponseEntity<?>  updateReview(@RequestBody ReviewModelDTO reviewModelDTO){
        Optional<Review> review = reviewsService.saveUpdateReview(reviewModelDTO);
        if(review.isPresent()){
            EntityModel<Review> savedBook = assembler.toModel(review.get());
            return ResponseEntity
                    .created(savedBook.getRequiredLink(IanaLinkRelations.SELF).toUri())
                    .body(savedBook);
        }else{
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Delete a book record")
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable String reviewId) {
        reviewsService.deleteReviewById(reviewId);
        return ResponseEntity.noContent().build();
    }

}
