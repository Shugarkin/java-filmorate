package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.ReleaseDate;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class Film {

    @NotNull
    private int id;

    @NotBlank
    private String name;

    @Size(max = 200)
    @NotNull
    private String description;

    @NotNull
    @ReleaseDate
    private LocalDate releaseDate;

    @Positive
    private long duration;

    private List<Genre> genres;

    private Mpa mpa;

}
