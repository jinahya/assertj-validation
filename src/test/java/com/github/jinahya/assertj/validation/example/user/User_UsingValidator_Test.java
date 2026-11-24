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

import jakarta.validation.ConstraintViolation;
import jakarta.validation.MessageInterpolator;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatBean;
import static com.github.jinahya.assertj.validation.example.user.User.newValidUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests {@code usingValidator(Validator)}. A validator supplied by the caller stays the caller's: the assertion only
 * uses it, and every use here happens while the factory which produced it is still open, as
 * {@link ValidatorFactory#close()} requires.
 */
class User_UsingValidator_Test {

    /**
     * Wraps specified factory so that validators taken from it fail once it is closed, rather than relying on the
     * provider to reject them; Hibernate Validator does not.
     */
    private static ValidatorFactory closeAware(final ValidatorFactory delegate, final AtomicBoolean closed) {
        return (ValidatorFactory) Proxy.newProxyInstance(
                ValidatorFactory.class.getClassLoader(),
                new Class<?>[]{ValidatorFactory.class},
                (proxy, method, args) -> {
                    if ("close".equals(method.getName())) {
                        closed.set(true);
                        return null;
                    }
                    if ("getValidator".equals(method.getName())) {
                        final Validator validator = delegate.getValidator();
                        return Proxy.newProxyInstance(
                                Validator.class.getClassLoader(),
                                new Class<?>[]{Validator.class},
                                (p, m, a) -> {
                                    if (closed.get()) {
                                        throw new IllegalStateException("validator used after its factory closed");
                                    }
                                    try {
                                        return m.invoke(validator, a);
                                    } catch (final InvocationTargetException ite) {
                                        throw ite.getCause();
                                    }
                                }
                        );
                    }
                    return method.invoke(delegate, args);
                }
        );
    }

    @DisplayName("usingValidator(null) falls back to the per-assertion default")
    @Test
    void __Null() {
        assertThatBean(newValidUser()).usingValidator(null).isValid();
    }

    @DisplayName("usingValidator(non-null) validates, while the caller's factory is open")
    @Test
    void __NonNull() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            final Validator validator = factory.getValidator();
            assertThatBean(newValidUser()).usingValidator(validator).isValid();
            assertThatBean(User.of(null, 0)).usingValidator(validator).isNotValid();
        }
    }

    @DisplayName("a validator customized through usingContext() is the one validating")
    @Test
    void __Customized() {
        final MessageInterpolator fixed = new MessageInterpolator() {
            @Override
            public String interpolate(final String messageTemplate, final Context context) {
                return "customized";
            }

            @Override
            public String interpolate(final String messageTemplate, final Context context, final Locale locale) {
                return "customized";
            }
        };
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            final Validator validator = factory.usingContext().messageInterpolator(fixed).getValidator();
            assertThatBean(User.of(null, 0)).usingValidator(validator).isNotValid(s -> assertThat(s)
                    .extracting(ConstraintViolation::getMessage)
                    .containsOnly("customized"));
        }
    }

    @DisplayName("the assertion does not close the factory of the validator it was given")
    @Test
    void __NotClosed() {
        final var closed = new AtomicBoolean();
        try (ValidatorFactory real = Validation.buildDefaultValidatorFactory()) {
            final ValidatorFactory factory = closeAware(real, closed);
            final Validator validator = factory.getValidator();
            assertThatBean(newValidUser()).usingValidator(validator).isValid();
            assertThatBean(newValidUser()).usingValidator(validator).isValid();
            assertThat(closed).as("the caller's factory stays open").isFalse();
        }
    }

    @DisplayName("the close-aware double detects a validator used after its factory closed")
    @Test
    void __UsedAfterClose() {
        final var closed = new AtomicBoolean();
        try (ValidatorFactory real = Validation.buildDefaultValidatorFactory()) {
            final ValidatorFactory factory = closeAware(real, closed);
            final Validator validator = factory.getValidator();
            factory.close();
            assertThatThrownBy(() -> assertThatBean(newValidUser()).usingValidator(validator).isValid())
                    .isInstanceOf(IllegalStateException.class);
        }
    }
}
