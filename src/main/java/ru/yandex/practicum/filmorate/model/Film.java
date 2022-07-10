package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.Month;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;

    public static final LocalDate CINEMA_FOUNDATION_DATE = LocalDate.of(1895, Month.DECEMBER, 28);
}
