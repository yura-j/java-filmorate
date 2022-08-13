package ru.yandex.practicum.filmorate.validation;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ValidationChain {
    private final List<ValidationRule> validations = new ArrayList<>();
    private final ValidationValueBox value;

    private ValidationChain(ValidationValueBox value) {
        this.value = value;
    }

    public static ValidationChain of(Object value, String field, String rusField) {
        return new ValidationChain(new ValidationValueBox(value, field, rusField));
    }

    public ValidationChain add(ValidationRule rule) {
        rule.setValueBox(value);
        this.validations.add(rule);
        return this;
    }
}
