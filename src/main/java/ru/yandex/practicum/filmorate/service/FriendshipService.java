package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
public class FriendshipService {

    private FriendshipStorage friendshipStorage;

    @Autowired
    public FriendshipService(FriendshipStorage friendshipStorage) {
        this.friendshipStorage = friendshipStorage;
    }

    public void addFriend(int userId, int friendId) {
        friendshipStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(int userId, int friendId) {
        friendshipStorage.deleteFriend(userId, friendId);
    }

    public List<User> getFriendsUserForId(Integer id) {
        return friendshipStorage.getFriendsUserForId(id);
    }

    public List<User> getListFriend(int userId, int friendId) {
        return friendshipStorage.getListFriend(userId, friendId);
    }
}
