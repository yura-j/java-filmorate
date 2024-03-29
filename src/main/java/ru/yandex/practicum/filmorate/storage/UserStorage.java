package ru.yandex.practicum.filmorate.storage;


import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getUsers();

    User updateUser(User user);

    User createUser(User user);

    User getById(Long id);

    void initRelationsGreedy(User user);
}
