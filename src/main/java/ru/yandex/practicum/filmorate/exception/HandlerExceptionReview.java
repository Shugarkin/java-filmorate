package ru.yandex.practicum.filmorate.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(value = "ru.yandex.practicum.filmorate.controller")
public class HandlerExceptionReview {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleThereIsNoSuchUserException(MethodArgumentNotValidException ex) {
        log.error("Invalid fields");
        return new ResponseEntity<>(new ErrorResponse(ex.getFieldError().getDefaultMessage()), HttpStatus.BAD_REQUEST);
    }
}
