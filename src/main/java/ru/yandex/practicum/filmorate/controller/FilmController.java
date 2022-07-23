package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.error.NotFoundException;
import ru.yandex.practicum.filmorate.error.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService service;

    @Autowired
    public FilmController(FilmService service) {
        this.service = service;
    }

    @PostMapping()
    public Film create(@RequestBody Film film) {
        if (film == null) {
            throw new RuntimeException("Пустой запрос");
        }
        return service.createFilm(film);
    }

    @PutMapping()
    public Film update(@RequestBody Film film) {
        if (film == null) {
            throw new RuntimeException("Пустой запрос");
        }
        return service.updateFilm(film);
    }

    @GetMapping()
    public List<Film> get() {
        return service.getFilms();
    }

    @GetMapping("/{filmId}")
    public Film getFilm(@PathVariable long filmId) {
        return service.getFilm(filmId);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film setLike(@PathVariable long id, @PathVariable long userId) {
        return service.setLike(id,userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable long id, @PathVariable long userId) {
        return service.deleteLike(id,userId);
    }

    @GetMapping("/popular")
    public List<Film> getTopPopular(@RequestParam(value = "count", defaultValue = "10", required = false) String count) {
        return service.getTopPopular(Integer.parseInt(count));
    }
}
