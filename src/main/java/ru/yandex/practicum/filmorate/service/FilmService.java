package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmIsNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.*;

@Service
@Slf4j
public class FilmService {
    private InMemoryFilmStorage inMemoryFilmStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
    }

    public Film addLike(Integer filmId, Integer userId) {
        Map<Integer, Film> mapFilm = inMemoryFilmStorage.getFilms();
        if (mapFilm.containsKey(filmId)) {
            mapFilm.get(filmId).getLike().add(userId);
            Film film = mapFilm.get(filmId);
            log.info("Пользователем с id={} поставлен лайк фильму с id={}.", userId, filmId);
            return film;
        }
        throw new FilmIsNotFoundException("Фильм не найден.");
    }

    public Film deleteLike(Integer filmId, Integer userId) {
        Map<Integer, Film> mapFilm = inMemoryFilmStorage.getFilms();
        if (mapFilm.containsKey(filmId)) {
            mapFilm.get(filmId).getLike().remove(userId);
            Film film = mapFilm.get(filmId);
            log.info("Пользователем с id={} был удален лайк с фильма с id={}.", userId, filmId);
            return film;
        }
        throw new FilmIsNotFoundException("Фильм не найден.");
    }

    public List<Film> getPopularFilms(Integer end) {
        List<Film> list = new ArrayList<>(inMemoryFilmStorage.getAllFilms());
        list.sort((o1, o2) -> o2.getLike().size() - o1.getLike().size());

        if (end == null) {
            if (list.size() >= 10) {
                log.info("Получен список популярных фильмов.");
                return list.subList(0, 9);
            }
            log.info("Получен список популярных фильмов.");
            return list;
        }
        log.info("Получен список популярных фильмов колличесвом {} фильмов.", end);
        return list.subList(0, end);
    }
}