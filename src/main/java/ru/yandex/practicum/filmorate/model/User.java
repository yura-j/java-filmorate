package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class User {
    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
}
