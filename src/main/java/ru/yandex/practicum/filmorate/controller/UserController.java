package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
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

    @PutMapping("/{id}/friends/{friendId}")
    public void userAddFriend(@PathVariable("id") final Integer userId, @PathVariable("friendId") final Integer friendId) {
        userService.userAddFriend(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void userDeleteFriend(@PathVariable("id") final Integer userId, @PathVariable("friendId") final Integer friendId) {
        userService.userDeleteFriend(userId, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getListFriend(@PathVariable("id") final Integer userId, @PathVariable("otherId") final Integer friendId) {
        return userService.getListFriend(userId, friendId);
    }

    @GetMapping("/{id}")
    public User getUserForId(@PathVariable int id) {
        return userService.getUserForId(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendsUserForId(@PathVariable("id") Integer id) {
        return userService.getFriendsUserForId(id);
    }

    @DeleteMapping("/{id}")
    public void userDeleteById(@PathVariable("id") final Integer userId) {
        userService.userDeleteById(userId);
    }


    @GetMapping("/{id}/feed")
    public List<Feed> getFeed(@PathVariable("id") Integer id) {
        return userService.getFeed(id);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendation(@PathVariable("id") Integer id) {
        return userService.getRecommendation(id);

    }
}