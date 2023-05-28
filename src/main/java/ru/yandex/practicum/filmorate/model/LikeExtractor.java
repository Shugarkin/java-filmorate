package ru.yandex.practicum.filmorate.model;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LikeExtractor implements ResultSetExtractor<Map<Integer, List<Integer>>> {
    public Map<Integer, List<Integer>> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Integer, List<Integer>> userLikes = new HashMap<>();
        while (rs.next()) {
            int userId = rs.getInt("USER_ID");
            if (userLikes.containsKey(userId)) {
                List<Integer> filmsId = userLikes.get(userId);
                int filmId = rs.getInt("FILM_ID");
                filmsId.add(filmId);
                userLikes.put(userId, filmsId);
            } else {
                int filmId = rs.getInt("FILM_ID");
                List<Integer> newFilmId = new ArrayList<>();
                newFilmId.add(filmId);
                userLikes.put(userId, newFilmId);
            }
        }
        return userLikes;
    }
}
