package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.filmorate.validation.IValid;
import ru.yandex.practicum.filmorate.validation.ValidationChain;
import ru.yandex.practicum.filmorate.validation.checkers.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class Film implements IValid {
    Integer id;
    String name;
    String description;
    LocalDate releaseDate;
    Integer duration;

    public static final LocalDate CINEMA_FOUNDATION_DATE = LocalDate.of(1895, Month.DECEMBER, 28);

    public Film() {
    }

    @Override
    public List<ValidationChain> rules() {
        return List.of(
                ValidationChain.of(name, "name", "Имя фильма")
                        .add(new NotNull())
                        .add(new NotBlank()),

                ValidationChain.of(description, "description", "Описание")
                        .add(new NotNull())
                        .add(new LimitedLetters(200)),

                ValidationChain.of(duration, "duration", "Продолжительность фильма")
                        .add(new NotNull())
                        .add(new Positive()),

                ValidationChain.of(releaseDate, "releaseDate", "Дата выхода фильма")
                        .add(new NotNull())
                        .add(new ElderThen(CINEMA_FOUNDATION_DATE))
        );
    }
}
