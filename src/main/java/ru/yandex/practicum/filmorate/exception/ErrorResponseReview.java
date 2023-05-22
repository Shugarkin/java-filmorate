package ru.yandex.practicum.filmorate.exception;

import lombok.Data;

@Data
public class ErrorResponseReview {

    private String error;

    public ErrorResponseReview(String error) {
        this.error = error;
    }
}
