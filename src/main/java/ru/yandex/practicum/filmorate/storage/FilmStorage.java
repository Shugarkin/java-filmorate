package ru.yandex.practicum.filmorate.storage;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.List;

public interface FilmStorage {
    public List<Film> getAllFilms();

    public Film createFilms(@Valid @RequestBody Film film);

    public Film updateFilm(@Valid @RequestBody Film film);
}
