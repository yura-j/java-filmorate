package ru.yandex.practicum.filmorate.use_case;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.Validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class UserUseCase {
    private static Map<Integer, User> Users = new HashMap<>();
    private static Integer maxId = 0;

    private static final Validator validator = new Validator() {{
        setIsOneErrorFail(false);
        setThrowException(true);
    }};

    public static List<User> getUsers() {
        return new ArrayList<>(UserUseCase.Users.values());
    }

    public static User updateUser(User user) {
        UserUseCase.validateAndLog(user);

        if (!Users.containsKey(user.getId())) {
            throw new RuntimeException("Пользователя с указанным идентификатором нет");
        }
        Users.put(user.getId(), user);
        log.info("Обновлен пользователь" + user);
        return user;
    }

    public static User createUser(User user) {
        UserUseCase.validateAndLog(user);
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(++UserUseCase.maxId);
        Users.put(user.getId(), user);
        log.info("Добавлен новый пользователь" + user);
        return user;
    }

    private static void validateAndLog(User user) {
        validator.setThrowException(false);
        if (!validator.validate(user)) {
            log.info("Пользователь " + user + "Не прошел валидацию");
        }
        validator.setThrowException(true);
        validator.validate(user);
    }

}
