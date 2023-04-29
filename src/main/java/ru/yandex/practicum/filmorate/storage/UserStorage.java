package ru.yandex.practicum.filmorate.storage;
import ru.yandex.practicum.filmorate.model.User;
import java.util.List;
import java.util.Optional;

public interface UserStorage {

    List<Optional<User>> getAllUsers();

    Optional<User> createUser(User user);

    Optional<User> updateUser(User user);

    void addFriend(int userId, int friendId);

    List<Optional<User>> getListFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);

    Optional<User> getUserForId(int id);

    List<Optional<User>> getFriendsUserForId(Integer id);

}
