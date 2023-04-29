package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserIsNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Qualifier("FriendshipDbStorage")
public class FriendshipDbStorage implements FriendshipStorage {

    private final JdbcTemplate jdbcTemplate;

    private final UserStorage userStorage;

    public FriendshipDbStorage(JdbcTemplate jdbcTemplate, UserStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
    }

    @Override
    public void addFriend(int userId, int friendId) {
        if (userStorage.getUserForId(userId) == null || userStorage.getUserForId(friendId) == null) {
            throw new UserIsNotFoundException("Пользователя такого нету((");
        }
        String sqlQuery = "insert into USER_FRIEND (USER_ID, FRIEND_ID) " +
                "values (?, ?) ";
        jdbcTemplate.update(sqlQuery,userId, friendId);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        String sqlQuery = "delete from USER_FRIEND where USER_ID = ? and FRIEND_ID = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public List<User> getFriendsUserForId(Integer id) {
        String friend = "select USER_ID , EMAIL , LOGIN , BIRTHDAY , USERNAME " +
                "from USERS WHERE USERS.USER_ID  in (SELECT FRIEND_ID FROM USER_FRIEND WHERE USER_ID = ?); ";
        List<User> list = jdbcTemplate.query(friend,this::getFriend, id);

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
