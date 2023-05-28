package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

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
    public List<Film> getAllFilms() { //получение все фильмов
        return filmService.getAllFilms();
    }

    @PostMapping
    public Film createFilms(@Valid @RequestBody Film film) { //создание фильма
        return filmService.createFilms(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) { //обновление фильма
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
    public List<Film> getPopularFilms(@Positive @RequestParam(defaultValue = "10") Integer count,
                                      @Positive @RequestParam(required = false) Integer genreId,
                                      @RequestParam(required = false) Integer year) {
        return filmService.getPopularFilms(count, genreId, year);
    }

    @GetMapping("/{id}") //получение фильма по айди
    public Film getFilmForId(@PathVariable("id") int id) {
        return filmService.getFilmForId(id);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsByDirectorSorted(@PathVariable int directorId, @RequestParam String sortBy) {
        return filmService.getFilmsByDirectorSorted(directorId, sortBy);
    }

    @DeleteMapping("/{id}") //удаление фильма по id
    public void filmDeleteById(@PathVariable("id") final Integer filmId) {
        filmService.filmDeleteById(filmId);
    }

    @GetMapping("/search") // Возвращает список фильмов, отсортированных по популярности.
    public List<Film> returnListFilmsSortedByPopularity(@RequestParam(required = false) String query,
                                                        @RequestParam(required = false) List<String> by) {
        return filmService.getListFilmsSortedByPopularity(query, by);
    }

}
