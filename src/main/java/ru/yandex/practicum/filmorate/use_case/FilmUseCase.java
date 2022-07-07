package ru.yandex.practicum.filmorate.use_case;

import lombok.extern.slf4j.Slf4j;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validation.Validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class FilmUseCase {
    private static Map<Integer, Film> Films = new HashMap<>();
    private static Integer maxId = 0;

    private static final Validator validator = new Validator() {{
        setIsOneErrorFail(false);
        setThrowException(true);
    }};

    public static List<Film> getFilms() {
        return new ArrayList<>(FilmUseCase.Films.values());
    }

    public static Film updateFilm(Film film) {
        validateAndLog(film);
        if (!Films.containsKey(film.getId())) {
            throw new RuntimeException("Фильма с указанным идентификатором нет");
        }
        Films.put(film.getId(), film);
        log.info("Обновлен фильм" + film);
        return film;
    }

    public static Film createFilm(Film film) {
        validateAndLog(film);
        film.setId(++FilmUseCase.maxId);
        Films.put(film.getId(), film);

        log.info("Добавлен новый фильм" + film);
        return film;
    }

    public static void validateAndLog(Film film) {
        validator.setThrowException(false);
        if (!validator.validate(film)) {
            log.info("Фильм " + film + "Не прошел валидацию");
        }
        validator.setThrowException(true);
        validator.validate(film);
    }
}
