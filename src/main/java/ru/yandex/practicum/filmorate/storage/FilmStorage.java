package ru.yandex.practicum.filmorate.storage;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    List<Film> getAllFilms();

    Film createFilms(Film film);

    Film updateFilm(Film film);

    Film getFilmForId(int id);
}
