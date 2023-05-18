package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review {

    private int id;

    @NotNull private String content;

    @NotNull private boolean isPositive;

    @NotNull private int userId;

    @NotNull private int filmId;

    @NotNull private int useful;
}
