package com.resenha.microserviceresenha.controllers;

import com.resenha.microserviceresenha.controllers.assemblers.ReviewModelAssembler;
import com.resenha.microserviceresenha.data.model.Review;
import com.resenha.microserviceresenha.dto.PageableResults;
import com.resenha.microserviceresenha.dto.ReviewDTO;
import com.resenha.microserviceresenha.dto.model.ReviewModelDTO;
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

    @ApiOperation(value = "Fetch the recent reviews",response = ReviewDTO.class)
    @GetMapping(value = "/reviews/page/{page}/size/{size}")
    public ResponseEntity<List<ReviewDTO>>  findRecentReviews(@PathVariable(name = "page") Integer page,
                                                              @PathVariable(name = "size") Integer size){

        PageableResults<ReviewDTO> first10ReviewsOrderByCreationDate = reviewsService.findFirst10ReviewsOrderByCreationDate(page, size);
        List<ReviewDTO> data = first10ReviewsOrderByCreationDate.getData();
        if(data.isEmpty()){
            throw new RecordNotFoundException("sem filtro");
        }
//        Map<String, Object> response = new HashMap<>();
//        response.put("reviews", reviews);
//        response.put("currentPage", page);
//        response.put("totalItems", first10ReviewsOrderByCreationDate.getMetadata().getTotal());
//        response.put("totalPages", totalPages);

        return ResponseEntity
                .ok()
                .body(data);
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

    @ApiOperation(value = "Search most liked reviews",response = ReviewDTO.class)
    @GetMapping(value = "/reviews/favorites")
    public ResponseEntity<List<ReviewDTO>> findFavoritesReviews(){
        List<ReviewDTO> first10MoreLikedReviews = reviewsService.findFavoritesReviews();
        if(first10MoreLikedReviews.isEmpty()){
            throw new RecordNotFoundException("sem filtro");
        }

        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .body(first10MoreLikedReviews);
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
