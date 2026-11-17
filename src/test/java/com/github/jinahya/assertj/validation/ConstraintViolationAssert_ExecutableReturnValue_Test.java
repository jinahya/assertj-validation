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

class ConstraintViolationAssert_ExecutableReturnValue_Test
        extends ConstraintViolationAssert__Base {

    @Test
    void should_pass_if_executable_return_value_is_equal() {
        final var actual = violation();
        when(actual.getExecutableReturnValue()).thenReturn("r");
        assertThatCode(() -> assertion(actual).hasExecutableReturnValue("r")).doesNotThrowAnyException();
        returnsSelf(assertion(actual), a -> a.hasExecutableReturnValue("r"));
    }

    @Test
    void should_fail_naming_the_attribute_if_executable_return_value_differs() {
        final var actual = violation();
        when(actual.getExecutableReturnValue()).thenReturn("r");
        assertThatThrownBy(() -> assertion(actual).hasExecutableReturnValue("s"))
                .hasMessageStartingWith("[executableReturnValue]");
    }

    @Test
    void should_extract_the_executable_return_value_as_an_object_assertion() {
        final var actual = violation();
        when(actual.getExecutableReturnValue()).thenReturn("r");
        // returns(..) is declared on AbstractObjectAssert, not on Assert
        assertThatCode(() -> assertion(actual).extractingExecutableReturnValue().returns("r", String::valueOf))
                .doesNotThrowAnyException();
    }

    @Test
    void should_pass_if_there_is_no_executable_return_value() {
        assertThatCode(() -> assertion(violation()).hasNoExecutableReturnValue()).doesNotThrowAnyException();
    }

    @Test
    void should_fail_if_actual_is_null() {
        assertThatThrownBy(() -> assertion(null).hasNoExecutableReturnValue())
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Expecting actual not to be null");
    }
}
