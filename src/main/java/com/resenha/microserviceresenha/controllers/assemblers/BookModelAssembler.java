package com.resenha.microserviceresenha.controllers.assemblers;

import com.resenha.microserviceresenha.controllers.BooksController;
import com.resenha.microserviceresenha.data.model.Book;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class BookModelAssembler implements RepresentationModelAssembler<Book, EntityModel<Book>> {


    @Override
    public EntityModel<Book> toModel(Book book) {
        return EntityModel.of(book,
                linkTo(methodOn(BooksController.class).findByBookId(book.getId())).withSelfRel(),
                linkTo(methodOn(BooksController.class).findAllBooksOrderByInsertDate()).withRel("books/"));
    }
}
