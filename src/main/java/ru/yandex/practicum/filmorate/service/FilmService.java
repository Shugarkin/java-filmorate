package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.exception.FilmIsNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.*;

@Service
@Slf4j
public class FilmService {

    private FilmStorage filmDbStorage;

    private LikeStorage likeStorage;

    private GenreStorage genreStorage;

    private MpaStorage mpaStorage;

    @Autowired
    public FilmService(FilmDbStorage filmDbStorage, LikeStorage likeStorage, GenreStorage genreStorage, MpaStorage mpaStorage) {
        this.filmDbStorage = filmDbStorage;
        this.likeStorage = likeStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    public void addLike(Integer filmId, Integer userId) {
        if (userId < 0) {
            throw new ValidationException("id пользователя должен быть положительным");
        }
        likeStorage.addLike(filmId, userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        if (userId < 0) {
            throw new ValidationException("id пользователя должен быть положительным");
        }
        likeStorage.deleteLike(filmId, userId);
    }

    public List<Film> getPopularFilms(Integer end) {
        log.info("Получен список популярных фильмов колличесвом {} фильмов.", end);
        return likeStorage.getPopularFilms(end);
    }

    public List<Film> getAllFilms() {
        return filmDbStorage.getAllFilms();
    }

    public Film createFilms(Film film) {
        return filmDbStorage.createFilms(film);
    }

    public Film updateFilm(Film film) {
        return filmDbStorage.updateFilm(film);
    }

    public Film getFilmForId(int id) {
        return filmDbStorage.getFilmForId(id).orElseThrow(() -> new FilmIsNotFoundException("При получении id пришел null"));
    }

    public List<Genre> getGenre() {
        return genreStorage.getGenreList();
    }

    public List<Mpa> getMpa() {
        return mpaStorage.getMpaList();
    }

    public Mpa getMpaById(Integer id) {
        return mpaStorage.getMpaById(id);
    }
}