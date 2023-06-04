package ru.yandex.practicum.filmorate.exception;

public class DirectorNotFoundException extends RuntimeException {
    public DirectorNotFoundException(final String massage) {
        super(massage);
    }
}
