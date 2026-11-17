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
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.github.jinahya.assertj.validation.ValidationAssertionsTestUtils.applyValidator;
import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatConstraintViolations;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the publicly reachable assertion for the {@link Set} of constraint violations that
 * {@link jakarta.validation.Validator#validate(Object, Class[])} returns.
 */
class ConstraintViolations_Test {

    private static Set<ConstraintViolation<User>> validate(final User user) {
        return applyValidator(v -> v.validate(user));
    }

    @Test
    void __empty() {
        final var violations = validate(User.newValidUser());
        assertThat(violations).isEmpty();
        assertThatConstraintViolations(violations).isEmpty();
    }

    @Test
    void __notEmpty() {
        final var violations = validate(User.newUser(false, false));
        assertThat(violations).isNotEmpty();
        assertThatConstraintViolations(violations)
                .isNotEmpty()
                .allSatisfy(v -> assertThat(v.getMessage()).isNotBlank());
    }

    @Test
    void __navigatesToElementAssert() {
        final var violations = validate(User.of(null, 0));
        assertThatConstraintViolations(violations)
                .isNotEmpty()
                .first()
                .isNotNull();
    }

    /**
     * Filtering creates a new iterable assertion through {@code newAbstractIterableAssert}; the result must still be a
     * violation assertion, navigating to typed element assertions.
     */
    @Test
    void __filteredOn_keepsTypedNavigation() {
        final var violations = validate(User.of(null, -1));
        assertThatConstraintViolations(violations)
                .hasSize(2)
                .filteredOn(v -> v.getPropertyPath().toString().equals(User.PROPERTY_NAME_NAME))
                .hasSize(1)
                .first()
                .hasMessageTemplate("{jakarta.validation.constraints.NotBlank.message}");
    }

    @Test
    void __filteredOn_noMatch() {
        final var violations = validate(User.of(null, -1));
        assertThatConstraintViolations(violations)
                .filteredOn(v -> v.getPropertyPath().toString().equals("unknown"))
                .isEmpty();
    }
}
