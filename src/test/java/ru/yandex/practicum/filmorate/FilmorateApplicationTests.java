package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.error.NotFoundException;
import ru.yandex.practicum.filmorate.storage.db.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmDbStorage;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final UserFriendshipDbStorage userFriendshipDbStorage;
    private final UserLikeFilmDbStorage userLikeFilm;

    @Test
    public void testFindUserById() {
        Assertions.assertThrows(NotFoundException.class, () -> userStorage.getById(1L));
    }

    @Test
    public void testFindFilmById() {
        Assertions.assertThrows(NotFoundException.class, () -> filmDbStorage.getById(1L));
    }

    @Test
    public void testFindMpaById() {
        Assertions.assertTrue(mpaDbStorage.getById(1L).isPresent());
    }

    @Test
    public void testFindGenreById() {
        Assertions.assertTrue(genreDbStorage.getById(1L).isPresent());
    }

    @Test
    public void testFindUserFriendshipById() {
        Assertions.assertEquals(0, userFriendshipDbStorage.get().size());
    }

    @Test
    public void testFindUserLikeById() {
        Assertions.assertEquals(0, userLikeFilm.get().size());
    }

}
