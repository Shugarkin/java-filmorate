package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class Film {

    private int id;

    @NotBlank
    @NotNull
    private String name;

    private String description;

    private LocalDate releaseDate;

    private long duration;
}
