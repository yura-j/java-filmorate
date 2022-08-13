package ru.yandex.practicum.filmorate.validation.checkers;

import ru.yandex.practicum.filmorate.validation.ValidationRule;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ElderThen extends ValidationRule {

    private final LocalDate comparedDate;

    public ElderThen(LocalDate comparedDate) {
        this.comparedDate = comparedDate;
    }

    @Override
    protected boolean checkValue() {
        if (!(valueBox.getValue() instanceof LocalDate)) {
            return false;
        }
        LocalDate testedValue = (LocalDate) valueBox.getValue();
        message = String.format("Поле %s должно быть позднее чем  %s (передано %s)",
                valueBox.getRusFieldName(), comparedDate.format(DateTimeFormatter.ofPattern("d.MM.uuuu")),
                testedValue.format(DateTimeFormatter.ofPattern("d.MM.uuuu")));

        return testedValue.isAfter(comparedDate);
    }
}
