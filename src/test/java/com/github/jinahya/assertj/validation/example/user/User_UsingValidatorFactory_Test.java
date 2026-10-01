package com.github.jinahya.assertj.validation.example.user;

/*-
 * #%L
 * assertj-validation
 * %%
 * Copyright (C) 2021 - 2023 Jinahya, Inc.
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

import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatBean;
import static com.github.jinahya.assertj.validation.example.user.User.newValidUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests {@code usingValidatorFactory(ValidatorFactory)} and how it interacts with
 * {@code usingValidator(Validator)}.
 */
class User_UsingValidatorFactory_Test {

    private static ValidatorFactory counting(final ValidatorFactory delegate, final AtomicInteger count) {
        return (ValidatorFactory) java.lang.reflect.Proxy.newProxyInstance(
                ValidatorFactory.class.getClassLoader(), new Class<?>[]{ValidatorFactory.class},
                (proxy, method, args) -> {
                    if ("getValidator".equals(method.getName())) {
                        count.incrementAndGet();
                    }
                    if ("close".equals(method.getName())) {
                        throw new AssertionError("the assertion must not close a caller's factory");
                    }
                    return method.invoke(delegate, args);
                });
    }

    @DisplayName("usingValidatorFactory(null) falls back to the per-assertion default")
    @Test
    void __Null() {
        assertThatBean(newValidUser()).usingValidatorFactory(null).isValid();
    }

    @DisplayName("usingValidatorFactory(non-null) validates through the given factory")
    @Test
    void __NonNull() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            assertThatBean(newValidUser()).usingValidatorFactory(factory).isValid();
            assertThatBean(User.of(null, 0)).usingValidatorFactory(factory).isNotValid();
        }
    }

    @DisplayName("the assertion never closes a caller's factory")
    @Test
    void __NeverClosed() {
        try (ValidatorFactory real = Validation.buildDefaultValidatorFactory()) {
            final var count = new AtomicInteger();
            final var factory = counting(real, count);   // its close() fails the test
            assertThatBean(newValidUser()).usingValidatorFactory(factory).isValid();
            assertThat(real.getValidator().validate(newValidUser()))
                    .as("still usable afterwards").isEmpty();
        }
    }

    @DisplayName("a validator is taken from the factory once per assertion")
    @Test
    void __OncePerAssertion() {
        try (ValidatorFactory real = Validation.buildDefaultValidatorFactory()) {
            final var count = new AtomicInteger();
            final var assertion = assertThatBean(newValidUser()).usingValidatorFactory(counting(real, count));
            assertThat(count).as("nothing taken at configuration time").hasValue(0);
            assertion.isValid();
            assertion.isValid();
            assertThat(count).as("one per assertion").hasValue(2);
        }
    }

    @DisplayName("validator and factory are alternatives; the last call wins")
    @Test
    void __LastCallWins() {
        try (ValidatorFactory real = Validation.buildDefaultValidatorFactory()) {
            final var count = new AtomicInteger();
            final var factory = counting(real, count);
            assertThatBean(newValidUser())
                    .usingValidatorFactory(factory)
                    .usingValidator(real.getValidator())    // supersedes the factory
                    .isValid();
            assertThat(count).as("the superseded factory is never consulted").hasValue(0);

            final var count2 = new AtomicInteger();
            assertThatBean(newValidUser())
                    .usingValidator(real.getValidator())
                    .usingValidatorFactory(counting(real, count2))   // supersedes the validator
                    .isValid();
            assertThat(count2).as("the factory now supplies it").hasValue(1);
        }
    }

    @DisplayName("a failing assertion still reports through the given factory")
    @Test
    void __Failing() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            assertThatThrownBy(() -> assertThatBean(User.of(null, 0)).usingValidatorFactory(factory).isValid())
                    .isInstanceOf(AssertionError.class);
        }
    }
}
