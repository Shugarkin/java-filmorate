package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

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
        if(film.getDescription().length() > 200) {
            log.info("Попытка ввести описание более 200 символов");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if(film.getReleaseDate().isBefore(DAYX)) {
            log.info("Попытка добавить фильм с датой позднее 28.12.1895");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if(film.getDuration() <= 0) {
            log.info("Попытка добавить фильм с отрицательной продолжительностью");
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
        log.info("Фильм добавлен в коллекцию");
        film.setId(filmNextId);
        films.put(filmNextId++, film);

        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if(film.getId() < 0) {
            log.info("Попытка добавить фильм с id меньше нуля");
            throw new ValidationException("id не может быть меньше 0");
        }
        if(film.getDescription().length() > 200) {
            log.info("Попытка ввести описание более 200 символов");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if(film.getReleaseDate().isBefore(DAYX)) {
            log.info("Попытка добавить фильм с датой позднее 28.12.1895");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if(film.getDuration() <= 0) {
            log.info("Попытка добавить фильм с отрицательной продолжительностью");
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
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
}
