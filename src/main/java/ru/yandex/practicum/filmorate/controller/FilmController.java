package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.use_case.FilmUseCase;
import ru.yandex.practicum.filmorate.validation.ValidationException;

import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {

    @PostMapping("")
    public Film create(@RequestBody Film film) {
        if (film == null) {
            throw new RuntimeException("Пустой запрос");
        }
        return FilmUseCase.createFilm(film);
    }

    @PutMapping("")
    public Film update(@RequestBody Film film) {
        if (film == null) {
            throw new RuntimeException("Пустой запрос");
        }
        return FilmUseCase.updateFilm(film);
    }

    @GetMapping("")
    public List<Film> get() {
        return FilmUseCase.getFilms();
    }
}
