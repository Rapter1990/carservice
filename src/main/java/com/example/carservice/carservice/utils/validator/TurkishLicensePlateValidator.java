package com.example.carservice.carservice.utils.validator;

import com.example.carservice.carservice.utils.annotation.ValidTurkishPlate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Validator implementation for the {@link ValidTurkishPlate} custom constraint.
 * Validates that the provided license plate matches the Turkish license plate format.
 */
public class TurkishLicensePlateValidator implements ConstraintValidator<ValidTurkishPlate, String> {

    //R: Number
    //H: Letter
    // “99 X 9999”, “99 X 99999” , “99 XX 999”, “99 XX 9999” , “99 XXX 99”
    //
    private static final Pattern TURKISH_PLATE_PATTERN = Pattern.compile(
            "^(0[1-9]|[1-7][0-9]|8[01])(([A-Z])(\\d{4,5})|([A-Z]{2})(\\d{3,4})|([A-Z]{3})(\\d{2,3}))$"
    );

    /**
     * Validates whether the given license plate string matches Turkish license plate formats.
     *
     * @param plate the plate string to validate
     * @param context context in which the constraint is evaluated
     * @return true if valid, false otherwise
     */

    @Override
    public boolean isValid(String plate, ConstraintValidatorContext context) {
        if (plate == null || plate.isBlank()) {
            return false;
        }
        return TURKISH_PLATE_PATTERN.matcher(plate.replaceAll("\\s+", "").toUpperCase()).matches();
    }

}
