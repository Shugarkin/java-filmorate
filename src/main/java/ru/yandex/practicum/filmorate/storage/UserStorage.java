package ru.yandex.practicum.filmorate.storage;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.List;

public interface UserStorage {

    public List<User> getAllUsers();

    public User createUser(@Valid @RequestBody User user);

    public User updateUser(@Valid @RequestBody User user);

}
