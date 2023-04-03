package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserIsNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private UserStorage inMemoryUserStorage;

    @Autowired
    public UserService(UserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public User userAddFriend(int userId, int friendId) { //метод добавления в друзья
        Map<Integer, User> users = inMemoryUserStorage.getMapUsers();
        if (users.containsKey(userId) && users.containsKey(friendId)) {
            inMemoryUserStorage.addFriend(userId, friendId);
            User user = users.get(userId);
            log.info("Пользователь с id {} добавил пользователя с id {} в друзья. ", userId, friendId);
            return user;
        }
        throw new UserIsNotFoundException("Пользователь с данным id не найден.");
    }

    public User userDeleteFriend(int userId, int friendId) { //метод удаления из друзей
        Map<Integer, User> users = inMemoryUserStorage.getMapUsers();
        if (users.containsKey(userId) && users.containsKey(friendId)) {
            User user = users.get(userId);
            if (!user.getFriendVault().contains(friendId)) {
                return user;
            }
            inMemoryUserStorage.deleteFriend(userId, friendId);
            log.info("Пользователь с id {} удалил из друзей пользователя с id {}. ", userId, friendId);
            return user;
        }
        throw new UserIsNotFoundException("Пользователь с данным id не найден.");
    }

    public List<User> getListFriend(int userId, int friendId) { //метод получения списка друзей
        User user = getUserForId(userId);
        User friend = getUserForId(friendId);

        List<User> listUser = user.getFriendVault().stream()
                .filter(friend.getFriendVault()::contains)
                .map(this::getUserForId).collect(Collectors.toList());

        return listUser;
    }

    public List<User> getAllUsers() {
        return inMemoryUserStorage.getAllUsers();
    }

    public User createUser(User user) {
        userCheck(user);
        return inMemoryUserStorage.createUser(user);
    }

    public User updateUser(User user) {
        userCheck(user);
        return inMemoryUserStorage.updateUser(user);
    }

    public User getUserForId(int id) {
        return inMemoryUserStorage.getUserForId(id).orElseThrow(() -> new ValidationException("При получении id пришел null"));
    }

    public List<User> getFriendsUserForId(Integer id) {
        return inMemoryUserStorage.getFriendsUserForId(id);
    }

    private void userCheck(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Имя пользователя не было указанно, по этому использован его логин.");
            user.setName(user.getLogin());
        }
    }
}
