package com.example.demo.exception;

public class NotFoundException extends RuntimeException{
    private String message;
    public NotFoundException(){
        this.message = "No such entity";
    }

    @Override
    public String getMessage() {
        return message;
    }
}
