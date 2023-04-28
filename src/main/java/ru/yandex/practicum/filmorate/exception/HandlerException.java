package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.sql.SQLException;

@RestControllerAdvice("ru.yandex.practicum.filmorate.controller")
@Slf4j
public class HandlerException {

//    @ExceptionHandler({FilmIsNotFoundException.class, UserIsNotFoundException.class,
//            IncorrectIDException.class, ValidationException.class})
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public ErrorResponse handleUserIncorrect(final RuntimeException e) {
//        log.warn("Ошибка пользователя.");
//        return new ErrorResponse("Ошибка пользователя", e.getMessage());
//    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse serverError(final Throwable e) {
        log.warn("Неизвестная ошибка.");
        return new ErrorResponse("Что-то пошло не так(", e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse badRequest(final Exception e) {
        log.warn("Ошибка валидации.");
        return new ErrorResponse("Ошибка валидации", e.getMessage());
    }
}
