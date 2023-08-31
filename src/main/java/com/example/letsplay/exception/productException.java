package com.example.letsplay.exception;

public class productException extends Exception{
    private static final long serialVersionUID = 1L;

    public productException (String message) {
        super(message);
    }

    public static String NotFoundException(String id) {
        return "Product with " + id + " not found!";
    }

    public static String productAlreadyExists (String id) {
        return "Product already exists";
    }

}
