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
    private FilmStorage inMemoryFilmStorage;

    private FilmDbStorage filmDbStorage;

    @Autowired
    public FilmService(FilmStorage inMemoryFilmStorage, FilmDbStorage filmDbStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.filmDbStorage = filmDbStorage;
    }

    public Optional<Film> addLike(Integer filmId, Integer userId) {
//        Map<Integer, Film> mapFilm = inMemoryFilmStorage.getMapFilms();
//        if (mapFilm.containsKey(filmId)) {
//            inMemoryFilmStorage.addLike(filmId, userId);
//            Film film = mapFilm.get(filmId);
//            log.info("Пользователем с id={} поставлен лайк фильму с id={}.", userId, filmId);
//            return film;
//        }
//        throw new FilmIsNotFoundException("Фильм не найден.");
        return filmDbStorage.addLike(filmId, userId);
    }

    public Film deleteLike(Integer filmId, Integer userId) {
//        Map<Integer, Film> mapFilm = inMemoryFilmStorage.getMapFilms();
//        if (mapFilm.containsKey(filmId)) {
//            inMemoryFilmStorage.deleteLike(filmId, userId);
//            Film film = mapFilm.get(filmId);
//            log.info("Пользователем с id={} был удален лайк с фильма с id={}.", userId, filmId);
//            return film;
//        }
//        throw new FilmIsNotFoundException("Фильм не найден.");
        return null;
    }

    public List<Optional<Film>> getPopularFilms(Integer end) {
        log.info("Получен список популярных фильмов колличесвом {} фильмов.", end);
        return filmDbStorage.getPopularFilms(end);
    }

    public List<Optional<Film>> getAllFilms() {
        return filmDbStorage.getAllFilms();
    }

//    public List<Film> getAllFilms() {
//        return inMemoryFilmStorage.getAllFilms();
//    }

    public Film createFilms(Film film) {
        return filmDbStorage.createFilms(film);
    }

    public Film updateFilm(Film film) {
        return filmDbStorage.updateFilm(film);
    }

//    public Film getFilmForId(int id) {
//        return inMemoryFilmStorage.getFilmForId(id).orElseThrow(() -> new ValidationException("При получении id пришел null"));
//    }

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
        return filmDbStorage.getMpaByid(id);
    }
}