package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private int filmNextId = 1;
    private static final LocalDate DAYX = LocalDate.of(1895, 12, 28);

    private Map<Integer, Film> films = new HashMap();

    public List<Film> getAllFilms() {
        log.info("Коллекция фильмов получена, текущее количество {}", films.size());
        return new ArrayList<>(films.values());
    }

    public Film createFilms(Film film) {
        log.info("Фильм добавлен в коллекцию");
        film.setId(filmNextId);
        films.put(filmNextId++, film);

        return film;
    }

    public Film updateFilm(Film film) {
        filmCheckId(film);
        if(films.containsKey(film.getId())) {
            log.info("Фильм c id={} обновлен в коллекции", film.getId());
            films.put(film.getId(), film);
            return film;
        }
        log.info("Попытка изменить фильм по не существующему id");
        throw new ValidationException("Фильма с данным id нет");
    }

    private void filmCheckId(Film film) {
        if(film.getId() < 0) {
            log.info("Попытка добавить фильм с id меньше нуля");
            throw new ValidationException("id не может быть меньше 0");
        }
    }
}
