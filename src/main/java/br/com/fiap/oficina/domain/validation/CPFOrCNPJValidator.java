package br.com.fiap.oficina.domain.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CPFOrCNPJValidator implements ConstraintValidator<CPFOrCNPJ, String> {
    private static final Logger logger = LoggerFactory.getLogger(CPFOrCNPJValidator.class);

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }

        String cleaned = value.replaceAll("[.\\-/]", "");

        if (!cleaned.matches("^\\d+$")) {
            return false;
        }

        boolean isValidLength = cleaned.length() == 11 || cleaned.length() == 14;

        return isValidLength;
    }
}