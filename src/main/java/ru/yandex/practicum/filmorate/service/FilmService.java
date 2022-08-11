package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.error.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.*;
import ru.yandex.practicum.filmorate.validation.ValidationChain;
import ru.yandex.practicum.filmorate.validation.Validator;
import ru.yandex.practicum.filmorate.validation.checkers.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage storage;

    private final UserLikeFilmStorage likeStorage;

    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    private final Validator validator = new Validator() {{
        setIsOneErrorFail(false);
        setThrowException(true);
    }};

    public FilmService(@Qualifier("FilmDbStorage") FilmStorage storage,
                       @Qualifier("UserLikeFilmDbStorage") UserLikeFilmStorage likeStorage,
                       @Qualifier("UserDbStorage") UserStorage userStorage,
                       @Qualifier("GenreDbStorage") GenreStorage genreStorage,
                       @Qualifier("MpaDbStorage") MpaStorage mpaStorage) {
        this.storage = storage;
        this.likeStorage = likeStorage;
        this.userStorage = userStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
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
        UserLikeFilm like = this.likeStorage.getByFilmIdAndUserId(id, userId);
        Film film = getFilmById(id);

        User user = this.userStorage.getById(userId);
        if (user == null
                || film == null) {
            return null;
        }

        if (like == null) {
            this.likeStorage.create(new UserLikeFilm(null, film, user));
        }

        return film;
    }

    public Film deleteLike(long id, long userId) {
        Film film = getFilmById(id);
        User user = this.userStorage.getById(userId);
        if (film == null
                || user == null) {
            return film;
        }
        UserLikeFilm like = this.likeStorage.getByFilmIdAndUserId(id, userId);
        if (like == null) {
            throw new NotFoundException("Лайк не найден");
        }
        this.likeStorage.delete(like.getId());
        return getFilmById(id);
    }

    public List<Film> getTopPopular(int count) {
        List<Film> sortedList = storage.getFilms();
        System.out.println("getFilms() = " + getFilms());
        sortedList.sort((film1, film2) -> {
            Integer value1 = film1.getLikes().toArray().length;
            Integer value2 = film2.getLikes().toArray().length;
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
                        .add(new ElderThen(Film.CINEMA_FOUNDATION_DATE)),

                ValidationChain.of(film.getMpa(), "mpa", "Рейтинг")
                        .add(new NotNull())
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
                .getById(id);
    }

    public Genre getGenreById(long genreId) {
        return genreStorage
                .getById(genreId).orElseThrow(() -> new NotFoundException("не найдено"));
    }

    public List<Genre> getGenre() {
        return genreStorage
                .get();
    }

    public Mpa getMpaById(long MpaId) {
        return mpaStorage
                .getById(MpaId).orElseThrow(() -> new NotFoundException("не найдено"));
    }

    public List<Mpa> getMpa() {
        return mpaStorage
                .get();
    }
}
