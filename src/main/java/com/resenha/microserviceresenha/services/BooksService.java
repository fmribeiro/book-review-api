package com.resenha.microserviceresenha.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resenha.microserviceresenha.data.model.Book;
import com.resenha.microserviceresenha.data.repositories.BookRepository;
import com.resenha.microserviceresenha.dto.BookDTO;
import com.resenha.microserviceresenha.dto.model.BookModelDTO;
import com.resenha.microserviceresenha.dto.projection.BookProjectionDTO;
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
public class BooksService {

    private BookRepository bookRepository;
    private MongoTemplate mongoTemplate;
    private ObjectMapper objectMapper;

    public List<Book> findByTitle(String nome){
        return bookRepository.findByTitleRegexOrderByIdDesc(nome);
    }

    public Book findById(String userId){
        Optional<Book> byId = bookRepository.findById(userId);
        if(byId.isPresent()){
            return byId.get();
        }
        return null;
    }

    public List<Book> findByUserId(String userId){
        ObjectId objectId = new ObjectId(userId);
        return bookRepository.findByUserIdOrderByIdDesc(objectId);
    }


    public List<BookDTO> findUserBooksByStatus(String userId, String readStatus){
        Query query = new Query(Criteria
                .where("userId").is(new ObjectId(userId))
                .and("readStatus").is(readStatus));
        query.with(Sort.by(Sort.Direction.DESC, "_id"));

        List<BookDTO> results = mongoTemplate.find(query, BookDTO.class, "books");

        return results;
    }

    public BookProjectionDTO findFirst10OrderByIdDesc(int page, int size){
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
        BookProjectionDTO aggregatedResults = mongoTemplate
                .aggregate(aggregation, "books", BookProjectionDTO.class)
                .getUniqueMappedResult();
        return aggregatedResults;
    }

    public Optional<Book> saveUpdateBook(BookModelDTO bookModelDTO){
        Optional optionalBook = Optional.empty();
        try{
            Book book = objectMapper.convertValue(bookModelDTO, Book.class);
            return optionalBook.of(bookRepository.save(book));
        }
        catch (Exception e){
            log.error("Erro ao inserir ou atualizar o livro", e);
        }
        return optionalBook;
    }

    public void deleteById(String id){
        bookRepository.deleteById(id);
    }



}
