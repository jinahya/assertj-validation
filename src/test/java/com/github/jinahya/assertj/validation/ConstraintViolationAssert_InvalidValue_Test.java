package com.github.jinahya.assertj.validation;

/*-
 * #%L
 * assertj-validation
 * %%
 * Copyright (C) 2021 - 2024 Jinahya, Inc.
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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class ConstraintViolationAssert_InvalidValue_Test
        extends ConstraintViolationAssert__Base {

    @Test
    void should_pass_if_invalid_value_is_equal() {
        final var actual = violation();
        when(actual.getInvalidValue()).thenReturn("x");
        assertThatCode(() -> assertion(actual).hasInvalidValue("x")).doesNotThrowAnyException();
        returnsSelf(assertion(actual), a -> a.hasInvalidValue("x"));
    }

    @Test
    void should_fail_naming_the_attribute_if_invalid_value_differs() {
        final var actual = violation();
        when(actual.getInvalidValue()).thenReturn("x");
        assertThatThrownBy(() -> assertion(actual).hasInvalidValue("y"))
                .hasMessageStartingWith("[invalidValue]");
    }

    @Test
    void should_fail_honoring_the_description() {
        final var actual = violation();
        assertThatThrownBy(() -> assertion(actual).as("case 42").hasInvalidValue("y"))
                .hasMessageStartingWith("[case 42: invalidValue]");
    }

    @Test
    void should_extract_the_invalid_value() {
        final var actual = violation();
        when(actual.getInvalidValue()).thenReturn(42);
        assertThatCode(() -> assertion(actual).extractingInvalidValue().isEqualTo(42)).doesNotThrowAnyException();
    }

    @Test
    void should_pass_if_invalid_value_is_null_as_expected() {
        // a null value of a non-null violation is not a null actual
        assertThatCode(() -> assertion(violation()).hasInvalidValue(null)).doesNotThrowAnyException();
    }

    @Test
    void should_extract_the_invalid_value_through_a_typed_factory() {
        final var actual = violation();
        when(actual.getInvalidValue()).thenReturn("xyz");
        assertThatCode(() -> assertion(actual)
                .extractingInvalidValue(jakarta.validation.ConstraintViolation::getInvalidValue,
                                        org.assertj.core.api.InstanceOfAssertFactories.STRING)
                .startsWith("x"))
                .doesNotThrowAnyException();
    }

    @Test
    void should_fail_if_actual_is_null() {
        assertThatThrownBy(() -> assertion(null).hasInvalidValue("x"))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Expecting actual not to be null");
    }
}
