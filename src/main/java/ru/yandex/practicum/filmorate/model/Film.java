package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class Film {
    public static final LocalDate CINEMA_FOUNDATION_DATE = LocalDate.of(1895, Month.DECEMBER, 28);
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private Mpa mpa;
    private Set<Genre> genres = new HashSet<>();
    private Set<UserLikeFilm> likes = new HashSet<>();
    //relations
    private Long mpaId;

    public Film(Long id, String name, String description, LocalDate releaseDate, Integer duration, Long mpaId, Set<Genre> genres, Set<UserLikeFilm> likes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpaId = mpaId;
        this.genres = genres;
        this.likes = likes;
    }

    public Film(Long id, String name, String description, LocalDate releaseDate, Integer duration, Long mpaId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpaId = mpaId;
    }
}
