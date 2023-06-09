package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    List<Film> getAllFilms();

    Film createFilms(Film film);

    Film updateFilm(Film film);

    Film getFilmForId(int id);

    void deleteFilmById(int id);

    List<Film> getFilmsByDirectorSortedByLikes(int directorId);

    List<Film> getFilmsByDirectorSortedByYears(int directorId);

    void addDirectorToFilm(int filmId, int directorId);

    List<Film> getListFilm(List<Integer> list);

    List<Film> findFilmsByDirector(String query);

    List<Film> findFilmsByTitle(String query);

    List<Film> findFilmsByDirectorTitle(String query);

    Integer getFilm(Integer idReview);
}
