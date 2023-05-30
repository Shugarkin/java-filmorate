package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping
    public List<Director> getListAllDirectors() {
        return directorService.getListAllDirectors();
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable @Positive int id) {
        return directorService.getDirectorById(id);
    }

    @PostMapping
    public Director createDirector(@Valid @RequestBody Director director) {
        return directorService.createDirector(director);
    }

    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director director) {
        return directorService.updateDirector(director);
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable @Positive int id) {
        directorService.deleteDirector(id);
    }
}
