package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmIsNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Director createDirector(Director director) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sqlQuery = "insert into directors (director_name) values (?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"DIRECTOR_ID"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);
        director.setId(Objects.requireNonNull(keyHolder.getKey().intValue()));
        log.info("Режиссер {} сохранён", director);
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        if (isDirectorExists(director.getId())) {
            String updateDirector = "update directors set director_name = ? where director_id = ?";
            jdbcTemplate.update(updateDirector, director.getName(), director.getId());
            log.info("Режиссер {} обновлён", director);
            return director;
        } else {
            log.warn("Режиссер с id = {} не найден", director.getId());
            throw new DirectorNotFoundException(String.format("Режиссер с id %d не найден", director.getId()));
        }
    }

    @Override
    public void deleteDirector(int id) {
        if (isDirectorExists(id)) {
            String deleteDirector = "delete from directors where director_id = ?";
            jdbcTemplate.update(deleteDirector, id);
            log.info("Режиссер с id = {} удалён", id);
        } else {
            log.warn("Режиссер с id = {} не найден", id);
            throw new DirectorNotFoundException(String.format("Режиссер с id %d не найден", id));
        }
    }

    @Override
    public Director getDirectorById(int id) {
        String sqlQuery = "select director_id, director_name from directors where director_id = ?";
        SqlRowSet directorRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (directorRows.next()) {
            return jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> mapRowDirector(rs), id);
        } else {
            log.warn("Режиссер с id = {} не найден", id);
            throw new DirectorNotFoundException(String.format("Режиссер с id %d не найден", id));
        }
    }

    @Override
    public List<Director> getListAllDirectors() {
        String sqlQuery = "select director_id, director_name from directors";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> mapRowDirector(rs));
    }

    @Override
    public void updateDirectorInFilm(Film film) {
        String sqlDelete = "delete from film_director where film_id = ?";
        jdbcTemplate.update(sqlDelete, film.getId());
        if (film.getDirectors() != null) {
            for (Director director : film.getDirectors()) {
                if (isDirectorExists(director.getId())) {
                    addDirector(film.getId(), director.getId());
                } else {
                    throw new DirectorNotFoundException("Режиссер не найден");
                }
            }
        }
    }

    @Override
    public boolean isDirectorExistsByName(String name) {
        String sqlQuery = "SELECT EXISTS (SELECT * FROM DIRECTORS WHERE DIRECTOR_NAME = ?)";
        boolean result = jdbcTemplate.queryForObject(sqlQuery, Boolean.class, name);
        if (result) {
            return result;
        } else {
            log.warn("Режиссер по имени = {} не найден", name);
            throw new FilmIsNotFoundException(String.format("Режиссер по имени = {} не найден", name));
        }
    }

    @Override
    public boolean isDirectorExists(int id) {
        String sqlQuery = "select director_name from directors where director_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        return userRows.next();
    }

    @Override
    public void addDirector(int filmId, int directorId) {
        String sqlQuery = "insert into film_director (film_id, director_id) values (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, directorId);
    }

    private Director mapRowDirector(ResultSet rs) throws SQLException {
        return Director.builder()
                .id(rs.getInt("director_id"))
                .name(rs.getString("director_name"))
                .build();
    }
}
