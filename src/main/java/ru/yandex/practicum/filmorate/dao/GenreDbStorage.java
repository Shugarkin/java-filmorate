package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Qualifier("GenreDbStorage")
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
        return jdbcTemplate.queryForObject(genre, this::findGenre, id);
    }

    public void addGenre(Film film) {
        String a = "delete from GENRE_FILM where FILM_ID = ?";
        jdbcTemplate.update(a, film.getId());


        String addGenre = "insert into GENRE_FILM (FILM_ID, GENRE_ID) values(?,?)";

        Set<Integer> genres = film.getGenres()
                .stream()
                .map(s -> s.getId())
                .collect(Collectors.toSet());
        for (Integer genre : genres) {
            jdbcTemplate.update(addGenre, film.getId(), genre);
        }
    }


    private Genre findGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("GENRE_ID"))
                .name(resultSet.getString("GENRE_NAME"))
                .build();
    }
}
