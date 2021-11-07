package com.exam.springnews.exceptions;

public class ArticleServiceException extends CustomApplicationException {
    public ArticleServiceException(String message) {
        super(message);
    }
}
