package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.UserIsNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@Slf4j
public class UserService {

    private UserDbStorage userDbStorage;

    private UserStorage inMemoryUserStorage;

    @Autowired
    public UserService(UserStorage inMemoryUserStorage, UserDbStorage userDbStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
        this.userDbStorage = userDbStorage;

    }

    public Optional<User> userAddFriend(int userId, int friendId) { //метод добавления в друзья
        return userDbStorage.addFriend(userId, friendId);
        //throw new UserIsNotFoundException("Пользователь с данным id не найден.");
    }

    public User userDeleteFriend(int userId, int friendId) { //метод удаления из друзей
//        Map<Integer, User> users = inMemoryUserStorage.getMapUsers();
//        if (users.containsKey(userId) && users.containsKey(friendId)) {
//            User user = users.get(userId);
//            if (!user.getFriendVault().contains(friendId)) {
//                return user;
//            }
//            inMemoryUserStorage.deleteFriend(userId, friendId);
//            log.info("Пользователь с id {} удалил из друзей пользователя с id {}. ", userId, friendId);
//            return user;
//        }
//        throw new UserIsNotFoundException("Пользователь с данным id не найден.");
        return userDbStorage.deleteFriend(userId, friendId);
    }

    public List<Optional<User>> getListFriend(int userId, int friendId) { //метод получения списка друзей
//        User user = getUserForId(userId);
//        User friend = getUserForId(friendId);
//
//        List<User> listUser = user.getFriendship()
//           .stream()
//                .filter(friend.getFriendVault()::contains)
//                .map(this::getUserForId)
//                .collect(Collectors.toList());
//
//        return listUser;

        //userDbStorage.getFriendsUserForId(userId, friendId);
        return userDbStorage.getListFriend(userId, friendId);
    }

    public List<Optional<User>> getAllUsers() {
        return userDbStorage.getAllUsers();
    }

    public User createUser(User user) {
        userCheck(user);
        return userDbStorage.createUser(user);
    }

    public User updateUser(User user) {
        userCheck(user);
        return userDbStorage.updateUser(user);
    }

    public User getUserForId(int id) {
        return userDbStorage.getUserForId(id).orElseThrow(() -> new UserIsNotFoundException("При получении id пришел null"));
    }

    public List<Optional<User>> getFriendsUserForId(Integer id) {
        return userDbStorage.getFriendsUserForId(id);
    }

    private void userCheck(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Имя пользователя не было указанно, по этому использован его логин.");
            user.setName(user.getLogin());
        }
    }
}
