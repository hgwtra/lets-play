package com.example.letsplay.exception;

import java.io.Serial;

public class ProductException extends Exception{

    @Serial
    private static final long serialVersionUID = 1L;

    public ProductException(String message) {
        super(message);
    }

    public static String NotFoundException(String id) {
        return "Product with " + id + " not found!";
    }

    public static String productAlreadyExists (String id) {
        return "Product already exists";
    }

}
