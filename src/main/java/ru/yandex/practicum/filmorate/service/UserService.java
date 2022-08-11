package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.error.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserFriendship;
import ru.yandex.practicum.filmorate.storage.UserFriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validation.ValidationChain;
import ru.yandex.practicum.filmorate.validation.Validator;
import ru.yandex.practicum.filmorate.validation.checkers.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage storage;
    private final UserFriendshipStorage friendshipStorage;

    private final Validator validator = new Validator() {{
        setIsOneErrorFail(false);
        setThrowException(true);
    }};

    @Autowired
    public UserService(
            @Qualifier("UserDbStorage") UserStorage storage,
            @Qualifier("UserFriendshipDbStorage") UserFriendshipStorage friendshipStorage) {
        this.storage = storage;
        this.friendshipStorage = friendshipStorage;
    }

    public List<User> getUsers() {
        return storage.getUsers();
    }

    public User updateUser(User user) {
        validateAndLog(user);
        User savedUser = storage.updateUser(user);
        log.info("Обновлен пользователь" + savedUser);
        return savedUser;
    }

    public User createUser(User user) {
        validateAndLog(user);
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        User savedUser = storage.createUser(user);
        log.info("Добавлен новый пользователь" + savedUser);
        return savedUser;
    }

    public User addToFriendList(long id, long friendId) {
        User user = getUserById(id);
        User friend = getUserById(friendId);

        if (user == null
                || friend == null) {
            throw new NotFoundException("не найдено");
        }

        UserFriendship friendship = friendshipStorage.getByUserIdAndFriendId(id, friendId);
        if (friendship == null) {
            friendshipStorage.create(new UserFriendship(null, user, friend));
        }
        return getUserById(user.getId());
    }

    public User deleteFromFriendList(long id, long friendId) {
        User user = getUserById(id);

        User friend = getUserById(friendId);

        if (user == null || friend == null) {
            return user;
        }

        UserFriendship friendship = friendshipStorage.getByUserIdAndFriendId(friendId, id);
        if (friendship != null) {
           friendshipStorage.delete(friendship.getId());
        }
        return getUserById(id);
    }

    public List<User> getFriends(long id) {
        User user = getUserById(id);

        if (user == null) {
            return null;
        }
        return new ArrayList<>(user.getFriends());
    }

    public List<User> getCommonFriends(long id, long otherId) {

        return friendshipStorage
                .getCommonFriends(id, otherId);
    }

    public User getUser(long id) {
        if (getUserById(id) == null) {
            throw new NotFoundException("пользователь не найден");
        }
        return getUserById(id);
    }

    public static List<ValidationChain> getUserValidationRules(User user) {
        return List.of(
                ValidationChain.of(user.getName(), "name", "Имя пользователя")
                        .add(new NotNull()),

                ValidationChain.of(user.getEmail(), "email", "Емайл")
                        .add(new NotNull())
                        .add(new NotBlank())
                        .add(new EmailSyntax()),

                ValidationChain.of(user.getLogin(), "login", "Логин")
                        .add(new NotNull())
                        .add(new NotBlank())
                        .add(new HaveNoSpaces()),

                ValidationChain.of(user.getBirthday(), "birthday", "Дата рождения")
                        .add(new NotNull())
                        .add(new YoungerThen(LocalDate.now()))
        );
    }

    private void validateAndLog(User user) {
        validator.setThrowException(false);
        if (!validator.validate(getUserValidationRules(user))) {
            log.info("Пользователь " + user + "Не прошел валидацию");
        }
        validator.setThrowException(true);
        validator.validate(getUserValidationRules(user));
    }

    private User getUserById(long id) {
        return storage
                .getById(id);
    }
}
