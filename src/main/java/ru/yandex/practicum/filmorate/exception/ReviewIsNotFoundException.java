package ru.yandex.practicum.filmorate.exception;

public class ReviewIsNotFoundException extends RuntimeException {
    public ReviewIsNotFoundException(final String massage) {
        super(massage);
    }
}
