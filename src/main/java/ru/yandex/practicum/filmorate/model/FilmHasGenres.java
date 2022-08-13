package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FilmHasGenres {
    private Long id;
    private Long filmId;
    private Long genreId;
    private Genre genre;

    public FilmHasGenres(Long id, Long filmId, Long genreId) {
        this.id = id;
        this.filmId = filmId;
        this.genreId = genreId;
    }

    public FilmHasGenres(Long id, Long filmId, Long genreId, Genre genre) {
        this.id = id;
        this.filmId = filmId;
        this.genreId = genreId;
        this.genre = genre;
    }
}
