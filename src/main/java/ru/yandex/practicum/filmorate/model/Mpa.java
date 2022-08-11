package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Mpa {
    private Long id;
    private String description;
    private String name;

    public Mpa(Long id, String description, String name) {
        this.id = id;
        this.description = description;
        this.name = name;
    }
}
