package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;

@Service
public class UserService {

    public User userAddFriend (User user, long id1, long id2) {

        user.getFriendVault().add(id2);
        return user;
    }
}
