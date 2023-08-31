package com.example.letsplay.exception;

public class userException extends Exception{

    //a version control for classes.
    // If you change the structure of a class (e.g., add new fields), but don't change the serialVersionUID, then trying to deserialize old objects of that class will fail because Java runtime considers them to be of different versions.
    // By setting this value explicitly, you take control of this versioning (instead of letting Java automatically generate a UID, which might change unpredictably when the class structure changes).
    private static final long serialVersionUID = 1L;

    public userException (String message) {
        super(message);
    }

    public static String NotFoundException(String id) {
        return "User with " + id + " not found!";
    }

    public static String userAlreadyExists (String id) {
        return "User with the given email already exists";
    }

}
