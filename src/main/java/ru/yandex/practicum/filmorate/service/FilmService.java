package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserIsNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class FilmService {

    private FilmStorage filmStorage;

    private LikeService likeService;

    private GenreService genreService;
    private DirectorService directorService;

    @Autowired
    public FilmService(FilmStorage filmStorage, LikeService likeService, GenreService genreService, DirectorService directorService) {
        this.filmStorage = filmStorage;
        this.likeService = likeService;
        this.genreService = genreService;
        this.directorService = directorService;
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

    public List<Film> getPopularFilms(Integer end, Integer genreId, Integer year) {
        log.info("Получен список популярных фильмов колличесвом {} фильмов.", end);
        List<Film> list = likeService.getPopularFilms(end, genreId, year);
        genreService.load(list);
        return list;
    }

    public List<Film> getAllFilms() {
        List<Film> films = filmStorage.getAllFilms();
        ;
        genreService.load(films);
        return films;
    }

    public Film createFilms(Film film) {
        filmStorage.createFilms(film);
        if (film.getGenres() != null) {
            genreService.addGenre(film);
        }
        if (film.getDirectors() != null) {
            for (Director director : film.getDirectors()) {
                if (directorService.isDirectorExists(director.getId())) {
                    directorService.addDirector(film.getId(), director.getId());
                } else {
                    throw new DirectorNotFoundException("Режиссер не найден");
                }
            }
        }
        film.setDirectors(filmStorage.getDirector(film.getId()));
        return film;
    }

    public Film updateFilm(Film film) {
        if (film.getGenres() != null) {
            genreService.deleteGenre(film.getId());
        }
        filmStorage.updateFilm(film);
        if (film.getGenres() != null) {
            genreService.addGenre(film);
        } else {
            film.setGenres(new LinkedHashSet<>());
        }
        directorService.updateDirectorInFilm(film);
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

    public List<Film> getFilmsByDirectorSorted(int directorId, String sortBy) {
        if (sortBy == null) {
            throw new ValidationException("Пустой параметр сортировки");
        }
        if (!directorService.isDirectorExists(directorId)) {
            throw new DirectorNotFoundException("Режиссер не найден");
        }
        if (sortBy.equals("likes")) {
            List<Film> films = filmStorage.getFilmsByDirectorSortedByLikes(directorId);
            genreService.load(films);
            return films;
        } else if (sortBy.equals("year")) {
            List<Film> films = filmStorage.getFilmsByDirectorSortedByYears(directorId);
            genreService.load(films);
            return films;
        } else {
            throw new ValidationException("Неверно указан параметр");
        }
    }

    public void addDirectorToFilm(int filmId, int directorId) {
        filmStorage.addDirectorToFilm(filmId, directorId);
    }

    public Set<Director> getDirector(int filmId) {
        return filmStorage.getDirector(filmId);
    }

    public List<Film> getListFilmsSortedByPopularity(String query, List<String> by) {
        if (query == null) {
            throw new ValidationException("Отсутствует строка запроса для поиска");
        }
        query = query.toLowerCase();
        List<Film> listNameFilms = null;
        if (by.size() == 2) {
            listNameFilms = filmStorage.findFilmsByDirectorTitle(query);
            genreService.load(listNameFilms);
            return listNameFilms;
        } else if (by.size() == 1) {
            if (by.get(0).equals("director")) {
                listNameFilms = filmStorage.findFilmsByDirector(query);
                genreService.load(listNameFilms);
            } else if (by.get(0).equals("title")) {
                listNameFilms = filmStorage.findFilmsByTitle(query);
                genreService.load(listNameFilms);
            }
            return listNameFilms;
        } else {
            throw new ValidationException("Неверно указан параметр");
        }
    }
}