package com.github.jinahya.assertj.validation;

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

import org.assertj.core.api.AbstractAssert;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link ValidationBDDAssertions} and {@link WithValidationAssertions} keep parity with {@link ValidationAssertions}:
 * one method per public {@code assertThat…} factory, of the same parameter and generic return types, delegating to it.
 */
class EntryPointFacades_Test {

    private static final WithValidationAssertions WITH = new WithValidationAssertions() {
    };

    private static List<Method> factories() {
        return Arrays.stream(ValidationAssertions.class.getDeclaredMethods())
                .filter(m -> !m.isSynthetic())
                .filter(m -> Modifier.isPublic(m.getModifiers()) && Modifier.isStatic(m.getModifiers()))
                .filter(m -> m.getName().startsWith("assertThat"))
                .toList();
    }

    /**
     * Invokes specified method with nulls, which every factory accepts, building an assertion over {@code null}.
     */
    private static Object invokeWithNulls(final Method method, final Object target) throws Exception {
        return method.invoke(target, new Object[method.getParameterCount()]);
    }

    @Test
    void should_have_a_then_for_each_assertThat() throws Exception {
        final var factories = factories();
        assertThat(factories).hasSize(27);
        for (final var factory : factories) {
            final var name = "then" + factory.getName().substring("assertThat".length());
            final var then = ValidationBDDAssertions.class.getMethod(name, factory.getParameterTypes());
            assertThat(Modifier.isStatic(then.getModifiers())).as(name).isTrue();
            assertThat(then.getGenericReturnType().getTypeName()).as(name)
                    .isEqualTo(factory.getGenericReturnType().getTypeName());
            assertThat(invokeWithNulls(then, null)).as(name)
                    .isInstanceOf(AbstractAssert.class)
                    .hasSameClassAs(invokeWithNulls(factory, null));
        }
        assertThat(Arrays.stream(ValidationBDDAssertions.class.getDeclaredMethods())
                           .filter(m -> !m.isSynthetic() && Modifier.isPublic(m.getModifiers())))
                .hasSameSizeAs(factories);
    }

    @Test
    void should_have_a_default_for_each_assertThat() throws Exception {
        final var factories = factories();
        for (final var factory : factories) {
            final var method = WithValidationAssertions.class.getMethod(factory.getName(), factory.getParameterTypes());
            assertThat(method.isDefault()).as(factory.getName()).isTrue();
            assertThat(method.getGenericReturnType().getTypeName()).as(factory.getName())
                    .isEqualTo(factory.getGenericReturnType().getTypeName());
            assertThat(invokeWithNulls(method, WITH)).as(factory.getName())
                    .isInstanceOf(AbstractAssert.class)
                    .hasSameClassAs(invokeWithNulls(factory, null));
        }
        assertThat(WithValidationAssertions.class.getDeclaredMethods()).hasSameSizeAs(factories);
    }

    @Test
    void should_not_instantiate_the_bdd_entry_points() throws Exception {
        final var constructor = ValidationBDDAssertions.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        assertThatThrownBy(constructor::newInstance).hasCauseInstanceOf(AssertionError.class);
    }
}
