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
import org.assertj.core.api.AbstractIntegerAssert;
import org.assertj.core.api.AssertFactory;
import org.assertj.core.api.InstanceOfAssertFactories;

import java.util.Objects;
import java.util.function.Consumer;

// Implemented by node assertion classes which share no base beyond AbstractNodeAssert. Java has no multiple class
// inheritance, so, unlike every other assertion contract in this package, this cannot be an abstract class. It is
// self-bounded, the way assertj-core bounds its own mixins such as org.assertj.core.api.NumberAssert.
/**
 * An interface for node assertions whose actual value has a {@code typeArgumentIndex}.
 *
 * @param <SELF> self type parameter
 */
public interface HasTypeArgumentIndex<SELF extends HasTypeArgumentIndex<SELF>> {

    /**
     * Extracts an assertion for verifying the {@code actual} value's {@code typeArgumentIndex}.
     *
     * @param <ASSERT> assertion type parameter
     * @param factory  the assertion factory.
     * @return the extracted assertion.
     */
    <ASSERT extends AbstractIntegerAssert<? extends ASSERT>> ASSERT extractingTypeArgumentIndex(
            AssertFactory<? super Integer, ? extends ASSERT> factory
    );

    /**
     * Verifies that the {@code actual} node's {@code typeArgumentIndex} satisfies specified consumer, using an
     * assertion created by specified factory.
     * <p>
     * The consumer receives an <em>assertion</em> on the type argument index, carrying this assertion's description and
     * representation, not the value itself, e.g. {@code hasTypeArgumentIndexSatisfying(a -> a.isNotNull())}.
     *
     * @param <ASSERT> assertion type parameter
     * @param factory  the assertion factory.
     * @param consumer the consumer accepting the extracted assertion.
     * @return this assertion object.
     */
    @SuppressWarnings({"unchecked"})
    default <ASSERT extends AbstractIntegerAssert<? extends ASSERT>> SELF hasTypeArgumentIndexSatisfying(
            final AssertFactory<? super Integer, ? extends ASSERT> factory,
            final Consumer<? super ASSERT> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        consumer.accept(extractingTypeArgumentIndex(factory));
        return (SELF) this;
    }

    /**
     * Extracts an assertion for verifying the {@code actual} value's {@code typeArgumentIndex}.
     *
     * @return the extracted assertion.
     */
    @CheckReturnValue
    default AbstractIntegerAssert<?> extractingTypeArgumentIndex() {
        return extractingTypeArgumentIndex(InstanceOfAssertFactories.INTEGER);
    }

    /**
     * Verifies that the {@code actual} value's {@code typeArgumentIndex} satisfies specified consumer.
     * <p>
     * The consumer receives an <em>assertion</em> on the type argument index, carrying this assertion's description and
     * representation, not the value itself, e.g. {@code hasTypeArgumentIndexSatisfying(a -> a.isNotNull())}.
     *
     * @param consumer the consumer accepting the extracted assertion.
     * @return this assertion object.
     */
    default SELF hasTypeArgumentIndexSatisfying(final Consumer<? super AbstractIntegerAssert<?>> consumer) {
        return hasTypeArgumentIndexSatisfying(InstanceOfAssertFactories.INTEGER, consumer);
    }

    /**
     * Verifies that the {@code actual} node's {@code typeArgumentIndex} is equal to specified value.
     *
     * @param expectedTypeArgumentIndex the expected value of {@code typeArgumentIndex}.
     * @return this assertion object.
     */
    @SuppressWarnings({
            "unchecked"
    })
    default SELF hasTypeArgumentIndex(final Integer expectedTypeArgumentIndex) {
        ValidationAssertMessages.describedAsAttribute(extractingTypeArgumentIndex(), "typeArgumentIndex").isEqualTo(expectedTypeArgumentIndex);
        return (SELF) this;
    }

    /**
     * Verifies that the {@code actual} value has no {@code typeArgumentIndex}.
     *
     * @return this assertion object.
     */
    default SELF doesNotHaveTypeArgumentIndex() {
        return hasTypeArgumentIndexSatisfying(AbstractAssert::isNull);
    }
}
