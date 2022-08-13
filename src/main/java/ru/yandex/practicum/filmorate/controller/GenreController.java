package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/genres")
public class GenreController {

    private final FilmService service;

    @Autowired
    public GenreController(FilmService service) {
        this.service = service;
    }

    @GetMapping("/{genreId}")
    public Genre getById(@PathVariable long genreId) {
        return service.getGenreById(genreId);
    }

    @GetMapping()
    public List<Genre> get() {
        return service.getGenre();
    }
}
