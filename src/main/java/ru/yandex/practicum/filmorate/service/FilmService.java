package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserIsNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.*;

@Service
@Slf4j
public class FilmService {

    private FilmStorage filmStorage;

    private LikeService likeService;

    private GenreService genreService;

    @Autowired
    public FilmService(FilmStorage filmStorage, LikeService likeService, GenreService genreService) {
        this.filmStorage = filmStorage;
        this.likeService = likeService;
        this.genreService = genreService;
    }

    public void addLike(Integer filmId, Integer userId) {
        if (userId < 0) {
            throw new ValidationException("id пользователя должен быть положительным");
        }
        likeService.addLike(filmId, userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        if (userId < 0) {
            throw new ValidationException("id пользователя должен быть положительным");
        }
        likeService.deleteLike(filmId, userId);
    }

    public List<Film> getPopularFilms(Integer end) {
        log.info("Получен список популярных фильмов колличесвом {} фильмов.", end);
        List<Film> list = likeService.getPopularFilms(end);
        genreService.load(list);
        return list;
    }

    public List<Film> getAllFilms() {
        List<Film> films = filmStorage.getAllFilms();;
        genreService.load(films);
        return films;
    }

    public Film createFilms(Film film) {
        filmStorage.createFilms(film);
        if (film.getGenres() != null) {
            genreService.addGenre(film);
        }

        return film;
    }

    public Film updateFilm(Film film) {
        if (film.getGenres() != null) {
            genreService.deleteGenre(film.getId());
        }
        filmStorage.updateFilm(film);
        if (film.getGenres() != null) {
            genreService.addGenre(film);
        }
        return film;
    }

    public Film getFilmForId(int id) {
        Film film = filmStorage.getFilmForId(id);
        genreService.load(List.of(film));
        return film;
    }

    public void filmDeleteById(int filmId) { //метод удаления фильма по id
        if (filmStorage.getFilmForId(filmId) == null) {
            throw new UserIsNotFoundException("Фильма такого нету((");
        }
        filmStorage.deleteFilmById(filmId);
    }
}