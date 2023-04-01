package ru.yandex.practicum.filmorate.exception;

public class IncorrectIDException extends RuntimeException {
    public IncorrectIDException(final String massage) {
        super(massage);
    }
}
