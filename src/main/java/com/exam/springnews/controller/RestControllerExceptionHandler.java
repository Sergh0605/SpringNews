package com.exam.springnews.controller;

import com.exam.springnews.dto.ErrorDto;
import com.exam.springnews.exceptions.CustomApplicationException;
import com.exam.springnews.service.ArticleServiceImpl;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.slf4j.LoggerFactory.getLogger;

@ControllerAdvice
public class RestControllerExceptionHandler {

    private static final Logger log = getLogger(ArticleServiceImpl.class);

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDto handleRuntimeException(CustomApplicationException e) {
        log.debug(e.getMessage());
        return new ErrorDto(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorDto handleRuntimeException(RuntimeException e) {
        log.debug(e.getMessage());
        return new ErrorDto(e.getMessage());
    }


}
