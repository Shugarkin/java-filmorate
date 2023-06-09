package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.ReleaseDate;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Builder
public class Film {

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

    private LinkedHashSet<Genre> genres;

    @NotNull
    private Mpa mpa;

    private Set<Director> directors;

    public void addGenre(Genre genre) {
        genres.add(genre);
    }

    public void addDirector(Director director) {
        directors.add(director);
    }
}
