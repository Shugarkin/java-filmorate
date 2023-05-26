package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class FriendshipDbStorage implements FriendshipStorage {

    private final JdbcTemplate jdbcTemplate;

    public FriendshipDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriend(int userId, int friendId) {
        String sqlQuery = "insert into USER_FRIEND (USER_ID, FRIEND_ID) " +
                "values (?, ?) ";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        String sqlQuery = "delete from USER_FRIEND where USER_ID = ? and FRIEND_ID = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public List<User> getFriendsUserForId(Integer id) {
        String friend = "select * from USERS, USER_FRIEND where USERS.USER_ID = USER_FRIEND.FRIEND_ID AND USER_FRIEND.USER_ID = ? ";
        List<User> list = jdbcTemplate.query(friend, this::getFriend, id);

        return list;
    }


    @Override
    public List<User> getListFriend(int userId, int friendId) {
        String ahaha = " SELECT *" +
                " FROM users " +
                " WHERE USER_ID in (SELECT FRIEND_ID " +
                " FROM USER_FRIEND " +
                " WHERE friend_id in (SELECT friend_id " +
                " FROM USER_FRIEND " +
                " WHERE USER_ID = ?) " +
                " AND USER_ID = ?); ";

        List<User> list = jdbcTemplate.query(ahaha, this::getFriend, friendId, userId);
        return list;
    }

    private User getFriend(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("USER_ID"))
                .email(resultSet.getString("EMAIL"))
                .login(resultSet.getString("LOGIN"))
                .birthday(resultSet.getDate("BIRTHDAY").toLocalDate())
                .name(resultSet.getString("USERNAME"))
                .build();
    }
}
