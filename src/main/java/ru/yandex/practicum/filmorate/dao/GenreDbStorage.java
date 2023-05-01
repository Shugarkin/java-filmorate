package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

@Component
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getGenreList() {
        String genre = "select * from GENRE ";
        return jdbcTemplate.query(genre, this::findGenre);
    }

    @Override
    public Genre getGenre(int id) {
        String genre = "select * from GENRE where GENRE_ID = ?";
        try {
            return jdbcTemplate.queryForObject(genre, this::findGenre, id);
        } catch (RuntimeException e) {
            throw new GenreNotFoundException("Нет такого жанра");
        }
    }

    public void deleteGenre(int id) {
        String a = "delete from GENRE_FILM where FILM_ID = ?";
        jdbcTemplate.update(a, id);
    }

    public void addGenre(Film film) {
        String addGenre = "insert into GENRE_FILM (FILM_ID, GENRE_ID) values(?,?)";

        List<Integer> genres = film.getGenres()
                .stream()
                .map(s -> s.getId())
                .collect(Collectors.toList());

            jdbcTemplate.batchUpdate(addGenre, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt(1, film.getId());
                    ps.setInt(2, genres.get(i));
                }

                @Override
                public int getBatchSize() {
                    return genres.size();
                }
            });
    }


    private Genre findGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("GENRE_ID"))
                .name(resultSet.getString("GENRE_NAME"))
                .build();

    }

    @Override
    public void load(List<Film> films) {
        final Map<Integer, Film> filmById = films.stream().collect(Collectors.toMap(Film::getId, identity()));

        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));

        final String sqlQuery = "select * from GENRE g, " +
                "GENRE_FILM fg where fg.GENRE_ID = g.GENRE_ID AND fg.FILM_ID in (" + inSql + ")";

        jdbcTemplate.query(sqlQuery, (rs) -> {
            final Film film = filmById.get(rs.getInt("FILM_ID"));
            film.addGenre(findGenre(rs, 0));
        }, films.stream().map(Film::getId).toArray());
    }
}
