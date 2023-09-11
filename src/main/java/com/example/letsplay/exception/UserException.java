package com.example.letsplay.exception;

import java.io.Serial;

public class UserException extends Exception{

    //a version control for classes.
    // If you change the structure of a class (e.g., add new fields), but don't change the serialVersionUID, then trying to deserialize old objects of that class will fail because Java runtime considers them to be of different versions.
    // By setting this value explicitly, you take control of this versioning (instead of letting Java automatically generate a UID, which might change unpredictably when the class structure changes).
    @Serial
    private static final long serialVersionUID = 1L;

    public UserException(String message) {
        super(message);
    }

    public static String NotFoundException(String id) {
        return "User with " + id + " not found!";
    }

    public static String userAlreadyExists () {
        return "User with the given email already exists";
    }

    public static String invalidUpdates () {
        //How to change this to bad request
        return "Fields can't be all spaces\nRole must either be ROLE_ADMIN or ROLE_USER\nPassword must be at least 4 characters, maximum 50 characters, no spaces allowed";
    }

    public static String invalidCredentials() {
        return "Invalid Credentials";
    }

    public static String invalid() {
        return "Invalid token or user is not authenticated";
    }

}
