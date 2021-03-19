package com.resenha.microserviceresenha.controllers.assemblers;

import com.resenha.microserviceresenha.controllers.LikedReviewController;
import com.resenha.microserviceresenha.data.model.LikedReview;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class LikedReviewAssembler implements RepresentationModelAssembler<LikedReview, EntityModel<LikedReview>> {

    @Override
    public EntityModel<LikedReview> toModel(LikedReview likedReview) {
        return EntityModel.of(likedReview,
                linkTo(methodOn(LikedReviewController.class).findById(likedReview.getId())).withSelfRel(),
                linkTo(methodOn(LikedReviewController.class).findAllLikedReviews()).withRel("liked-reviews/"));
    }
}