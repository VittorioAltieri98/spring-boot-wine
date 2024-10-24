package com.wine.auth.validation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!\\-_?])(?=\\S+$).{8,}$";

    private Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }
        if (!pattern.matcher(password).matches()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(getErrorMessage(password))
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private String getErrorMessage(String password) {
        if (password.length() < 8) {
            return "La password deve avere almeno 8 caratteri.";
        }
        if (!password.matches(".*[0-9].*")) {
            return "La password deve contenere almeno un numero.";
        }
        if (!password.matches(".*[a-z].*")) {
            return "La password deve contenere almeno una lettera minuscola.";
        }
        if (!password.matches(".*[A-Z].*")) {
            return "La password deve contenere almeno una lettera maiuscola.";
        }
        if (!password.matches(".*[@#$%^&+=!\\-_?].*")) {
            return "La password deve contenere almeno un carattere speciale.";
        }
        if (password.matches(".*\\s.*")) {
            return "La password non deve contenere spazi bianchi.";
        }
        return "Password non valida";
    }
}

