package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
