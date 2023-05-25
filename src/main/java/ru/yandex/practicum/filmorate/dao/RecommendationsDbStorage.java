package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserIsNotFoundException;
import ru.yandex.practicum.filmorate.storage.RecommendationsStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class RecommendationsDbStorage implements RecommendationsStorage {

    private final JdbcTemplate jdbcTemplate;

    public RecommendationsDbStorage(JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Integer> getRecommendation(int userId) {
        List<Integer> list;
        try {
           list = getListRecommendation(userId);
        } catch (RuntimeException e) {
            throw new UserIsNotFoundException("У данного пользователя нет рекомендаций.");
        }
        return list;
    }

    private List<Integer> getListRecommendation(int userId) {
        String sql = "SELECT FILM_ID " +
                "FROM LIKE_VAULT " +
                "WHERE USER_ID in " +
                "(SELECT user_id " +
                "FROM LIKE_VAULT " +
                "WHERE FILM_ID in " +
                "(SELECT film_id " +
                "FROM LIKE_VAULT " +
                "WHERE USER_ID  = ?) AND NOT USER_ID = ? " +
                "GROUP BY USER_ID " +
                "ORDER BY count(USER_ID) DESC " +
                "LIMIT 1) AND not FILM_ID IN (SELECT FILM_ID FROM LIKE_VAULT WHERE USER_ID = ?)"; //получили всех пользователей и фильмы
        return jdbcTemplate.query(sql, this::findRecommendation, userId, userId, userId);
    }

    private Integer findRecommendation(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getInt("FILM_ID");
    }
}
