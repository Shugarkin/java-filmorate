package ru.yandex.practicum.filmorate.exception;

public class UserIsNotFoundException extends RuntimeException {
    public UserIsNotFoundException (final String massage) {
        super(massage);
    }
}
