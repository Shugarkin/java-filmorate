package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

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
            String deleteDirector = "delete from directors where DIRECTOR_ID = ?; delete from FILM_DIRECTOR where FILM_ID = ?;";
            jdbcTemplate.update(deleteDirector, id, id);
            log.info("Режиссер с id = {} удалён", id);
        } else {
            log.warn("Режиссер с id = {} не найден", id);
            throw new DirectorNotFoundException(String.format("Режиссер с id %d не найден", id));
        }
    }

    @Override
    public void deleteDirectorByFilm(int id) {
        if (isDirectorExists(id)) {
            String deleteDirector = "delete from FILM_DIRECTOR where FILM_ID = ?;";
            jdbcTemplate.update(deleteDirector, id);
            log.info("Режиссер с id = {} удалён", id);
        } else {
            log.warn("Режиссер с id = {} не найден", id);
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
    public void load(List<Film> films) {
        final Map<Integer, Film> filmById = films.stream().collect(Collectors.toMap(Film::getId, identity()));

        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));

        final String sqlQuery = "select fd.FILM_ID, fd.DIRECTOR_ID, d.DIRECTOR_NAME " +
                "from FILM_DIRECTOR fd join DIRECTORS d on fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                " where fd.FILM_ID in (" + inSql + ")";

        jdbcTemplate.query(sqlQuery, (rs) -> {
            final Film film = filmById.get(rs.getInt("FILM_ID"));
            film.addDirector(findDirector(rs, 0));
            }, films.stream().map(Film::getId).toArray());
        List<Film> list = films;
    }

    private Director findDirector(ResultSet resultSet, int rowNum) throws SQLException {    return Director.builder()
            .id(resultSet.getInt("DIRECTOR_ID"))
            .name(resultSet.getString("DIRECTOR_NAME"))
            .build();
    }

    @Override
    public boolean isDirectorExists(int id) {
        String sqlQuery = "select director_name from directors where director_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        return userRows.next();
    }

    @Override
    public void addDirector (Film film) {
        String addGenre = "insert into FILM_DIRECTOR (FILM_ID, DIRECTOR_ID) values(?,?)";
        List<Integer> directors = film.getDirectors()
                .stream()
                .map(s -> s.getId())
                .collect(Collectors.toList());
        jdbcTemplate.batchUpdate(addGenre, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, film.getId());
                ps.setInt(2, directors.get(i));
            }
            @Override
            public int getBatchSize() {
                return directors.size();
            }
        });
    }

    private Director mapRowDirector(ResultSet rs) throws SQLException {
        return Director.builder()
                .id(rs.getInt("director_id"))
                .name(rs.getString("director_name"))
                .build();
    }
}
