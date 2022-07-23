package ru.yandex.practicum.filmorate.validation;

import ru.yandex.practicum.filmorate.error.ValidationException;

import java.util.List;

public class Validator {
    private boolean isOneErrorFail;
    private boolean isThrowException;

    public Validator setIsOneErrorFail(boolean oneErrorFail) {
        this.isOneErrorFail = oneErrorFail;
        return this;
    }

    public Validator setThrowException(boolean isThrowException) {
        this.isOneErrorFail = isThrowException;
        return this;
    }

    public boolean validate(List<ValidationChain> rules) {
        boolean check = true;
        StringBuilder messageBuilder = new StringBuilder();
        for (ValidationChain chain : rules) {
            for (ValidationRule rule : chain.getValidations()) {
                rule.setSetThrowingException(isOneErrorFail);
                boolean isValid = rule.validate();
                check = check && isValid;
                if (isOneErrorFail && !isValid) {
                    messageBuilder.append(rule.getMessage()).append("\n");
                }
            }
        }
        if (isThrowException && !check) {
            throw new ValidationException(messageBuilder.toString());
        }
        return check;
    }
}
