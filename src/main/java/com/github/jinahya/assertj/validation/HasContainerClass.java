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
import org.assertj.core.api.AbstractClassAssert;
import org.assertj.core.api.AssertFactory;
import org.assertj.core.api.InstanceOfAssertFactories;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

// Implemented by node assertion classes which share no base beyond AbstractNodeAssert. Java has no multiple class
// inheritance, so, unlike every other assertion contract in this package, this cannot be an abstract class. It is
// self-bounded, the way assertj-core bounds its own mixins such as org.assertj.core.api.NumberAssert.
/**
 * An interface for node assertions whose actual value has a {@code containerClass}.
 *
 * @param <SELF> self type parameter
 */
public interface HasContainerClass<SELF extends HasContainerClass<SELF>> {

    /**
     * Extracts an assertion for verifying the {@code actual} value's {@code containerClass}.
     *
     * @param <ASSERT> assertion type parameter
     * @param factory  the assertion factory.
     * @return the extracted assertion.
     */
    <ASSERT extends AbstractClassAssert<? extends ASSERT>> ASSERT extractingContainerClass(
            AssertFactory<? super Class<?>, ? extends ASSERT> factory
    );

    /**
     * Verifies that the {@code actual} node's {@code containerClass} satisfies specified consumer, using an
     * assertion created by specified factory.
     * <p>
     * The consumer receives an <em>assertion</em> on the container class, carrying this assertion's description and
     * representation, not the value itself, e.g. {@code hasContainerClassSatisfying(a -> a.isNotNull())}.
     *
     * @param <ASSERT> assertion type parameter
     * @param factory  the assertion factory.
     * @param consumer the consumer accepting the extracted assertion.
     * @return this assertion object.
     */
    @SuppressWarnings({"unchecked"})
    default <ASSERT extends AbstractClassAssert<? extends ASSERT>> SELF hasContainerClassSatisfying(
            final AssertFactory<? super Class<?>, ? extends ASSERT> factory,
            final Consumer<? super ASSERT> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        consumer.accept(extractingContainerClass(factory));
        return (SELF) this;
    }

    /**
     * Extracts an assertion for verifying the {@code actual} value's {@code containerClass}.
     *
     * @return the extracted assertion.
     */
    @CheckReturnValue
    default AbstractClassAssert<?> extractingContainerClass() {
        return extractingContainerClass(InstanceOfAssertFactories.CLASS);
    }

    /**
     * Verifies that the {@code actual} value's {@code containerClass} satisfies specified consumer.
     * <p>
     * The consumer receives an <em>assertion</em> on the container class, carrying this assertion's description and
     * representation, not the value itself, e.g. {@code hasContainerClassSatisfying(a -> a.isNotNull())}.
     *
     * @param consumer the consumer accepting the extracted assertion.
     * @return this assertion object.
     */
    default SELF hasContainerClassSatisfying(final Consumer<? super AbstractClassAssert<?>> consumer) {
        return hasContainerClassSatisfying(InstanceOfAssertFactories.CLASS, consumer);
    }

    /**
     * Verifies that the {@code actual} node's {@code containerClass} is equal to specified value.
     *
     * @param expectedContainerClass the expected value of {@code containerClass}.
     * @return this assertion object.
     */
    @SuppressWarnings({
            "unchecked"
    })
    default SELF hasContainerClass(final Class<?> expectedContainerClass) {
        ValidationAssertMessages.describedAsAttribute(extractingContainerClass(), "containerClass").isEqualTo(expectedContainerClass);
        return (SELF) this;
    }

    /**
     * Verifies that the {@code actual} node's {@code containerClass} is {@code null}, that is, the node is not
     * contained in a container type.
     *
     * @return this assertion object.
     * @apiNote The specification defines {@code getContainerClass()} as returning the container's type &quot;if
     * contained in a container type such as {@code Optional}, {@code List} or {@code Map}, {@code null}
     * otherwise&quot;, so {@code null} is a meaningful outcome rather than an exceptional one.
     */
    default SELF doesNotHaveContainerClass() {
        return hasContainerClassSatisfying(AbstractAssert::isNull);
    }
}
