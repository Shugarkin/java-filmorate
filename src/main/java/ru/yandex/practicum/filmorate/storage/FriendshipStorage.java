package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendshipStorage {

    public void addFriend(int userId, int friendId);

    public void deleteFriend(int userId, int friendId);

    public List<User> getFriendsUserForId(Integer id);

    public List<User> getListFriend(int userId, int friendId);

}
