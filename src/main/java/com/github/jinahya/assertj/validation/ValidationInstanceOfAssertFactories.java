package com.github.jinahya.assertj.validation;

import org.assertj.core.api.InstanceOfAssertFactory;

import javax.validation.ConstraintViolation;

public interface ValidationInstanceOfAssertFactories {

    @SuppressWarnings({
            "java:S3740" // ConstraintViolation // <?>
    })
    InstanceOfAssertFactory<ConstraintViolation, AbstractConstraintViolationAssert<?>> CONSTRAINT_VIOLATION =
            new InstanceOfAssertFactory<>(
                    ConstraintViolation.class,
                    ValidationAssertions::assertThatConstraintViolation
            );
}
