package ru.yandex.practicum.filmorate.validation.checkers;

import ru.yandex.practicum.filmorate.validation.ValidationRule;

public class LimitedLetters extends ValidationRule {

    private int limit;

    public LimitedLetters(int limit) {
        this.limit = limit;
    }

    @Override
    protected boolean checkValue() {
        if (!(valueBox.getValue() instanceof String)) {
            return false;
        }
        String testedValue = (String) valueBox.getValue();
        message = String.format("Поле %s должно содержать не более %d символов (количество символов %d)",
                valueBox.getRusFieldName(), limit, testedValue.length());

        return testedValue.length() <= limit;
    }
}
