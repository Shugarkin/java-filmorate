package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ErrorResponse;
import ru.yandex.practicum.filmorate.exception.IncorrectIDException;
import ru.yandex.practicum.filmorate.exception.UserIsNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/users")
public class UserController {

    private InMemoryUserStorage inMemoryUserStorage;
    private UserService userService;

    @Autowired
    public UserController(InMemoryUserStorage inMemoryUserStorage, UserService userService) {
        this.inMemoryUserStorage = inMemoryUserStorage;
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() { //геттер для пользователей
        return inMemoryUserStorage.getAllUsers();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) { //создание пользователя
        return inMemoryUserStorage.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) { //обновление пользователя
        return inMemoryUserStorage.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}") //добавление в друзья
    @ResponseBody
    public User userAddFriend(@PathVariable("id") final Integer userId, @PathVariable("friendId") final Integer friendId) {
        checkIdUserAndFriend(userId, friendId);
        return userService.userAddFriend(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}") //удаление из друзей
    @ResponseBody
    public User userDeleteFriend(@PathVariable("id") final Integer userId, @PathVariable("friendId") final Integer friendId) {
        checkIdUserAndFriend(userId, friendId);
        return userService.userDeleteFriend(userId, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")//геттер для списка общих друзей
    @ResponseBody
    public List<User> getListFriend(@PathVariable("id") final Integer userId, @PathVariable("otherId") final Integer friendId) {
        checkIdUserAndFriend(userId, friendId);
        return userService.getListFriend(userId, friendId);
    }

    @GetMapping("/{id}") //геттер по айди
    @ResponseBody
    public User getUserForId(@PathVariable final Integer id) {
        checkUserId(id);
        return inMemoryUserStorage.getUserForId(id);
    }

    @GetMapping("/{id}/friends")//получение списка друзей пользователя
    @ResponseBody
    public List<User> getFriendsUserForId(@PathVariable("id") Integer id) {
        checkUserId(id);
        return inMemoryUserStorage.getFriendsUserForId(id);
    }

    private void checkUserId(Integer id) {
        if (id == null) {
            throw new IncorrectIDException("Параметр id пользователя равен нулю.");
        }
        if (id <= 0) {
            throw new IncorrectIDException("Параметр id пользователя имеет отрицательное значение.");
        }
    }

    private void checkIdUserAndFriend(Integer idUser, Integer idFriend) {
        if (idUser == null) {
            throw new IncorrectIDException("Параметр id пользователя равен нулю.");
        }
        if (idFriend == null) {
            throw new IncorrectIDException("Параметр id друга равен нулю.");
        }
        if (idUser <= 0) {
            throw new IncorrectIDException("Параметр id пользователя имеет отрицательное значение.");
        }
        if (idFriend <= 0) {
            throw new IncorrectIDException("Параметр id друга имеет отрицательное значение.");
        }
    }

    @ExceptionHandler({UserIsNotFoundException.class, IncorrectIDException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleIncorrect(final RuntimeException e) {
        return new ErrorResponse("Ошибка пользователя", e.getMessage());
    }

}