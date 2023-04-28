package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.validator.ReleaseDate;

import javax.validation.constraints.*;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Builder
public class Film {

    @NotNull
    private int id;

    private Set<String> likes;

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


    private Set<Genre> genres;


    private Mpa mpa;

}
