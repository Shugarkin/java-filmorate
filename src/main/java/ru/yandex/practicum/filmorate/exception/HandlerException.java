package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice("ru.yandex.practicum.filmorate.controller")
@Slf4j
public class HandlerException {

    @ExceptionHandler({FilmIsNotFoundException.class, UserIsNotFoundException.class, IncorrectIDException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserIncorrect(final RuntimeException e) {
        return new ErrorResponse("Ошибка пользователя", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse serverError(final Throwable e) {
        return new ErrorResponse("Что-то пошло не так(", e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse badRequest(final Exception e) {
        return new ErrorResponse("Ошибка валидации", e.getMessage());
    }
}
