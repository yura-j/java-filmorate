package ru.yandex.practicum.filmorate.storage.in_memory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.error.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private Long maxId = 0L;

    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Фильма с указанным идентификатором нет");
        }
        films.put(film.getId(), film);
        return film;
    }

    public Film createFilm(Film film) {
        film.setId(++maxId);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film getById(Long id) {
        return films.get(id);
    }
}
