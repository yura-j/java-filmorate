package ru.yandex.practicum.filmorate.validation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ValidationRule {
    protected ValidationValueBox valueBox;
    private boolean isSetThrowingException;
    protected String message = "Данные не корректны";

    protected abstract boolean checkValue();

    public boolean validate() {
        boolean check = checkValue();
        if (isSetThrowingException && !check) {
            throw new ValidationException(message);
        }
        return check;
    }
}

