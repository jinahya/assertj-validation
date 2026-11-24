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

import jakarta.validation.Path;
import org.assertj.core.api.AbstractClassAssert;
import org.assertj.core.api.AbstractListAssert;
import org.assertj.core.api.AssertFactory;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ClassAssert;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

// Implemented by node assertion classes which share no base beyond AbstractNodeAssert. Java has no multiple class
// inheritance, so, unlike every other assertion contract in this package, this cannot be an abstract class. It is
// self-bounded, the way assertj-core bounds its own mixins such as org.assertj.core.api.NumberAssert.

/**
 * An interface for node assertions whose actual value has {@code parameterTypes}.
 *
 * @param <SELF> self type parameter
 */
public interface HasParameterTypes<SELF extends HasParameterTypes<SELF>> {

    /**
     * Extracts an assertion for verifying the {@code actual} node's {@code parameterTypes}, using specified assertion
     * factory.
     *
     * @param <ASSERT> assertion type parameter
     * @param factory  the assertion factory.
     * @return the extracted assertion.
     */
    <ASSERT extends AbstractListAssert<?, List<Class<?>>, Class<?>, ? extends AbstractClassAssert<?>>>
    ASSERT extractingParameterTypes(AssertFactory<? super List<Class<?>>, ? extends ASSERT> factory);

    /**
     * Verifies that the {@code actual} node's {@code parameterTypes} satisfies specified consumer, using an assertion
     * created by specified factory.
     * <p>
     * The consumer receives an <em>assertion</em> on the parameter types, carrying this assertion's description and
     * representation, not the value itself, e.g. {@code hasParameterTypesSatisfying(a -> a.isNotNull())}.
     *
     * @param <ASSERT> assertion type parameter
     * @param factory  the assertion factory.
     * @param consumer the consumer accepting the extracted assertion.
     * @return this assertion object.
     */
    @SuppressWarnings({"unchecked"})
    default <ASSERT extends AbstractListAssert<?, List<Class<?>>, Class<?>, ? extends AbstractClassAssert<?>>>
    SELF hasParameterTypesSatisfying(final AssertFactory<? super List<Class<?>>, ? extends ASSERT> factory,
                                     final Consumer<? super ASSERT> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        consumer.accept(extractingParameterTypes(factory));
        return (SELF) this;
    }

    /**
     * Extracts an assertion for verifying the {@code actual} value's {@code parameterTypes}.
     *
     * @return the extracted assertion.
     */
    @CheckReturnValue
    default AbstractListAssert<?, List<Class<?>>, Class<?>, ? extends AbstractClassAssert<?>> extractingParameterTypes() {
        return extractingParameterTypes(
                a -> Assertions.<List<Class<?>>, Class<?>, ClassAssert>assertThat(a, ClassAssert::new)
        );
    }

    /**
     * Verifies that the {@code actual} node's {@code parameterTypes} satisfies specified consumer.
     * <p>
     * The consumer receives an <em>assertion</em> on the parameter types, carrying this assertion's description and
     * representation, not the value itself, e.g. {@code hasParameterTypesSatisfying(a -> a.isNotNull())}.
     *
     * @param consumer the consumer accepting the extracted assertion; must be not {@code null}.
     * @return this assertion object.
     */
    @SuppressWarnings({
            "unchecked"
    })
    default SELF hasParameterTypesSatisfying(
            final Consumer<? super AbstractListAssert<?, List<Class<?>>, Class<?>,
                    ? extends AbstractClassAssert<?>>> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        consumer.accept(extractingParameterTypes());
        return (SELF) this;
    }

    /**
     * Verifies that the {@code actual} node's {@code parameterTypes} are exactly specified types, in order.
     *
     * @param expectedParameterTypes the expected parameter types, in declaration order.
     * @return this assertion object.
     * @see Path.MethodNode#getParameterTypes()
     */
    @SuppressWarnings({
            "unchecked"
    })
    default SELF hasParameterTypes(final Class<?>... expectedParameterTypes) {
        extractingParameterTypes().containsExactly(expectedParameterTypes);
        return (SELF) this;
    }
}
