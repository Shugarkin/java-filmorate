package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmIsNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private FilmStorage inMemoryFilmStorage;

    @Autowired
    public FilmService(FilmStorage inMemoryFilmStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
    }

    public Film addLike(Integer filmId, Integer userId) {
        Map<Integer, Film> mapFilm = inMemoryFilmStorage.getMapFilms();
        if (mapFilm.containsKey(filmId)) {
            inMemoryFilmStorage.addLike(filmId, userId);
            Film film = mapFilm.get(filmId);
            log.info("Пользователем с id={} поставлен лайк фильму с id={}.", userId, filmId);
            return film;
        }
        throw new FilmIsNotFoundException("Фильм не найден.");
    }

    public Film deleteLike(Integer filmId, Integer userId) {
        Map<Integer, Film> mapFilm = inMemoryFilmStorage.getMapFilms();
        if (mapFilm.containsKey(filmId)) {
            inMemoryFilmStorage.deleteLike(filmId, userId);
            Film film = mapFilm.get(filmId);
            log.info("Пользователем с id={} был удален лайк с фильма с id={}.", userId, filmId);
            return film;
        }
        throw new FilmIsNotFoundException("Фильм не найден.");
    }

    public List<Film> getPopularFilms(Integer end) {
        log.info("Получен список популярных фильмов колличесвом {} фильмов.", end);
        return inMemoryFilmStorage.getAllFilms().stream()
                .sorted((o1, o2) -> o2.getLike().size() - o1.getLike().size()).limit(end).collect(Collectors.toList());
    }

    public List<Film> getAllFilms() {
        return inMemoryFilmStorage.getAllFilms();
    }

    public Film createFilms(Film film) {
        return inMemoryFilmStorage.createFilms(film);
    }

    public Film updateFilm(Film film) {
        return inMemoryFilmStorage.updateFilm(film);
    }

    public Film getFilmForId(Optional<Integer> id) {
        int newId = id.orElseThrow(() -> new ValidationException("При получении id пришел null"));
        return inMemoryFilmStorage.getFilmForId(newId);
    }

}