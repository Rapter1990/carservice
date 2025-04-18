package com.example.carservice.carservice.utils.annotation;

import com.example.carservice.carservice.utils.validator.TurkishLicensePlateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Custom validation annotation for verifying Turkish license plate formats.
 * <p>
 * Should be applied to fields representing vehicle license plates.
 * </p>
 *
 * <p>Supported formats include:</p>
 * <ul>
 *   <li>99 X 9999</li>
 *   <li>99 X 99999</li>
 *   <li>99 XX 999</li>
 *   <li>99 XX 9999</li>
 *   <li>99 XXX 99</li>
 * </ul>
 *
 * @see TurkishLicensePlateValidator
 */
@Documented
@Constraint(validatedBy = TurkishLicensePlateValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTurkishPlate {
    String message() default "Invalid Turkish license plate format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
