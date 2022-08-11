package ru.yandex.practicum.filmorate.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.error.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;

class ValidatorTest {
    private Film fulfilledFilm;
    private User fulfilledUser;

    private static Validator validator;

    @BeforeAll
    static void init() {
        validator = new Validator() {{
            setIsOneErrorFail(false);
            setThrowException(true);
        }};

    }

    @BeforeEach
    void setTestEntities() {
        /*fulfilledFilm = new Film(1L, "name", "desc", LocalDate.now().minusYears(20), 100);
        fulfilledUser = new User(1L, "email@", "login", "name", LocalDate.now().minusYears(20));*/
    }

    @Test
    void validateAllRulesCorrectInOkOut() {
        validator.validate(UserService.getUserValidationRules(fulfilledUser));
        validator.validate(FilmService.getFilmValidationRules(fulfilledFilm));
    }

    @Test
    void validateNotNullRuleNullInExceptionOut() {
        fulfilledUser.setName(null);
        Assertions.assertThrows(ValidationException.class, () -> {
            validator.validate(UserService.getUserValidationRules(fulfilledUser));
        });
    }

    @Test
    void validateNotBlankAndNotNullRulesNullInExceptionOut() {
        fulfilledFilm.setName(null);
        Assertions.assertThrows(ValidationException.class, () -> {
            validator.validate(FilmService.getFilmValidationRules(fulfilledFilm));
        });
    }

    @Test
    void validateLimitedLettersRule201InExceptionOut() {
        fulfilledFilm.setDescription("1".repeat(201));
        Assertions.assertThrows(ValidationException.class, () -> {
            validator.validate(FilmService.getFilmValidationRules(fulfilledFilm));
        });
    }

    @Test
    void validatePositiveRuleNegativeInExceptionOut() {
        fulfilledFilm.setDuration(-1);
        Assertions.assertThrows(ValidationException.class, () -> {
            validator.validate(FilmService.getFilmValidationRules(fulfilledFilm));
        });
    }

    @Test
    void validateElderThenRuleYoungerInExceptionOut() {
        fulfilledFilm.setReleaseDate(Film.CINEMA_FOUNDATION_DATE.minusDays(1));
        Assertions.assertThrows(ValidationException.class, () -> {
            validator.validate(FilmService.getFilmValidationRules(fulfilledFilm));
        });
    }

    @Test
    void validateYoungerThenRuleElderInExceptionOut() {
        fulfilledUser.setBirthday(LocalDate.now().plusDays(1));
        Assertions.assertThrows(ValidationException.class, () -> {
            validator.validate(UserService.getUserValidationRules(fulfilledUser));
        });
    }

    @Test
    void validateHaveNoSpacesRuleSpaceInExceptionOut() {
        fulfilledUser.setLogin("ds a");
        Assertions.assertThrows(ValidationException.class, () -> {
            validator.validate(UserService.getUserValidationRules(fulfilledUser));
        });
    }

    @Test
    void validateEmailSyntaxNoCommercialAtInExceptionOut() {
        fulfilledUser.setEmail("no_commercial_at");
        Assertions.assertThrows(ValidationException.class, () -> {
            validator.validate(UserService.getUserValidationRules(fulfilledUser));
        });
    }

}