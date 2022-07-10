package ru.yandex.practicum.filmorate.use_case;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.ValidationChain;
import ru.yandex.practicum.filmorate.validation.Validator;
import ru.yandex.practicum.filmorate.validation.checkers.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class UserUseCase {
    private static Map<Long, User> Users = new HashMap<>();
    private static Long maxId = 0L;

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
        if (!validator.validate(getUserValidationRules(user))) {
            log.info("Пользователь " + user + "Не прошел валидацию");
        }
        validator.setThrowException(true);
        validator.validate(getUserValidationRules(user));
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

}
