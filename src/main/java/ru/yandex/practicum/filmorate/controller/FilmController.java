package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ErrorResponse;
import ru.yandex.practicum.filmorate.exception.FilmIsNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectIDException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {

    private InMemoryFilmStorage inMemoryFilmStorage;

    private FilmService filmService;

    @Autowired
    public FilmController(InMemoryFilmStorage inMemoryFilmStorage, FilmService filmService) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getAllFilms() { //получение все фильмов
        return inMemoryFilmStorage.getAllFilms();
    }

    @PostMapping
    public Film createFilms(@Valid @RequestBody Film film) { //создание фильма
        return inMemoryFilmStorage.createFilms(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) { //обновление фильма
       return inMemoryFilmStorage.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}") //добавление лайка
    @ResponseBody
    public Film addLike(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        checkId(id, userId);
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}") //удаление лайка
    @ResponseBody
    public Film deleteLike(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        checkId(id, userId);
        return filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular") //список популярных фильмов
    @ResponseBody
    public List<Film> getPopularFilms(@RequestParam(required = false) Integer count) {
        return filmService.getPopularFilms(count);
    }

    @GetMapping("/{id}") //получение фильма по айди
    @ResponseBody
    public Film getFilmForId(@PathVariable("id") int id) {
        return inMemoryFilmStorage.getFilmForId(id);
    }

    @ExceptionHandler({FilmIsNotFoundException.class, IncorrectIDException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleIncorrect(final RuntimeException e) {
        return new ErrorResponse("Ошибка пользователя", e.getMessage());
    }

    private void checkId(Integer id, Integer userId) {
        if (id == null) {
            throw new IncorrectIDException("Параметр id фильма равен null.");
        }
        if (userId == null) {
            throw new IncorrectIDException("Параметр id пользователя при добавления лайка равен null.");
        }
        if (id <= 0) {
            throw new IncorrectIDException("Параметр id фильма имеет отрицательное значение.");
        }
        if (userId <= 0) {
            throw new IncorrectIDException("Параметр id пользователя при добавления лайка имеет отрицательное значение.");
        }
    }
}
