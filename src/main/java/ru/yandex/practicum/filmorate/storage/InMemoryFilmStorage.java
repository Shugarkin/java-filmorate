package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.exception.FilmIsNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import javax.validation.ValidationException;
import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private int filmNextId = 1;

    private Map<Integer, Film> films = new HashMap();

    public List<Film> getAllFilms() {
        log.info("Коллекция фильмов получена, текущее количество {}", films.size());
        return new ArrayList<>(films.values());
    }

    public Film createFilms(Film film) {
        log.info("Фильм добавлен в коллекцию");
        film.setId(filmNextId);
        film.setLike(new TreeSet<>());
        films.put(filmNextId++, film);

        return film;
    }

    public Film updateFilm(Film film) {
        filmCheckId(film);
        if (films.containsKey(film.getId())) {
            log.info("Фильм c id={} обновлен в коллекции", film.getId());
            film.setLike(new TreeSet<>());
            films.put(film.getId(), film);
            return film;
        }
        log.info("Попытка изменить фильм по не существующему id");
        throw new ValidationException("Фильма с данным id нет");
    }

    private void filmCheckId(Film film) {
        if (film.getId() < 0) {
            log.info("Попытка добавить фильм с id меньше нуля");
            throw new ValidationException("id не может быть меньше 0");
        }
    }

    public Map<Integer, Film> getFilms() {
        return films;
    }

    public Film getFilmForId(int id) {
        if (!films.containsKey(id)) {
            throw new FilmIsNotFoundException("Фильм с таким id не найден");
        }
        log.info("Получен фильс с id={}", id);
        return films.get(id);
    }
}
