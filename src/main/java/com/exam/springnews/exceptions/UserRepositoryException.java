package com.exam.springnews.exceptions;

public class UserRepositoryException extends CustomApplicationException {

    public UserRepositoryException(String message) {
        super(message);
    }
}
