package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DirectorApplicationTests {
    private final DirectorService directorStorage;
    private final JdbcTemplate jdbcTemplate;

    Director firstDirector = Director.builder()
            .name("James Cameron")
            .build();

    Director secondDirector = Director.builder()
            .name("Christopher Nolan")
            .build();

    @BeforeEach
    public void cleanDb() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate,
                "directors", "film_director");
        jdbcTemplate.update("ALTER TABLE directors ALTER COLUMN director_id RESTART WITH 1");
    }

    @Test
    public void shouldCreateDirector() {
        Director newDirector = directorStorage.createDirector(firstDirector);
        assertThat(newDirector)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    public void shouldUpdateDirector() {
        Director newDirector = directorStorage.createDirector(firstDirector);

        secondDirector.setId(newDirector.getId());
        Director updateDirector = directorStorage.updateDirector(secondDirector);
        assertThat(updateDirector)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", secondDirector.getName());
    }

    @Test
    public void shouldDeleteDirector() {
        Director newDirector = directorStorage.createDirector(firstDirector);

        directorStorage.deleteDirector(newDirector.getId());
        List<Director> directors = directorStorage.getListAllDirectors();
        assertThat(directors)
                .isNotNull()
                .isEqualTo(Collections.emptyList());
    }

    @Test
    public void shouldGetDirectorById() {
        Director newDirector = directorStorage.createDirector(firstDirector);
        Director foundDirector = directorStorage.getDirectorById(newDirector.getId());
        assertThat(foundDirector)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", firstDirector.getName());
        DirectorNotFoundException e = assertThrows(DirectorNotFoundException.class, () -> directorStorage.getDirectorById(-1));
        assertEquals("Режиссер с id -1 не найден", e.getMessage());

        e = assertThrows(DirectorNotFoundException.class, () -> directorStorage.getDirectorById(999));
        assertEquals("Режиссер с id 999 не найден", e.getMessage());
    }

    @Test
    public void shouldGetListAllDirectors() {
        directorStorage.createDirector(firstDirector);
        directorStorage.createDirector(secondDirector);
        List<Director> allDirectors = directorStorage.getListAllDirectors();
        System.out.println(allDirectors);
        assertNotNull(allDirectors, "Cписок режиссеров пустой");
        assertEquals(allDirectors.size(), 2, "Количество режиссеров в списке не верное");
    }
}
