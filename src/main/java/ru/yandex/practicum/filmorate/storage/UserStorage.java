package ru.yandex.practicum.filmorate.storage;
import ru.yandex.practicum.filmorate.model.User;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserStorage {

    public List<Optional<User>> getAllUsers();

    public User createUser(User user);

    public User updateUser(User user);

    public Optional<User> addFriend(int userId, int friendId);

    List<Optional<User>> getListFriend(int userId, int friendId);

    public Map<Integer, User> getMapUsers();

    public User deleteFriend(int userId, int friendId);

    public Optional<User> getUserForId(int id);

    public List<Optional<User>> getFriendsUserForId(Integer id);

}
