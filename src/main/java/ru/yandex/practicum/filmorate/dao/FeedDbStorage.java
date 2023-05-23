package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

@Component
@Qualifier("FeedDbStorage")
public class FeedDbStorage implements FeedStorage {

    private final JdbcTemplate jdbcTemplate;

    public FeedDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public void addFeed(Integer userId, Integer entityId, EventType eventType, Operation operation) {
        long date = Instant.now().toEpochMilli();
        String sqlQuery = "insert into FEED (USER_ID, ENTITY_ID, EVENT_TYPE, OPERATION, TIMESTAMP_FEED) " +
                " values(?, ?, ?, ?, ?); ";

        jdbcTemplate.update(sqlQuery, userId, entityId, String.valueOf(eventType), String.valueOf(operation), date);

    }

    @Override
    public List<Feed> getFeed(Integer id) {
        String sqlFeed = "select * from FEED where USER_ID = ? ;";

        return jdbcTemplate.query(sqlFeed, this::findFeed, id);
    }

    private Feed findFeed(ResultSet resultSet, int rowNum) throws SQLException {

        return Feed.builder()
                .eventId(resultSet.getInt("EVENT_ID"))
                .userId(resultSet.getInt("USER_ID"))
                .entityId(resultSet.getInt("ENTITY_ID"))
                .timestamp(resultSet.getLong("TIMESTAMP_FEED"))
                .eventType(resultSet.getString("EVENT_TYPE"))
                .operation(resultSet.getString("OPERATION"))
                .build();
    }
}
