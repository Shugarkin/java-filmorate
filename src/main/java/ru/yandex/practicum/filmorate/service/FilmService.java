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

import java.util.*;

@Service
@Slf4j
public class FilmService {

    private FilmStorage filmDbStorage;

    @Autowired
    public FilmService(FilmDbStorage filmDbStorage) {
        this.filmDbStorage = filmDbStorage;
    }

    public void addLike(Integer filmId, Integer userId) {
        if(userId < 0 ) {
            throw new ValidationException("id пользователя должен быть положительным");
        }
        filmDbStorage.addLike(filmId, userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        if(userId < 0 ) {
            throw new ValidationException("id пользователя должен быть положительным");
        }
        filmDbStorage.deleteLike(filmId, userId);
    }

    public List<Optional<Film>> getPopularFilms(Integer end) {
        log.info("Получен список популярных фильмов колличесвом {} фильмов.", end);
        return filmDbStorage.getPopularFilms(end);
    }

    public List<Optional<Film>> getAllFilms() {
        return filmDbStorage.getAllFilms();
    }

    public Optional<Film> createFilms(Film film) {
        return filmDbStorage.createFilms(film);
    }

    public Optional<Film> updateFilm(Film film) {
        return filmDbStorage.updateFilm(film);
    }

    public Film getFilmForId(int id) {
        return filmDbStorage.getFilmForId(id).orElseThrow(() -> new FilmIsNotFoundException("При получении id пришел null"));
    }

    public List<Genre> getGenre() {
        return filmDbStorage.getGenreList();
    }

    public Genre getGenreById(Integer id) {
        return filmDbStorage.getGenre(id);
    }

    public List<Mpa> getMpa() {
        return filmDbStorage.getMpaList();
    }

    public Mpa getMpaById(Integer id) {
        return filmDbStorage.getMpaById(id);
    }
}