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

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatBean;
import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatConstructor;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests the assertions backed by {@link jakarta.validation.executable.ExecutableValidator}.
 */
class ExecutableValidation_Test {

    private static Method greet() throws NoSuchMethodException {
        return Greeter.class.getMethod(Greeter.METHOD_NAME_GREET, String.class, int.class);
    }

    private static Constructor<Greeter> constructor() throws NoSuchMethodException {
        return Greeter.class.getConstructor(String.class);
    }

    // ------------------------------------------------------------------------------- validateParameters
    @Test
    void methodParameters__valid() throws Exception {
        final var greeter = new Greeter("Jane");
        assertThatCode(() -> assertThatBean(greeter).hasValidParameters(greet(), "world", 1))
                .doesNotThrowAnyException();
    }

    @Test
    void methodParameters__invalid() throws Exception {
        final var greeter = new Greeter("Jane");
        final var method = greet();
        assertThatThrownBy(() -> assertThatBean(greeter).hasValidParameters(method, "  ", 0))
                .isInstanceOf(AssertionError.class);
        assertThatCode(() -> assertThatBean(greeter).doesNotHaveValidParameters(method, "  ", 0))
                .doesNotThrowAnyException();
    }

    @Test
    void methodParameters__acceptsViolations() throws Exception {
        final var greeter = new Greeter("Jane");
        assertThatBean(greeter).hasValidParameters(greet(), new Object[]{"world", 1},
                                                   s -> org.assertj.core.api.Assertions.assertThat(s).isEmpty());
    }

    // ------------------------------------------------------------------------------ validateReturnValue
    @Test
    void methodReturnValue__valid() throws Exception {
        final var greeter = new Greeter("Jane");
        assertThatCode(() -> assertThatBean(greeter).hasValidReturnValue(greet(), "Jane greets world"))
                .doesNotThrowAnyException();
    }

    @Test
    void methodReturnValue__invalid() throws Exception {
        final var greeter = new Greeter("Jane");
        final var method = greet();
        assertThatThrownBy(() -> assertThatBean(greeter).hasValidReturnValue(method, "   "))
                .isInstanceOf(AssertionError.class);
        assertThatCode(() -> assertThatBean(greeter).doesNotHaveValidReturnValue(method, "   "))
                .doesNotThrowAnyException();
    }

    // -------------------------------------------------------------------- validateConstructorParameters
    @Test
    void constructorParameters__valid() throws Exception {
        assertThatCode(() -> assertThatConstructor(constructor()).hasValidParameters("Jane"))
                .doesNotThrowAnyException();
    }

    @Test
    void constructorParameters__invalid() throws Exception {
        final var constructor = constructor();
        assertThatThrownBy(() -> assertThatConstructor(constructor).hasValidParameters(new Object[]{"  "}))
                .isInstanceOf(AssertionError.class);
        assertThatCode(() -> assertThatConstructor(constructor).doesNotHaveValidParameters(new Object[]{"  "}))
                .doesNotThrowAnyException();
    }

    // ------------------------------------------------------------------- validateConstructorReturnValue
    @Test
    void constructorReturnValue__valid() throws Exception {
        assertThatCode(() -> assertThatConstructor(constructor()).hasValidReturnValue(new Greeter("Jane")))
                .doesNotThrowAnyException();
    }

    @Test
    void constructorReturnValue__invalid() throws Exception {
        final var constructor = constructor();
        final var blank = new Greeter("   "); // parameter constraints do not run at construction time
        assertThatThrownBy(() -> assertThatConstructor(constructor).hasValidReturnValue(blank))
                .isInstanceOf(AssertionError.class);
        assertThatCode(() -> assertThatConstructor(constructor).doesNotHaveValidReturnValue(blank))
                .doesNotThrowAnyException();
    }

    @Test
    void constructorReturnValue__nullCreatedObjectIsAProviderContractError() throws Exception {
        final var constructor = constructor();
        // the spec makes a null created instance an IllegalArgumentException, not a constraint violation;
        // the assertion must propagate it rather than report a failed assertion.
        assertThatThrownBy(() -> assertThatConstructor(constructor).hasValidReturnValue(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
