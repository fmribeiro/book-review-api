package com.resenha.microserviceresenha.exceptions;

public class RecordNotFoundException extends RuntimeException{

    public RecordNotFoundException(String id){
        super("Could not find record with filter: "+id);
    }
}
