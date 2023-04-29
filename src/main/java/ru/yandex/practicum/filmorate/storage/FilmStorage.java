package ru.yandex.practicum.filmorate.storage;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    List<Film> getAllFilms();

    Film createFilms(Film film);

    Film updateFilm(Film film);

    Optional<Film> getFilmForId(int id);

}
