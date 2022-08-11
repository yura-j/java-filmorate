package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserFriendship;

import java.util.List;

public interface UserFriendshipStorage {

    UserFriendship create(UserFriendship friendship);

    UserFriendship getByUserIdAndFriendId(Long friendId, Long userId);

    Long delete(Long Id);

    List<User> getCommonFriends(Long userId, Long anotherUserId);

    List<UserFriendship> get();
}
