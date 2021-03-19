package com.resenha.microserviceresenha.controllers;

import com.resenha.microserviceresenha.controllers.assemblers.LikedReviewAssembler;
import com.resenha.microserviceresenha.data.model.LikedReview;
import com.resenha.microserviceresenha.dto.LikedReviewDTO;
import com.resenha.microserviceresenha.dto.model.LikedReviewModelDTO;
import com.resenha.microserviceresenha.exceptions.RecordNotFoundException;
import com.resenha.microserviceresenha.services.LikedReviewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Api(value="LikedReviewController")
@AllArgsConstructor
@RestController
public class LikedReviewController {

    private final LikedReviewService likedReviewService;
    private final LikedReviewAssembler assembler;

    @ApiOperation(value = "Fetch all liked reviews",response = LikedReview.class)
    @GetMapping(value = "/liked-reviews")
    public ResponseEntity<List<LikedReview>> findAllLikedReviews(){
        List<LikedReview> allLikedReviews = likedReviewService.getAllLikedReviews();
        if(allLikedReviews.isEmpty()){
            throw new RecordNotFoundException("sem filtro");
        }

        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .body(allLikedReviews);
    }

    @ApiOperation(value = "Fetch all liked reviews",response = LikedReviewDTO.class)
    @GetMapping(value = "/liked-reviews/user/{userId}")
    public ResponseEntity<List<LikedReviewDTO>> findAllLikedReviews(@PathVariable String userId){
        List<LikedReviewDTO> allLikedReviews = likedReviewService.findFavoritesReviews(userId);
        if(allLikedReviews.isEmpty()){
            throw new RecordNotFoundException("sem filtro");
        }

        return ResponseEntity
                .ok()
//                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .body(allLikedReviews);
    }


    @ApiOperation(value = "Search a liked review by id",response = LikedReview.class)
    @GetMapping(value = "/liked-reviews/{id}")
    public ResponseEntity<LikedReview> findById(@PathVariable String id){
        LikedReview byId = likedReviewService.findById(id);
        if(Objects.isNull(byId)){
            throw new RecordNotFoundException(id);
        }

        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.maxAge(60, TimeUnit.MINUTES))
                .body(byId);
    }

    @ApiOperation(value = "Create a like to a review",response = LikedReview.class)
    @PostMapping(value = "/liked-reviews")
    public ResponseEntity<?> createLikedReview(@RequestBody LikedReviewModelDTO likedReviewModelDTO){
        Optional<LikedReview> likedReview = likedReviewService.saveUpdateLikedReview(likedReviewModelDTO);
        if(likedReview.isPresent()){
            EntityModel<LikedReview> likedReviewEntityModel = assembler.toModel(likedReview.get());
            return ResponseEntity
                    .created(likedReviewEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                    .body(likedReviewEntityModel);
        }else{
            return new ResponseEntity(HttpStatus.CONFLICT);
        }
    }


    @ApiOperation(value = "Delete a like review")
    @DeleteMapping("/liked-reviews/{reviewId}")
    public ResponseEntity<?> deleteLikedReview(@PathVariable String reviewId) {

        likedReviewService.deleteLikedReviewById(reviewId);

        return ResponseEntity.noContent().build();
    }
}
