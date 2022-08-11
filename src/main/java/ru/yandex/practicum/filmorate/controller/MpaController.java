package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
public class MpaController {

    private final FilmService service;

    @Autowired
    public MpaController(FilmService service) {
        this.service = service;
    }

    @GetMapping("/{mpaId}")
    public Mpa getById(@PathVariable long mpaId) {
        return service.getMpaById(mpaId);
    }

    @GetMapping()
    public List<Mpa> get() {
        return service.getMpa();
    }
}
