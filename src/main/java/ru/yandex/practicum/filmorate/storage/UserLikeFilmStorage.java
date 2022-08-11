package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.UserLikeFilm;

import java.util.List;

public interface UserLikeFilmStorage {
    List<UserLikeFilm> get();

    UserLikeFilm create(UserLikeFilm like);

    UserLikeFilm getByFilmIdAndUserId(Long filmId, Long userId);

    Long delete(Long Id);

    List<UserLikeFilm> getByFilm(Film film);
}
