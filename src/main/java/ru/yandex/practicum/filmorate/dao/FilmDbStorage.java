package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmIsNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectIDException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    private final UserDbStorage userDbStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, UserDbStorage userDbStorage){
        this.jdbcTemplate=jdbcTemplate;
        this.userDbStorage = userDbStorage;
    }

    public List<Optional<Film>> getAllFilms() {
            String sqlQuery = "select FILM_ID, FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID, GENRE_ID, LIKES from FILMS";
            return jdbcTemplate.query(sqlQuery, this::findFilm);
    }


    @Override
    public Film createFilms(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        if(film.getGenres() == null) {
            film.setGenres(Set.of());
        }
        if(film.getLikes() == null) {
            film.setLikes(Set.of());
        }
        Set<Genre> genre = film.getGenres().stream()
                .map(Genre::getId)
                .map(this::getGenre)
                .collect(Collectors.toSet());

        String sqlQuery = "insert into films (FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID, GENRE_ID, LIKES)"
                + " values(?,?,?,?,?,?,?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery,  new String[]{"FILM_ID"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            stmt.setString(6, genre.toString());
            stmt.setString(7, String.valueOf(film.getLikes()));

            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKey().intValue());
        film.setGenres(genre);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if(getFilmForId(film.getId()) == null) {
            throw new FilmIsNotFoundException("Нет такого фильма");
        }
        if(film.getGenres() == null) {
            film.setGenres(Set.of());
        }
        if(film.getLikes() == null) {
            film.setLikes(Set.of());
        }
        Set<Genre> genre = film.getGenres()
                .stream()
                .map(Genre::getId)
                .map(this::getGenre)
                .sorted(Comparator.comparingInt(Genre::getId))
                .collect(Collectors.toSet());

        film.setGenres(genre);

        String sqlQuery = "update FILMS set " +
                "FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, MPA_ID = ?, GENRE_ID = ?, LIKES = ?  " +
                "where FILM_ID = ?";

        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                genre.toString(),
                String.valueOf(film.getLikes()),
                film.getId());
        return film;
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {

    }

    @Override
    public Optional<Film> getFilmForId(int id)  {
            String sqlQuery = "select FILM_ID, FILM_NAME, " +
                    "DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID, GENRE_ID, LIKES from FILMS where FILM_ID = ?";
            return jdbcTemplate.queryForObject(sqlQuery, this::findFilm, id);
    }

    private Optional<Film> findFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Mpa mpa = getMpaByid(Integer.parseInt(resultSet.getString("MPA_ID")));

        String genre = resultSet.getString("GENRE_ID").replaceAll("[^\\d,]", "")
                .replaceAll(",,", ",");
        Set<Genre> genreList;
        if(!genre.isBlank()) {
            genreList = Arrays.asList(genre
                    .split(","))
                    .stream()
                    .map(s -> Integer.parseInt(s))
                    .map(this::getGenre)
                    .collect(Collectors.toSet());
        } else {
            genreList = Set.of();
        }

        String like = resultSet.getString("LIKES").replaceAll("[^\\d,]", "");
        Set<String> likeList;
        if(!like.isBlank()) {
            likeList = Arrays.asList(like
                            .split(","))
                            .stream()
                            .collect(Collectors.toSet());
        } else {
            likeList = Set.of();
        }

        return Optional.of(Film.builder()
                .id(resultSet.getInt("FILM_ID"))
                .name(resultSet.getString("FILM_NAME"))
                .description(resultSet.getString("DESCRIPTION"))
                .releaseDate(resultSet.getDate("RELEASE_DATE").toLocalDate())
                .duration(resultSet.getLong("DURATION"))
                .mpa(mpa)
                .genres(genreList)
                .likes(likeList)
                .build());
    }

    public List<Genre> getGenreList () {
        String genre = "select * from GENRE ";
        return jdbcTemplate.query(genre, this::findGenre);
    }

    public Genre getGenre (int id) {
        String genre = "select * from GENRE " + "where GENRE_ID = ?";
        return jdbcTemplate.queryForObject(genre, this::findGenre, id);
    }

    private Genre findGenre (ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("GENRE_ID"))
                .name(resultSet.getString("GENRE_NAME"))
                .build();
    }

    public List<Mpa> getMpaList() {
        String sqlMpa = "select * from MPA ";
        return jdbcTemplate.query(sqlMpa,this::findMpa);
    }



    private Mpa findMpa(ResultSet resultSet, int i) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getInt("MPA_ID"))
                .name(resultSet.getString("MPA_NAME"))
                .build();
    }

    public Mpa getMpaByid(Integer id) {
        String sqlMpa = "select * from MPA " + "where MPA_ID = ?";
        return jdbcTemplate.queryForObject(sqlMpa, this::findMpa, id);
    }

    @Override
    public Optional<Film> addLike(Integer filmId, Integer userId) {
        if(getFilmForId(filmId) == null || userDbStorage.getUserForId(userId) == null) {
            throw new IncorrectIDException("Нет фильма, либо пользователя с данными id");
        }
        String sqlLike = "insert into LIKE_VAULT (FILM_ID, USER_ID)"
                + " values(?,?)";
        jdbcTemplate.update(sqlLike, filmId, userId);

        String sqlFriends = "select USER_ID from LIKE_VAULT" + " where FILM_ID = ?";
        Set<String> list = jdbcTemplate.query(sqlFriends, this::getUser, filmId).stream().collect(Collectors.toSet());

        String sqlFriendship = "update FILMS set " + " LIKES = ? " + " where FILM_ID = ?";
        jdbcTemplate.update(sqlFriendship, String.join(",", list), filmId);

        return getFilmForId(filmId);
    }

    private String getUser(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getString("USER_ID");
    }

    public List<Optional<Film>> getPopularFilms(Integer end) {

        return getAllFilms().stream()
                .sorted((o1, o2) -> o2.get().getLikes().size() - o1.get().getLikes().size())
                 .limit(end)
                 .collect(Collectors.toList());
    }
}
