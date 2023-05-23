package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() { //геттер для пользователей
        return userService.getAllUsers();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) { //создание пользователя
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) { //обновление пользователя
        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}") //добавление в друзья
    public void userAddFriend(@PathVariable("id") final Integer userId, @PathVariable("friendId") final Integer friendId) {
        userService.userAddFriend(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}") //удаление из друзей
    public void userDeleteFriend(@PathVariable("id") final Integer userId, @PathVariable("friendId") final Integer friendId) {
        userService.userDeleteFriend(userId, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")//геттер для списка общих друзей
    public List<User> getListFriend(@PathVariable("id") final Integer userId, @PathVariable("otherId") final Integer friendId) {
        return userService.getListFriend(userId, friendId);
    }

    @GetMapping("/{id}") //геттер по айди
    public User getUserForId(@PathVariable int id) {
        return userService.getUserForId(id);
    }

    @GetMapping("/{id}/friends")//получение списка друзей пользователя
    public List<User> getFriendsUserForId(@PathVariable("id") Integer id) {
        return userService.getFriendsUserForId(id);
    }

    @GetMapping("/{id}/feed")
    public List<Feed> getFeed(@PathVariable("id") Integer id) {
        return userService.getFeed(id);
    }
}