package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DirectorApplicationTests {
    private final DirectorStorage directorStorage;
    private final JdbcTemplate jdbcTemplate;

    Director FirstDirector = Director.builder()
            .name("James Cameron")
            .build();

    Director SecondDirector = Director.builder()
            .name("Christopher Nolan")
            .build();

    @AfterEach
    public void cleanDb() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate,
                "directors", "FILM_DIRECTOR");
        jdbcTemplate.update("ALTER TABLE directors ALTER COLUMN director_id RESTART WITH 1");
    }

    @Test
    public void shouldCreateDirector() {
        Director newDirector = directorStorage.createDirector(FirstDirector);
        assertThat(newDirector)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    public void shouldUpdateDirector() {
        Director newDirector = directorStorage.createDirector(FirstDirector);

        SecondDirector.setId(newDirector.getId());
        Director updateDirector = directorStorage.updateDirector(SecondDirector);
        assertThat(updateDirector)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", SecondDirector.getName());
    }

    @Test
    public void shouldDeleteDirector() {
        Director newDirector = directorStorage.createDirector(FirstDirector);

        directorStorage.deleteDirector(newDirector.getId());
        List<Director> directors = directorStorage.getListAllDirectors();
        assertThat(directors)
                .isNotNull()
                .isEqualTo(Collections.emptyList());
    }

    @Test
    public void shouldGetDirectorById() {
        Director newDirector = directorStorage.createDirector(FirstDirector);
        Director foundDirector = directorStorage.getDirectorById(newDirector.getId());
        assertThat(foundDirector)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", FirstDirector.getName());
        DirectorNotFoundException e = assertThrows(DirectorNotFoundException.class, () -> directorStorage.getDirectorById(-1));
        assertEquals("Режиссер с id -1 не найден", e.getMessage());

        e = assertThrows(DirectorNotFoundException.class, () -> directorStorage.getDirectorById(999));
        assertEquals("Режиссер с id 999 не найден", e.getMessage());
    }

    @Test
    public void shouldGetListAllDirectors() {
        List<Director> directors = directorStorage.getListAllDirectors();
        assertThat(directors)
                .isNotNull()
                .isEqualTo(Collections.emptyList());
        directorStorage.createDirector(FirstDirector);
        directors = directorStorage.getListAllDirectors();
        assertNotNull(directors, "Cписок режиссеров пустой");
        assertEquals(directors.size(), 1, "Количество режиссеров в списке не верное");
        assertEquals(directors.get(0).getId(), 1, "Значение id режиссера в списке не совпадает");
    }
}
