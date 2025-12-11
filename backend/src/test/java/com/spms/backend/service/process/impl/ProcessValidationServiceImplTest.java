package com.spms.backend.service.process.impl;

import com.spms.backend.service.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProcessValidationServiceImplTest {

    private ProcessValidationServiceImpl validationService;

    @BeforeEach
    void setUp() {
        validationService = new ProcessValidationServiceImpl();
    }

    @Test
    void validateVersion_validFormat_shouldNotThrowException() {
        assertDoesNotThrow(() -> validationService.validateVersion("1.0.0"));
        assertDoesNotThrow(() -> validationService.validateVersion("0.1.2"));
        assertDoesNotThrow(() -> validationService.validateVersion("10.20.30"));
    }

    @Test
    void validateVersion_emptyVersion_shouldThrowValidationException() {
        ValidationException exception = assertThrows(ValidationException.class,
            () -> validationService.validateVersion(""));
        assertEquals("Version cannot be null or empty", exception.getMessage());
    }

    @Test
    void validateVersion_nullVersion_shouldThrowValidationException() {
        ValidationException exception = assertThrows(ValidationException.class,
            () -> validationService.validateVersion(null));
        assertEquals("Version cannot be null or empty", exception.getMessage());
    }

    @Test
    void validateVersion_tooFewComponents_shouldThrowValidationException() {
        ValidationException exception = assertThrows(ValidationException.class,
            () -> validationService.validateVersion("1.0"));
        assertEquals("Version must follow major.minor.revision format (e.g., 1.0.0)", exception.getMessage());
    }

    @Test
    void validateVersion_tooManyComponents_shouldThrowValidationException() {
        ValidationException exception = assertThrows(ValidationException.class,
            () -> validationService.validateVersion("1.0.0.1"));
        assertEquals("Version must follow major.minor.revision format (e.g., 1.0.0)", exception.getMessage());
    }

    @Test
    void validateVersion_nonNumericComponent_shouldThrowValidationException() {
        ValidationException exception = assertThrows(ValidationException.class,
            () -> validationService.validateVersion("1.a.0"));
        assertEquals("Version components must be integers", exception.getMessage());
    }

    @Test
    void validateVersion_negativeComponent_shouldThrowValidationException() {
        ValidationException exception = assertThrows(ValidationException.class,
            () -> validationService.validateVersion("1.-1.0"));
        assertEquals("Version components must be non-negative integers", exception.getMessage());
    }

    @Test
    void validateVersion_leadingZeros_shouldNotThrowException() {
        assertDoesNotThrow(() -> validationService.validateVersion("01.02.03"));
    }
}
