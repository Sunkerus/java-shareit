package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException ex) {
        log.info("[VALIDATION ERROR]: " + ex.getMessage());
        return new ErrorResponse(Objects.requireNonNull(ex.getFieldError()).getDefaultMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(final ConstraintViolationException ex) {
        log.info("[VALIDATION ERROR]: " + ex.getConstraintViolations());
        StringBuilder builder = new StringBuilder();

        if (ex.getMessage().contains("from")) {
            builder.append("from: must be greater than or equal to 0. ");
        }

        if (ex.getLocalizedMessage().contains("size")) {
            builder.append("size: must be greater than or equal to 1.");
        }

        return new ErrorResponse(builder.toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingServletRequestParameterException(final MissingServletRequestParameterException ex) {
        log.info("[REQUEST PARAMETER ERROR]: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingRequestHeaderException(final MissingRequestHeaderException ex) {
        log.info("[REQUEST HEADER ERROR]: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidException(final DataException ex) {
        log.info("[INVALID ERROR]: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowableException(final Throwable ex) {
        log.info("[UNEXPECTED ERROR]: {}", ex.getMessage());
        return new ErrorResponse(String.format("[UNEXPECTED ERROR]: {%s}", ex.getMessage()));
    }
}
