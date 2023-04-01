package ru.yandex.practicum.filmorate.exception;

public class FilmIsNotFoundException extends RuntimeException{
    public FilmIsNotFoundException(final String massage) {
        super(massage);
    }
}
