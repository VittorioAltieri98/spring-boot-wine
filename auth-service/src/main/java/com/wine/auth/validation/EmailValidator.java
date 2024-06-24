package com.wine.auth.validation;

import com.wine.auth.utils.AllowedEmailDomain;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class EmailValidator implements ConstraintValidator<ValidEmail, String> {

    private final AllowedEmailDomain[] allowedDomains = AllowedEmailDomain.values();

    @Override
    public void initialize(ValidEmail constraintAnnotation) {
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null) {
            return false;
        }

        // Verifica se l'email è nel formato valido
        if (!email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
            return false;
        }

        // Estrae il dominio dall'email
        String[] parts = email.split("@");
        String domain = parts[1];

        // Verifica se il dominio è tra quelli consentiti
        return Arrays.stream(allowedDomains)
                .anyMatch(allowedDomain -> allowedDomain.getDomain().equalsIgnoreCase(domain));
    }
}
