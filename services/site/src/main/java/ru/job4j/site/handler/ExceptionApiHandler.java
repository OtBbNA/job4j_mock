package ru.job4j.site.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.job4j.site.domain.ErrorMessage;
import ru.job4j.site.exception.IdNotFoundException;

public class ExceptionApiHandler {

    @ExceptionHandler(IdNotFoundException.class)
    public ResponseEntity<ErrorMessage> notFoundException(IdNotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage(exception.getMessage()));
    }
}
