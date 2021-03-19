package com.resenha.microserviceresenha.controllers;

import com.resenha.microserviceresenha.exceptions.RecordNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class RecordNotFoundAdvice {

    @ResponseBody
    @ExceptionHandler(RecordNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String recordNotFoundHandler(RecordNotFoundException ex){
        return ex.getMessage();
    }
}
