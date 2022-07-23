package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping()
    public User create(@RequestBody User user) {
        if (user == null) {
            throw new RuntimeException("Пустой запрос");
        }
        return service.createUser(user);
    }

    @PutMapping()
    public User update(@RequestBody User user) {
        if (user == null) {
            throw new RuntimeException("Пустой запрос");
        }
        return service.updateUser(user);

    }

    @GetMapping()
    public List<User> get() {
        return service.getUsers();
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable long userId) {
        return service.getUser(userId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addToFriendList(@PathVariable long id, @PathVariable long friendId) {
        return service.addToFriendList(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFormFriendList(@PathVariable long id, @PathVariable long friendId) {
        return service.deleteFromFriendList(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable long id) {
        return service.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        return service.getCommonFriends(id, otherId);
    }
}
