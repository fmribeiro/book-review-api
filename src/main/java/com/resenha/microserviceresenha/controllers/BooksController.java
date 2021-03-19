package com.resenha.microserviceresenha.controllers;

import com.resenha.microserviceresenha.controllers.assemblers.BookModelAssembler;
import com.resenha.microserviceresenha.data.model.Book;
import com.resenha.microserviceresenha.dto.BookDTO;
import com.resenha.microserviceresenha.dto.model.BookModelDTO;
import com.resenha.microserviceresenha.exceptions.RecordNotFoundException;
import com.resenha.microserviceresenha.services.BooksService;
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

@Api(value="livrosController")
@AllArgsConstructor
@RestController
public class BooksController {

    private final BooksService booksService;
    private final BookModelAssembler assembler;

    @ApiOperation(value = "Search a book by the book's title",response = Book.class)
    @GetMapping("/books/title/{title}")
    public ResponseEntity<List<Book>> findByTitle(@PathVariable String title) {
        List<Book> booksByName = booksService.findByTitle(title);
        if(booksByName.isEmpty()){
            throw new RecordNotFoundException(title);
        }

        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .body(booksByName);
    }

    @ApiOperation(value = "Search a book by the book's userId",response = Book.class)
    @GetMapping("/books/user/{id}")
    public ResponseEntity<List<Book>> findBookByUserId(@PathVariable String id) {
        List<Book> booksByName = booksService.findByUserId(id);
        if(booksByName.isEmpty()){
            throw new RecordNotFoundException(id);
        }

        return ResponseEntity
                .ok()
//                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .body(booksByName);
    }

    @ApiOperation(value = "Search a book by the book's id",response = Book.class)
    @GetMapping("/books/{id}")
    public ResponseEntity<Book> findByBookId(@PathVariable String id) {
        Book booksByName = booksService.findById(id);
        if(Objects.isNull(booksByName)){
            throw new RecordNotFoundException(id);
        }

        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.maxAge(60, TimeUnit.MINUTES))
                .body(booksByName);
    }

    @ApiOperation(value = "Search user book by reading status",response = BookDTO.class)
    @GetMapping("/books/user/{id}/readStatus/{readStatus}")
    public ResponseEntity<List<BookDTO>>  findUserBookByReadingStatus(@PathVariable String id, @PathVariable String readStatus) {
        List<BookDTO> booksByReadStatus = booksService.findUserBooksByStatus(id, readStatus);
        if(booksByReadStatus.isEmpty()){
            throw new RecordNotFoundException(id);
        }
        return ResponseEntity
                .ok()
//                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .body(booksByReadStatus);
    }

    @ApiOperation(value = "Fetch last created books",response = BookDTO.class)
    @GetMapping("/books")
    public ResponseEntity<List<BookDTO>>  findAllBooksOrderByInsertDate(){
        List<BookDTO> findBooksByInsertDate = booksService.findFirst10OrderByIdDesc();
        if(findBooksByInsertDate.isEmpty()){
            throw new RecordNotFoundException("sem filtro");
        }
        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .body(findBooksByInsertDate);
    }

    @ApiOperation(value = "Create a book record",response = Book.class)
    @PostMapping("/books")
    public ResponseEntity<?> createBook(@RequestBody BookModelDTO book){
        Optional<Book> book1 = booksService.saveUpdateBook(book);
        if(book1.isPresent()){
            EntityModel<Book> savedBook = assembler.toModel(book1.get());
            return ResponseEntity
                    .created(savedBook.getRequiredLink(IanaLinkRelations.SELF).toUri())
                    .body(savedBook);
        }else{
            return new ResponseEntity(HttpStatus.CONFLICT);
        }
    }

    @ApiOperation(value = "Update a book record", response = Book.class)
    @PutMapping("/books")
    public ResponseEntity<?> updateBook(@RequestBody BookModelDTO bookModelDTO){
        Optional<Book> book1 = booksService.saveUpdateBook(bookModelDTO);
        if(book1.isPresent()){
            EntityModel<Book> savedBook = assembler.toModel(book1.get());
            return ResponseEntity
                    .created(savedBook.getRequiredLink(IanaLinkRelations.SELF).toUri())
                    .body(savedBook);
        }else{
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Delete a book record")
    @DeleteMapping("/books/{bookId}")
    public ResponseEntity<?> deleteEmployee(@PathVariable String bookId) {
        booksService.deleteById(bookId);
        return ResponseEntity.noContent().build();
    }

}
