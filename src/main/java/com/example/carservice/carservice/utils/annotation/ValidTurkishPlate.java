package com.example.carservice.carservice.utils.annotation;

import com.example.carservice.carservice.utils.validator.TurkishLicensePlateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TurkishLicensePlateValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTurkishPlate {
    String message() default "Invalid Turkish license plate format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
