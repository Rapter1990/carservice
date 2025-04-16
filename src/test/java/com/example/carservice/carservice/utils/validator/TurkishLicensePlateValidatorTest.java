package com.example.carservice.carservice.utils.validator;

import com.example.carservice.base.AbstractBaseServiceTest;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class TurkishLicensePlateValidatorTest extends AbstractBaseServiceTest {

    private TurkishLicensePlateValidator turkishLicensePlateValidator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilder;

    @BeforeEach
    void setUp() {

        turkishLicensePlateValidator = new TurkishLicensePlateValidator();

        // Stub the context so that buildConstraintViolationWithTemplate returns a non-null builder.
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);

        // Stub addPropertyNode to return a NodeBuilderCustomizableContext
        when(violationBuilder.addPropertyNode(anyString())).thenReturn(nodeBuilder);

        // Stub addConstraintViolation to return the context
        when(nodeBuilder.addConstraintViolation()).thenReturn(context);

    }

    @Test
    @DisplayName("Should return false when plate is null")
    void testNullPlateReturnsFalse() {
        assertFalse(turkishLicensePlateValidator.isValid(null, context));
    }

    @Test
    @DisplayName("Should return false when plate is blank")
    void testBlankPlateReturnsFalse() {
        assertFalse(turkishLicensePlateValidator.isValid("   ", context));
    }

    @Test
    @DisplayName("Valid: 34 A 1234")
    void validFormat_OneLetterFourDigit() {
        assertTrue(turkishLicensePlateValidator.isValid("34 A 1234", context));
    }

    @Nested
    @DisplayName("Valid License Plates")
    class ValidPlates {

        @Test
        @DisplayName("Valid: 34 A 12345")
        void validFormat_OneLetterFiveDigit() {
            assertTrue(turkishLicensePlateValidator.isValid("34 A 12345", context));
        }

        @Test
        @DisplayName("Valid: 06 AB 123")
        void validFormat_TwoLetterThreeDigit() {
            assertTrue(turkishLicensePlateValidator.isValid("06 AB 123", context));
        }

        @Test
        @DisplayName("Valid: 06 AB 1234")
        void validFormat_TwoLetterFourDigit() {
            assertTrue(turkishLicensePlateValidator.isValid("06 AB 1234", context));
        }

        @Test
        @DisplayName("Valid: 01 ABC 12")
        void validFormat_ThreeLetterTwoDigit() {
            assertTrue(turkishLicensePlateValidator.isValid("01 ABC 12", context));
        }

        @Test
        @DisplayName("Valid: 01 ABC 123")
        void validFormat_ThreeLetterThreeDigit() {
            assertTrue(turkishLicensePlateValidator.isValid("01 ABC 123", context));
        }

        @Test
        @DisplayName("Valid: lowercase should be uppercased internally")
        void validFormat_WithLowercase() {
            assertTrue(turkishLicensePlateValidator.isValid("34 ab 1234", context));
        }
    }

    @Nested
    @DisplayName("Invalid License Plates")
    class InvalidPlates {

        @Test
        @DisplayName("Invalid: Province code out of range")
        void invalidFormat_WrongProvinceCode() {
            assertFalse(turkishLicensePlateValidator.isValid("90 AB 1234", context)); // 90 not valid
        }

        @Test
        @DisplayName("Invalid: Incomplete plate")
        void invalidFormat_IncompletePlate() {
            assertFalse(turkishLicensePlateValidator.isValid("34 AB", context)); // Missing digits
        }

        @Test
        @DisplayName("Invalid: Too many characters")
        void invalidFormat_TooLong() {
            assertFalse(turkishLicensePlateValidator.isValid("34 ABC 12345", context));
        }

        @Test
        @DisplayName("Invalid: Contains symbols")
        void invalidFormat_WithSymbols() {
            assertFalse(turkishLicensePlateValidator.isValid("34@AB#123", context));
        }

    }

}