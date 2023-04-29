package ru.yandex.practicum.filmorate.storage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    List<Optional<Film>> getAllFilms();

    Optional<Film> createFilms(Film film);

    Optional<Film> updateFilm(Film film);

    List<Genre> getGenreList();

    Genre getGenre(int id);

    List<Mpa> getMpaList();

    Mpa getMpaById(Integer id);

    void addLike(Integer filmId, Integer userId);

    void deleteLike(Integer filmId, Integer userId);

    Optional<Film> getFilmForId(int id);

    List<Optional<Film>> getPopularFilms(Integer end);
}
