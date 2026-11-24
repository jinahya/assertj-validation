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

import jakarta.validation.ElementKind;
import jakarta.validation.Path;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractComparableAssert;
import org.assertj.core.api.AbstractIntegerAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.AssertFactory;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.assertj.core.api.ObjectAssert;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * An abstract class for verifying {@link Path.Node} values.
 *
 * @param <SELF>   self type parameter
 * @param <ACTUAL> type of {@link Path.Node}
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public abstract class AbstractNodeAssert<
        SELF extends AbstractNodeAssert<SELF, ACTUAL>, ACTUAL extends Path.Node>
        extends AbstractAssert<SELF, ACTUAL> {

    /**
     * Creates a new instance with specified arguments.
     *
     * @param actual   the actual value to verify.
     * @param selfType a class of {@code SELF}.
     */
    protected AbstractNodeAssert(final ACTUAL actual, final Class<?> selfType) {
        super(actual, selfType);
    }

    // ------------------------------------------------------------------------------------------------------- index

    /**
     * Extracts an assertion for verifying the {@code actual} value's {@code index}.
     *
     * @param <ASSERT> assertion type parameter
     * @param factory  the assertion factory.
     * @return the extracted assertion.
     */
    @CheckReturnValue
    public <ASSERT extends AbstractIntegerAssert<? extends ASSERT>> ASSERT extractingIndex(
            final AssertFactory<? super Integer, ? extends ASSERT> factory) {
        return isNotNull()
                .extracting(Path.Node::getIndex, factory);
    }

    /**
     * Verifies that the {@code actual} value's {@code index} satisfies specified consumer.
     * <p>
     * The consumer receives an <em>assertion</em> on the index, carrying this assertion's description and
     * representation, not the value itself, e.g. {@code hasIndexSatisfying(a -> a.isNotNull())}.
     *
     * @param <ASSERT> assertion type parameter
     * @param factory  the assertion factory.
     * @param consumer the consumer accepting the extracted assertion.
     * @return this assertion object.
     */
    public <ASSERT extends AbstractIntegerAssert<? extends ASSERT>> SELF hasIndexSatisfying(
            final AssertFactory<? super Integer, ? extends ASSERT> factory,
            final Consumer<? super ASSERT> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        consumer.accept(extractingIndex(factory));
        return myself;
    }

    /**
     * Returns an assert for verifying {@link Path.Node#getIndex() actual.index} value.
     *
     * @return an assert for verifying {@link Path.Node#getIndex() actual.index} value.
     * @see #extractingIndex(AssertFactory)
     */
    @CheckReturnValue
    public AbstractIntegerAssert<?> extractingIndex() {
        return extractingIndex(InstanceOfAssertFactories.INTEGER);
    }

    /**
     * Verifies that {@link Path.Node#getIndex() actual.index} value satisfies according to specified consumer.
     * <p>
     * The consumer receives an <em>assertion</em> on the index, carrying this assertion's description and
     * representation, not the value itself, e.g. {@code hasIndexSatisfying(a -> a.isNotNull())}.
     *
     * @param consumer the consumer verifies the {@link Path.Node#getIndex() actual.index} value.
     * @return this assertion object
     * @see #extractingIndex()
     */
    public SELF hasIndexSatisfying(final Consumer<? super AbstractIntegerAssert<?>> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        consumer.accept(extractingIndex());
        return myself;
    }

    /**
     * Verifies that {@link Path.Node#getIndex() actual.index} is equal to specified value.
     *
     * @param expectedIndex expected value of {@link Path.Node#getIndex() actual.index}.
     * @return this assertion object.
     * @see #hasIndexSatisfying(Consumer)
     */
    public SELF hasIndex(final Integer expectedIndex) {
        return hasIndexSatisfying(a -> a.isEqualTo(expectedIndex));
    }

    /**
     * Verifies that the {@code actual} value has no {@code index}.
     *
     * @return this assertion object.
     */
    public SELF doesNotHaveIndex() {
        return hasIndexSatisfying(AbstractAssert::isNull);
    }

    // --------------------------------------------------------------------------------------------------------- key

    /**
     * Returns a new assertion for verifying {@link Path.Node#getKey()} actual.key} value.
     *
     * @param factory  an assertion factory.
     * @param <KEY>    key type parameter
     * @param <ASSERT> assertion type parameter
     * @return an assert for verifying {@link Path.Node#getKey() actual.key} value.
     * @see #extractingKey()
     */
    @CheckReturnValue
    public <KEY, ASSERT extends AbstractObjectAssert<ASSERT, ? extends KEY>>
    ASSERT extractingKey(final AssertFactory<? super KEY, ? extends ASSERT> factory) {
        return extractingKey(
                a -> {
                    @SuppressWarnings({"unchecked"})
                    final KEY key = (KEY) a.getKey();
                    return key;
                },
                factory
        );
    }

    /**
     * Returns an assert for verifying {@link Path.Node#getKey()} actual.key} value.
     *
     * @param extractor a function for extracting {@link Path.Node#getKey() actual.key} value.
     * @param factory   an assertion factory.
     * @param <KEY>     key type parameter
     * @param <ASSERT>  assertion type parameter
     * @return an assert for verifying {@link Path.Node#getKey() actual.key} value.
     * @see #extractingKey()
     */
    @CheckReturnValue
    public <KEY, ASSERT extends AbstractObjectAssert<ASSERT, ? extends KEY>> ASSERT extractingKey(
            final Function<? super Path.Node, ? extends KEY> extractor,
            final AssertFactory<? super KEY, ? extends ASSERT> factory) {
        Objects.requireNonNull(extractor, "extractor is null");
        return isNotNull()
                .extracting(extractor, factory);
    }

    /**
     * Verifies that the {@code actual} value's {@code key} satisfies specified consumer.
     * <p>
     * The consumer receives an <em>assertion</em> on the key, carrying this assertion's description and representation,
     * not the value itself, e.g. {@code hasKeySatisfying(a -> a.isNotNull())}.
     *
     * @param <KEY>     key type parameter
     * @param <ASSERT>  assertion type parameter
     * @param extractor the extractor function.
     * @param factory   the assertion factory.
     * @param consumer  the consumer accepting the extracted assertion.
     * @return this assertion object.
     */
    public <KEY, ASSERT extends AbstractObjectAssert<ASSERT, ? extends KEY>> SELF hasKeySatisfying(
            final Function<? super Path.Node, ? extends KEY> extractor,
            final AssertFactory<? super KEY, ? extends ASSERT> factory,
            final Consumer<? super ASSERT> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        consumer.accept(extractingKey(extractor, factory));
        return myself;
    }

    /**
     * Returns an assert for verifying {@link Path.Node#getKey()} actual.key} value.
     *
     * @return an assert for verifying {@link Path.Node#getKey() actual.key} value.
     * @see #extractingKey()
     */
    @CheckReturnValue
    public AbstractObjectAssert<?, Object> extractingKey() {
        return extractingKey(Path.Node::getKey, ObjectAssert::new);
    }

    /**
     * Verifies that the {@code actual} value's {@code key} satisfies specified consumer.
     * <p>
     * The consumer receives an <em>assertion</em> on the key, carrying this assertion's description and representation,
     * not the value itself, e.g. {@code hasKeySatisfying(a -> a.isNotNull())}.
     *
     * @param consumer the consumer accepting the extracted assertion.
     * @return this assertion object.
     */
    public SELF hasKeySatisfying(final Consumer<? super AbstractObjectAssert<?, Object>> consumer) {
        consumer.accept(extractingKey());
        return myself;
    }

    /**
     * Verifies that {@link Path.Node#getKey() actual.key} is {@link #isEqualTo(Object) equal} to specified value.
     *
     * @param expectedKey expected value of {@link Path.Node#getKey() actual.key}.
     * @return this assertion object.
     * @see #hasKeySatisfying(Consumer)
     */
    public SELF hasKey(final Object expectedKey) {
        return hasKeySatisfying(a -> a.isEqualTo(expectedKey));
    }

    /**
     * Verifies that {@link Path.Node#getKey() actual.key} is {@code null}.
     *
     * @return this assertion object.
     * @implSpec This method invokes the {@link #hasKey(Object)} method with {@code null}, and returns the
     *         result.
     * @see #hasKey(Object)
     */
    public SELF doesNotHaveKey() {
        return hasKeySatisfying(AbstractAssert::isNull);
    }

    // -------------------------------------------------------------------------------------------------------- kind

    /**
     * Extracts an assertion for verifying the {@code actual} value's {@code kind}.
     *
     * @param <ASSERT> assertion type parameter
     * @param factory  the assertion factory.
     * @return the extracted assertion.
     */
    @CheckReturnValue
    public <ASSERT extends AbstractComparableAssert<?, ElementKind>> ASSERT extractingKind(
            final AssertFactory<? super ElementKind, ? extends ASSERT> factory) {
        return isNotNull()
                .extracting(Path.Node::getKind, factory);
    }

    /**
     * Verifies that the {@code actual} value's {@code kind} satisfies specified consumer.
     * <p>
     * The consumer receives an <em>assertion</em> on the kind, carrying this assertion's description and
     * representation, not the value itself, e.g. {@code hasKindSatisfying(a -> a.isNotNull())}.
     *
     * @param <ASSERT> assertion type parameter
     * @param factory  the assertion factory.
     * @param consumer the consumer accepting the extracted assertion.
     * @return this assertion object.
     */
    public <ASSERT extends AbstractComparableAssert<?, ElementKind>> SELF hasKindSatisfying(
            final AssertFactory<? super ElementKind, ? extends ASSERT> factory,
            final Consumer<? super ASSERT> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        consumer.accept(extractingKind(factory));
        return myself;
    }

    /**
     * Returns an assert for verifying {@link Path.Node#getKind() actual.kind} value.
     *
     * @return an assert for verifying {@link Path.Node#getKind() actual.kind} value.
     */
    @CheckReturnValue
    public AbstractComparableAssert<?, ElementKind> extractingKind() {
        return extractingKind(InstanceOfAssertFactories.comparable(ElementKind.class));
    }

    /**
     * Verifies that the {@code actual} value's {@code kind} satisfies specified consumer.
     * <p>
     * The consumer receives an <em>assertion</em> on the kind, carrying this assertion's description and
     * representation, not the value itself, e.g. {@code hasKindSatisfying(a -> a.isNotNull())}.
     *
     * @param consumer the consumer accepting the extracted assertion.
     * @return this assertion object.
     */
    public SELF hasKindSatisfying(final Consumer<? super AbstractComparableAssert<?, ElementKind>> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        return hasKindSatisfying(InstanceOfAssertFactories.comparable(ElementKind.class), consumer);
    }

    /**
     * Verifies that {@link Path.Node#getKind() actual.kind} value is {@link #isEqualTo(Object) equal} to specified
     * value.
     *
     * @param expectedKind expected value of {@link Path.Node#getKind() actual.kind}.
     * @return this assertion object.
     */
    public SELF hasKind(final ElementKind expectedKind) {
        return hasKindSatisfying(a -> a.isSameAs(expectedKind));
    }

    // -------------------------------------------------------------------------------------------------------- name

    /**
     * Extracts an assertion for verifying the {@code actual} value's {@code name}.
     *
     * @param <ASSERT> assertion type parameter
     * @param factory  the assertion factory.
     * @return the extracted assertion.
     */
    @CheckReturnValue
    public <ASSERT extends AbstractStringAssert<? extends ASSERT>> ASSERT extractingName(
            final AssertFactory<? super String, ? extends ASSERT> factory) {
        return isNotNull()
                .extracting(Path.Node::getName, factory);
    }

    /**
     * Verifies that the {@code actual} value's {@code name} satisfies specified consumer.
     * <p>
     * The consumer receives an <em>assertion</em> on the name, carrying this assertion's description and
     * representation, not the value itself, e.g. {@code hasNameSatisfying(a -> a.isNotNull())}.
     *
     * @param <ASSERT> assertion type parameter
     * @param factory  the assertion factory.
     * @param consumer the consumer accepting the extracted assertion.
     * @return this assertion object.
     */
    public <ASSERT extends AbstractStringAssert<? extends ASSERT>> SELF hasNameSatisfying(
            final AssertFactory<? super String, ? extends ASSERT> factory,
            final Consumer<? super ASSERT> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        consumer.accept(extractingName(factory));
        return myself;
    }

    /**
     * Returns an assert for verifying {@link Path.Node#getName() actual.name} value.
     *
     * @return an assert for verifying {@link Path.Node#getName() actual.name} value.
     */
    @CheckReturnValue
    public AbstractStringAssert<?> extractingName() {
        return extractingName(InstanceOfAssertFactories.STRING);
    }

    /**
     * Verifies that the {@code actual} value's {@code name} satisfies specified consumer.
     * <p>
     * The consumer receives an <em>assertion</em> on the name, carrying this assertion's description and
     * representation, not the value itself, e.g. {@code hasNameSatisfying(a -> a.isNotNull())}.
     *
     * @param consumer the consumer accepting the extracted assertion.
     * @return this assertion object.
     */
    public SELF hasNameSatisfying(final Consumer<? super AbstractStringAssert<?>> consumer) {
        return hasNameSatisfying(InstanceOfAssertFactories.STRING, consumer);
    }

    /**
     * Verifies that {@link Path.Node#getName() actual.name} value is {@link #isEqualTo(Object) equal} to specified
     * value.
     *
     * @param expectedName expected value of {@link Path.Node#getName() actual.name}.
     * @return this assertion object.
     */
    public SELF hasName(final String expectedName) {
        return hasNameSatisfying(a -> a.isEqualTo(expectedName));
    }

    /**
     * Verifies that {@link Path.Node#getName() actual.name} is {@code null}.
     *
     * @return this assertion object.
     * @apiNote A node's name is {@code null} only for a leaf node representing an entity, in particular the
     *         node representing the root object. Every other kind has a name, including the literals
     *         {@code <cross-parameter>}, {@code <return value>} and {@code <list element>}.
     * @see Path.Node#getName()
     */
    public SELF doesNotHaveName() {
        return hasNameSatisfying(AbstractAssert::isNull);
    }

    // -------------------------------------------------------------------------------------------------- inIterable

    /**
     * Extracts an assertion for verifying the {@code actual} value's {@code inIterable}.
     *
     * @param <ASSERT> assertion type parameter
     * @param factory  the assertion factory.
     * @return the extracted assertion.
     */
    @CheckReturnValue
    public <ASSERT extends AbstractBooleanAssert<? extends ASSERT>> ASSERT extractingInIterable(
            final AssertFactory<? super Boolean, ? extends ASSERT> factory) {
        return isNotNull()
                .extracting(Path.Node::isInIterable, factory);
    }

    /**
     * Verifies that the {@code actual} value's {@code inIterable} satisfies specified consumer.
     * <p>
     * The consumer receives an <em>assertion</em> on the {@code inIterable} flag, carrying this assertion's description
     * and representation, not the value itself, e.g. {@code hasInIterableSatisfying(a -> a.isNotNull())}.
     *
     * @param <ASSERT> assertion type parameter
     * @param factory  the assertion factory.
     * @param consumer the consumer accepting the extracted assertion.
     * @return this assertion object.
     */
    public <ASSERT extends AbstractBooleanAssert<? extends ASSERT>> SELF hasInIterableSatisfying(
            final AssertFactory<? super Boolean, ? extends ASSERT> factory,
            final Consumer<? super ASSERT> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        consumer.accept(extractingInIterable(factory));
        return myself;
    }

    /**
     * Extracts an assertion for verifying the {@code actual} value's {@code inIterable}.
     *
     * @return the extracted assertion.
     */
    @CheckReturnValue
    public AbstractBooleanAssert<?> extractingInIterable() {
        return extractingInIterable(InstanceOfAssertFactories.BOOLEAN);
    }

    /**
     * Verifies that the {@code actual} value's {@code inIterable} satisfies specified consumer.
     * <p>
     * The consumer receives an <em>assertion</em> on the {@code inIterable} flag, carrying this assertion's description
     * and representation, not the value itself, e.g. {@code hasInIterableSatisfying(a -> a.isNotNull())}.
     *
     * @param consumer the consumer accepting the extracted assertion.
     * @return this assertion object.
     */
    public SELF hasInIterableSatisfying(final Consumer<? super AbstractBooleanAssert<?>> consumer) {
        return hasInIterableSatisfying(InstanceOfAssertFactories.BOOLEAN, consumer);
    }

    /**
     * Verifies that the {@code actual} value is in iterable.
     *
     * @return this assertion object.
     */
    public SELF isInIterable() {
        return hasInIterableSatisfying(AbstractBooleanAssert::isTrue);
    }

    /**
     * Verifies that the {@code actual} value is <em>not</em> in iterable.
     *
     * @return this assertion object.
     */
    public SELF isNotInIterable() {
        return hasInIterableSatisfying(AbstractBooleanAssert::isFalse);
    }
}
