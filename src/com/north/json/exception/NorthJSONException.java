package com.north.json.exception;

public class NorthJSONException extends RuntimeException{

    public NorthJSONException(){
        super();
    }

    public NorthJSONException(String message){
        super(message);
    }

    public NorthJSONException(String message, Throwable cause){
        super(message, cause);
    }
}
