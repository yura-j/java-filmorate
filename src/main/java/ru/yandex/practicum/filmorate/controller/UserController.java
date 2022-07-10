package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.use_case.UserUseCase;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @PostMapping()
    public User create(@RequestBody User user) {
        if (user == null) {
            throw new RuntimeException("Пустой запрос");
        }
        return UserUseCase.createUser(user);
    }

    @PutMapping()
    public User update(@RequestBody User user) {
        if (user == null) {
            throw new RuntimeException("Пустой запрос");
        }
        return UserUseCase.updateUser(user);

    }

    @GetMapping()
    public List<User> get() {
        return UserUseCase.getUsers();
    }
}
