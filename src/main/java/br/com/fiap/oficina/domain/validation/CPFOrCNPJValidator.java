package br.com.fiap.oficina.domain.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CPFOrCNPJValidator implements ConstraintValidator<CPFOrCNPJ, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }

        String cleanedValue = value.replaceAll("\\D", "");

        return cleanedValue.length() == 11 || cleanedValue.length() == 14;
    }
}
