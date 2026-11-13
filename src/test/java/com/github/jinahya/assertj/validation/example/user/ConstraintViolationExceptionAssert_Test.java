package com.github.jinahya.assertj.validation.example.user;

/*-
 * #%L
 * assertj-validation
 * %%
 * Copyright (C) 2021 - 2022 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatConstraintViolationException;
import static com.github.jinahya.assertj.validation.ValidationAssertionsTestUtils.applyValidator;
import static com.github.jinahya.assertj.validation.ValidationInstanceOfAssertFactories.CONSTRAINT_VIOLATION_EXCEPTION;
import static com.github.jinahya.assertj.validation.ValidationInstanceOfAssertFactories.constraintViolation;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests the assertion for {@link ConstraintViolationException}, the exception a framework throws when it, rather than
 * the test, triggers validation.
 */
class ConstraintViolationExceptionAssert_Test {

    private static ConstraintViolationException exception(final User user) {
        final Set<ConstraintViolation<User>> violations = applyValidator(v -> v.validate(user));
        return new ConstraintViolationException("invalid user", violations);
    }

    @Test
    void hasViolationOn__() {
        assertThatConstraintViolationException(exception(User.of(null, -1)))
                .hasMessage("invalid user")
                .hasViolationOn(User.PROPERTY_NAME_NAME)
                .hasViolationOn(User.PROPERTY_NAME_AGE);
    }

    @Test
    void hasViolationOn__missing() {
        assertThatThrownBy(() -> assertThatConstraintViolationException(exception(User.of(null, 0)))
                .hasViolationOn(User.PROPERTY_NAME_AGE))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("to have a constraint violation on")
                .hasMessageContaining("[" + User.PROPERTY_NAME_NAME + "]");
    }

    @Test
    void hasViolationOn__noViolations() {
        final var empty = new ConstraintViolationException(Set.of());
        assertThatConstraintViolationException(empty)
                .extractingConstraintViolations()
                .isEmpty();
        assertThatThrownBy(() -> assertThatConstraintViolationException(empty).hasViolationOn("name"))
                .isInstanceOf(AssertionError.class);
        assertThatThrownBy(() -> assertThatConstraintViolationException(new ConstraintViolationException(null))
                .hasViolationOn("name"))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("(none)");
    }

    @Test
    void extractingConstraintViolations__() {
        assertThatConstraintViolationException(exception(User.of(null, 0)))
                .extractingConstraintViolations()
                .singleElement()
                .asInstanceOf(constraintViolation())
                .hasMessageTemplate("{jakarta.validation.constraints.NotBlank.message}");
    }

    @Test
    void null__() {
        assertThatThrownBy(() -> assertThatConstraintViolationException(null).hasViolationOn("name"))
                .isInstanceOf(AssertionError.class);
        assertThatThrownBy(() -> assertThatConstraintViolationException(null).extractingConstraintViolations())
                .isInstanceOf(AssertionError.class);
    }

    @Test
    void factory__fromAThrownException() {
        final var thrown = exception(User.of(null, 0));
        assertThat((Throwable) new RuntimeException("wrapped", thrown))
                .cause()
                .asInstanceOf(CONSTRAINT_VIOLATION_EXCEPTION)
                .hasViolationOn(User.PROPERTY_NAME_NAME);
    }
}
