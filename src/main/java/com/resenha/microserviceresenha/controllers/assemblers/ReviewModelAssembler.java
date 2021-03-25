package com.resenha.microserviceresenha.controllers.assemblers;

import com.resenha.microserviceresenha.controllers.ReviewsController;
import com.resenha.microserviceresenha.data.model.Review;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ReviewModelAssembler implements RepresentationModelAssembler<Review, EntityModel<Review>> {

    @Override
    public EntityModel<Review> toModel(Review review) {
        return EntityModel.of(review,
                linkTo(methodOn(ReviewsController.class).findById(review.getId())).withSelfRel());
//                linkTo(methodOn(ReviewsController.class).findRecentReviews()).withRel("reviews/"));
    }
}