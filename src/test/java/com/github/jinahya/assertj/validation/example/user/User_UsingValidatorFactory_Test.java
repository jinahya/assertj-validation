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
 * Tests {@code usingValidatorFactory(ValidatorFactory)}.
 */
class User_UsingValidatorFactory_Test {

    @DisplayName("usingValidatorFactory(null) resets to the default")
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

    @DisplayName("the factory is not closed by the assertion")
    @Test
    void __FactoryStaysOpen() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            assertThatBean(newValidUser()).usingValidatorFactory(factory).isValid();
            // the caller owns the instance, so it must still be usable afterwards
            assertThat(factory.getValidator().validate(newValidUser())).isEmpty();
        }
    }

    @DisplayName("getValidator() is invoked once, at configuration time")
    @Test
    void __OneGetValidatorPerConfiguration() {
        try (ValidatorFactory delegate = Validation.buildDefaultValidatorFactory()) {
            final var count = new AtomicInteger();
            final ValidatorFactory counting = (ValidatorFactory) java.lang.reflect.Proxy.newProxyInstance(
                    ValidatorFactory.class.getClassLoader(),
                    new Class<?>[]{ValidatorFactory.class},
                    (proxy, method, args) -> {
                        if ("getValidator".equals(method.getName())) {
                            count.incrementAndGet();
                        }
                        return method.invoke(delegate, args);
                    });
            final var assertion = assertThatBean(newValidUser()).usingValidatorFactory(counting);
            assertThat(count).as("taken eagerly, when the factory is supplied").hasValue(1);
            assertion.isValid();
            assertion.isValid();
            assertThat(count).as("a ValidatorFactory has no mutator, so there is nothing to re-read")
                    .hasValue(1);
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
