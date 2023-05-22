package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.*;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review {

    @NotNull int reviewId;

    @NotNull String content;

    @NotNull Boolean isPositive;

    @NotNull int userId;

    @NotNull int filmId;

    int useful;
}
