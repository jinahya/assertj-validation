package org.hibernate.validator.referenceguide.chapter01;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Car_Test {

    private static Validator validator;

    @BeforeAll
    public static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void manufacturerIsNull() {
        org.hibernate.validator.referenceguide.chapter01.Car car = new org.hibernate.validator.referenceguide.chapter01.Car(null, "DD-AB-123", 4);

        Set<ConstraintViolation<org.hibernate.validator.referenceguide.chapter01.Car>> constraintViolations =
                validator.validate(car);

        assertEquals(1, constraintViolations.size());
        assertEquals("must not be null", constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void licensePlateTooShort() {
        org.hibernate.validator.referenceguide.chapter01.Car car = new org.hibernate.validator.referenceguide.chapter01.Car("Morris", "D", 4);

        Set<ConstraintViolation<org.hibernate.validator.referenceguide.chapter01.Car>> constraintViolations =
                validator.validate(car);

        assertEquals(1, constraintViolations.size());
        assertEquals(
                "size must be between 2 and 14",
                constraintViolations.iterator().next().getMessage()
        );
    }

    @Test
    public void seatCountTooLow() {
        org.hibernate.validator.referenceguide.chapter01.Car car = new org.hibernate.validator.referenceguide.chapter01.Car("Morris", "DD-AB-123", 1);

        Set<ConstraintViolation<org.hibernate.validator.referenceguide.chapter01.Car>> constraintViolations =
                validator.validate(car);

        assertEquals(1, constraintViolations.size());
        assertEquals(
                "must be greater than or equal to 2",
                constraintViolations.iterator().next().getMessage()
        );
    }

    @Test
    public void carIsValid() {
        org.hibernate.validator.referenceguide.chapter01.Car car = new org.hibernate.validator.referenceguide.chapter01.Car("Morris", "DD-AB-123", 2);

        Set<ConstraintViolation<org.hibernate.validator.referenceguide.chapter01.Car>> constraintViolations =
                validator.validate(car);

        assertEquals(0, constraintViolations.size());
    }
}
