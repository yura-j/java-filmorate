package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.filmorate.validation.IValid;
import ru.yandex.practicum.filmorate.validation.ValidationChain;
import ru.yandex.practicum.filmorate.validation.checkers.*;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class User implements IValid {
    Integer id;
    String email;
    String login;
    String name;
    LocalDate birthday;

    public User() {
    }

    @Override
    public List<ValidationChain> rules() {
        return List.of(
                ValidationChain.of(name, "name", "Имя пользователя")
                        .add(new NotNull()),

                ValidationChain.of(email, "email", "Емайл")
                        .add(new NotNull())
                        .add(new NotBlank())
                        .add(new EmailSyntax()),

                ValidationChain.of(login, "login", "Логин")
                        .add(new NotNull())
                        .add(new NotBlank())
                        .add(new HaveNoSpaces()),

                ValidationChain.of(birthday, "birthday", "Дата рождения")
                        .add(new NotNull())
                        .add(new YoungerThen(LocalDate.now()))
        );
    }
}
