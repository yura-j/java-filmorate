package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.error.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validation.ValidationChain;
import ru.yandex.practicum.filmorate.validation.Validator;
import ru.yandex.practicum.filmorate.validation.checkers.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage storage;
    private final UserStorage userStorage;

    private final Validator validator = new Validator() {{
        setIsOneErrorFail(false);
        setThrowException(true);
    }};

    @Autowired
    public FilmService(FilmStorage storage, UserStorage userStorage) {
        this.storage = storage;
        this.userStorage = userStorage;
    }

    public List<Film> getFilms() {
        return storage.getFilms();
    }

    public Film updateFilm(Film film) {
        validateAndLog(film);
        Film savedFilm = storage.updateFilm(film);
        log.info("Обновлен фильм" + savedFilm);
        return savedFilm;
    }

    public Film createFilm(Film film) {
        validateAndLog(film);
        Film savedFilm = storage.createFilm(film);
        log.info("Добавлен новый фильм" + savedFilm);
        return savedFilm;
    }

    public Film setLike(long id, long userId) {
        Film film = getFilmById(id);
        if (film == null) {
            return film;
        }
        film.getLikedUsers().add(id);
        return film;
    }

    public Film deleteLike(long id, long userId) {
        Film film = getFilmById(id);
        if (film == null) {
            return film;
        }
        User user =  userStorage
                .getUsers()
                .stream()
                .filter(u -> u.getId() == userId)
                .findFirst()
                .orElse(null);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        film.getLikedUsers().remove(user.getId());
        return film;
    }

    public List<Film> getTopPopular(int count) {
        List<Film> sortedList = storage.getFilms();
        sortedList.sort((film1, film2) -> {
            Integer value1 = film1.getLikedUsers().toArray().length;
            Integer value2 = film2.getLikedUsers().toArray().length;
            return value2.compareTo(value1);
        });
        return sortedList
                .stream()
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film getFilm(long id) {
        if (getFilmById(id) == null) {
            throw new NotFoundException("фильм не найден");
        }
        return getFilmById(id);
    }

    public static List<ValidationChain> getFilmValidationRules(Film film) {
        return List.of(
                ValidationChain.of(film.getName(), "name", "Имя фильма")
                        .add(new NotNull())
                        .add(new NotBlank()),

                ValidationChain.of(film.getDescription(), "description", "Описание")
                        .add(new NotNull())
                        .add(new LimitedLetters(200)),

                ValidationChain.of(film.getDuration(), "duration", "Продолжительность фильма")
                        .add(new NotNull())
                        .add(new Positive()),

                ValidationChain.of(film.getReleaseDate(), "releaseDate", "Дата выхода фильма")
                        .add(new NotNull())
                        .add(new ElderThen(Film.CINEMA_FOUNDATION_DATE))
        );
    }

    private void validateAndLog(Film film) {
        validator.setThrowException(false);
        if (!validator.validate(getFilmValidationRules(film))) {
            log.info("Фильм " + film + "Не прошел валидацию");
        }
        validator.setThrowException(true);
        validator.validate(getFilmValidationRules(film));
    }

    private Film getFilmById(long id) {
        return storage
                .getFilms()
                .stream()
                .filter(f -> f.getId() == id)
                .findFirst()
                .orElse(null);
    }
}
