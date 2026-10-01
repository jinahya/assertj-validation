package com.github.jinahya.assertj.validation.example.service;

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
import jakarta.validation.Path;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatPath;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests the size vocabulary of the path assertion, which counts the nodes of the path.
 */
class PathAssert_Size_Test {

    /**
     * Returns a path of two nodes: the method, and its parameter.
     */
    private static Path methodParameterPath() throws NoSuchMethodException {
        final Method method = Greeter.class.getMethod(Greeter.METHOD_NAME_GREET, String.class, int.class);
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            final ConstraintViolation<Greeter> violation = factory.getValidator().forExecutables()
                    .validateParameters(new Greeter("Jane"), method, new Object[]{"  ", 1})
                    .iterator().next();
            return violation.getPropertyPath();
        }
    }

    @DisplayName("the size assertions pass when the node count matches")
    @Test
    void sizes__Pass() throws Exception {
        final Path path = methodParameterPath();
        assertThatCode(() -> {
            assertThatPath(path)
                    .isNotEmpty()
                    .hasSize(2)
                    .hasSizeGreaterThan(1)
                    .hasSizeGreaterThanOrEqualTo(2)
                    .hasSizeLessThan(3)
                    .hasSizeLessThanOrEqualTo(2)
                    .hasSizeBetween(2, 2)
                    .hasSameSizeAs(List.of(1, 2))
                    .hasSameSizeAs(new int[2]);
            assertThatPath(null).isNullOrEmpty();
        }).doesNotThrowAnyException();
    }

    @DisplayName("the size assertions fail as assertions when the node count does not match")
    @Test
    void sizes__Fail() throws Exception {
        final Path path = methodParameterPath();
        assertThatThrownBy(() -> assertThatPath(path).isEmpty()).isInstanceOf(AssertionError.class);
        assertThatThrownBy(() -> assertThatPath(path).isNullOrEmpty()).isInstanceOf(AssertionError.class);
        assertThatThrownBy(() -> assertThatPath(path).hasSize(3))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("3");
        assertThatThrownBy(() -> assertThatPath(path).hasSizeGreaterThan(2)).isInstanceOf(AssertionError.class);
        assertThatThrownBy(() -> assertThatPath(path).hasSizeGreaterThanOrEqualTo(3))
                .isInstanceOf(AssertionError.class);
        assertThatThrownBy(() -> assertThatPath(path).hasSizeLessThan(2)).isInstanceOf(AssertionError.class);
        assertThatThrownBy(() -> assertThatPath(path).hasSizeLessThanOrEqualTo(1))
                .isInstanceOf(AssertionError.class);
        assertThatThrownBy(() -> assertThatPath(path).hasSizeBetween(3, 4)).isInstanceOf(AssertionError.class);
        assertThatThrownBy(() -> assertThatPath(path).hasSameSizeAs(List.of(1))).isInstanceOf(AssertionError.class);
        assertThatThrownBy(() -> assertThatPath(path).hasSameSizeAs(new int[1])).isInstanceOf(AssertionError.class);
        assertThatThrownBy(() -> assertThatPath(path).as("custom").hasSize(3)).hasMessageContaining("[custom]");
    }

    @DisplayName("the size assertions fail as assertions on a null path")
    @Test
    void sizes__FailOnNull() {
        assertThatThrownBy(() -> assertThatPath(null).hasSize(0)).isInstanceOf(AssertionError.class);
        assertThatThrownBy(() -> assertThatPath(null).isEmpty()).isInstanceOf(AssertionError.class);
    }
}
