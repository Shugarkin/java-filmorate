package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IncorrectIDException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;

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
    public User userAddFriend(@PathVariable("id") final Integer userId, @PathVariable("friendId") final Integer friendId) {
        checkIdUserAndFriend(userId, friendId);
        return userService.userAddFriend(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}") //удаление из друзей
    public User userDeleteFriend(@PathVariable("id") final Integer userId, @PathVariable("friendId") final Integer friendId) {
        checkIdUserAndFriend(userId, friendId);
        return userService.userDeleteFriend(userId, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")//геттер для списка общих друзей
    public List<User> getListFriend(@PathVariable("id") final Integer userId, @PathVariable("otherId") final Integer friendId) {
        checkIdUserAndFriend(userId, friendId);
        return userService.getListFriend(userId, friendId);
    }

    @GetMapping("/{id}") //геттер по айди
    public User getUserForId(@PathVariable int id) {
        Optional<Integer> newId = Optional.of(id);
        checkUserId(newId.get());
        return userService.getUserForId(newId);
    }

    @GetMapping("/{id}/friends")//получение списка друзей пользователя
    public List<User> getFriendsUserForId(@PathVariable("id") Integer id) {
        checkUserId(id);
        return userService.getFriendsUserForId(id);
    }

    private void checkUserId(Integer id) {
        if (id <= 0) {
            throw new IncorrectIDException("Параметр id пользователя имеет отрицательное значение.");
        }
    }

    private void checkIdUserAndFriend(Integer idUser, Integer idFriend) {
        if (idUser <= 0) {
            throw new IncorrectIDException("Параметр id пользователя имеет отрицательное значение.");
        }
        if (idFriend <= 0) {
            throw new IncorrectIDException("Параметр id друга имеет отрицательное значение.");
        }
    }
}