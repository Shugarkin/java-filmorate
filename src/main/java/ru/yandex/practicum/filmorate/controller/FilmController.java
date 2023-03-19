package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private int filmNextId = 1;
    private static final LocalDate DAYX = LocalDate.of(1895, 12, 28);

    private Map<Integer, Film> films = new HashMap();

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Коллекция фильмов получена, текущее количество {}", films.size());
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film createFilms(@Valid @RequestBody Film film) {
        filmCheck(film);
        log.info("Фильм добавлен в коллекцию");
        film.setId(filmNextId);
        films.put(filmNextId++, film);

        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        filmCheck(film);
        for (Integer integer : films.keySet()) {
            if(integer == film.getId()) {
                log.info("Фильм c id={} обновлен в коллекции", film.getId());
                films.put(film.getId(), film);
                return film;
            }
        }
        log.info("Попытка изменить фильм по не существующему id");
        throw new ValidationException("Фильма с данным id нет");
    }

    private void filmCheck(Film film) {
        if(film.getId() < 0) {
            log.info("Попытка добавить фильм с id меньше нуля");
            throw new ValidationException("id не может быть меньше 0");
        }
        if(film.getReleaseDate().isBefore(DAYX)) {
            log.info("Попытка добавить фильм с датой позднее 28.12.1895");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }
}
