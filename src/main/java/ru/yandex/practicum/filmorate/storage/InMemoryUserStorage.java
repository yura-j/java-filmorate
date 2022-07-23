package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.error.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long maxId = 0L;

    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Пользователя с указанным идентификатором нет");
        }
        users.put(user.getId(), user);
        return user;
    }

    public User createUser(User user) {
        user.setId(++maxId);
        users.put(user.getId(), user);
        return user;
    }

}
