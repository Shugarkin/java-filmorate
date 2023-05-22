package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;

@Data
@Builder
public class Review {

    private int reviewId;

    @NotNull private String content;

    @NotNull private Boolean isPositive;

    @NotNull private Integer userId;

    @NotNull private Integer filmId;

    private int useful;
}
