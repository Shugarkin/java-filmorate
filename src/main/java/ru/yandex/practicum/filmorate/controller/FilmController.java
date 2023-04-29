package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/films")
@Validated
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Optional<Film>> getAllFilms() { //получение все фильмов
        return filmService.getAllFilms();
    }

    @PostMapping
    public Optional<Film> createFilms(@Valid @RequestBody Film film) { //создание фильма
        return filmService.createFilms(film);
    }

    @PutMapping
    public Optional<Film> updateFilm(@Valid @RequestBody Film film) { //обновление фильма
       return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}") //добавление лайка
    public void addLike(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}") //удаление лайка
    public void deleteLike(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular") //список популярных фильмов
    @Validated
    public List<Optional<Film>> getPopularFilms(@Positive @RequestParam(defaultValue = "10") Integer count) {
        return filmService.getPopularFilms(count);
    }

    @GetMapping("/{id}") //получение фильма по айди
    public Film getFilmForId(@PathVariable("id") int id) {
        return filmService.getFilmForId(id);
    }
}
