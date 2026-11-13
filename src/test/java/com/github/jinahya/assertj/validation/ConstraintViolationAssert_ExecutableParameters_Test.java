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

class ConstraintViolationAssert_ExecutableParameters_Test
        extends ConstraintViolationAssert_$Test {

    @Test
    void should_pass_if_executable_parameters_are_equal() {
        final var actual = violation();
        when(actual.getExecutableParameters()).thenReturn(new Object[]{"a", 1});
        assertThatCode(() -> assertion(actual).hasExecutableParameters(new Object[]{"a", 1}))
                .doesNotThrowAnyException();
        returnsSelf(assertion(actual), a -> a.hasExecutableParameters(new Object[]{"a", 1}));
    }

    @Test
    void should_fail_naming_the_attribute_if_executable_parameters_differ() {
        final var actual = violation();
        when(actual.getExecutableParameters()).thenReturn(new Object[]{"a", 1});
        assertThatThrownBy(() -> assertion(actual).hasExecutableParameters(new Object[]{"a", 2}))
                .hasMessageStartingWith("[executableParameters]");
    }

    @Test
    void should_pass_if_there_are_no_executable_parameters() {
        assertThatCode(() -> assertion(violation()).hasNoExecutableParameters()).doesNotThrowAnyException();
    }

    @Test
    void should_extract_the_executable_parameters() {
        final var actual = violation();
        when(actual.getExecutableParameters()).thenReturn(new Object[]{"a", 1});
        assertThatCode(() -> assertion(actual).extractingExecutableParameters().containsExactly("a", 1))
                .doesNotThrowAnyException();
    }

    @Test
    void should_fail_if_actual_is_null() {
        assertThatThrownBy(() -> assertion(null).hasNoExecutableParameters())
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Expecting actual not to be null");
    }
}
