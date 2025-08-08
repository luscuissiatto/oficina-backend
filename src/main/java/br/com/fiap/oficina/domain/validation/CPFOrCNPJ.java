package br.com.fiap.oficina.domain.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CPFOrCNPJValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CPFOrCNPJ {
    String message() default "Documento (CPF/CNPJ) inválido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
