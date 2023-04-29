package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.UserIsNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@Slf4j
public class UserService {

    private UserStorage userDbStorage;

    private FriendshipStorage friendshipStorage;

    @Autowired
    public UserService(UserDbStorage userDbStorage, FriendshipStorage friendshipStorage) {
        this.userDbStorage = userDbStorage;
        this.friendshipStorage = friendshipStorage;
    }

    public void userAddFriend(int userId, int friendId) { //метод добавления в друзья
        friendshipStorage.addFriend(userId, friendId);
    }

    public void userDeleteFriend(int userId, int friendId) { //метод удаления из друзей
        friendshipStorage.deleteFriend(userId, friendId);
    }

    public List<User> getListFriend(int userId, int friendId) { //метод получения списка друзей
        return friendshipStorage.getListFriend(userId, friendId);
    }

    public List<User> getAllUsers() {
        return userDbStorage.getAllUsers();
    }

    public User createUser(User user) {
        checkName(user);
        return userDbStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userDbStorage.updateUser(user);
    }

    public User getUserForId(int id) {
        return userDbStorage.getUserForId(id).orElseThrow(() -> new UserIsNotFoundException("При получении id пришел null"));
    }

    public List<User> getFriendsUserForId(Integer id) {
        return friendshipStorage.getFriendsUserForId(id);
    }

    private void checkName(User user) {
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

}
