package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserLikeFilm {
    private Long id;
    private Film film;
    private User user;

    public UserLikeFilm(Long id, Film film, User user) {
        this.id = id;
        this.film = film;
        this.user = user;
    }
}
