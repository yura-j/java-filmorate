package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    @Test
    void testEmptyQuery() {
        FilmController controller = new FilmController();
        Assertions.assertThrows(RuntimeException.class, ()-> {
            controller.create(null);
        });
    }
}