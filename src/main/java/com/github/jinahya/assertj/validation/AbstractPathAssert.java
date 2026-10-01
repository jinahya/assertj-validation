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
import org.assertj.core.api.AbstractClassAssert;
import org.assertj.core.api.AbstractComparableAssert;
import org.assertj.core.api.AbstractIntegerAssert;
import org.assertj.core.api.AbstractListAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.AssertFactory;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ClassAssert;
import org.assertj.core.api.EnumerableAssert;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.assertj.core.api.ListAssert;
import org.assertj.core.api.ObjectAssertFactory;
import org.assertj.core.error.ShouldBeEmpty;
import org.assertj.core.error.ShouldBeNullOrEmpty;
import org.assertj.core.error.ShouldHaveSameSizeAs;
import org.assertj.core.error.ShouldHaveSize;
import org.assertj.core.error.ShouldHaveSizeBetween;
import org.assertj.core.error.ShouldHaveSizeGreaterThan;
import org.assertj.core.error.ShouldHaveSizeGreaterThanOrEqualTo;
import org.assertj.core.error.ShouldHaveSizeLessThan;
import org.assertj.core.error.ShouldHaveSizeLessThanOrEqualTo;
import org.assertj.core.error.ShouldNotBeEmpty;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An abstract class for verifying {@link Path} values.
 * <p>
 * This extends {@link AbstractAssert} and implements {@link EnumerableAssert}, rather than extending
 * {@code AbstractIterableAssert}. A {@link Path} is a sequence of {@link Path.Node}s, but it is a domain type, not a
 * collection: {@code AbstractIterableAssert} requires {@code filteredOn} to return {@code SELF}, which would mean a
 * filtered subset of nodes is itself a {@link Path}, and it is not. Every one of assertj's own
 * {@code AbstractIterableAssert} subclasses has a plain {@code List}, {@code Collection} or {@code Iterable} as its
 * actual type, never a domain type.
 * <p>
 * {@link EnumerableAssert} supplies the size vocabulary &mdash; {@link #hasSize(int)}, {@link #isEmpty()} and friends
 * &mdash; without that obligation, exactly as {@code AbstractCharSequenceAssert} uses it for the characters of a
 * {@code String}. The full collection surface is reached through {@link #nodes()}, where filtering nodes yields nodes
 * and nothing has to be fabricated.
 *
 * @param <SELF> self type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see #nodes()
 */
@SuppressWarnings({
        "java:S119" // <SELF>, <ACTUAL>
})
public abstract class AbstractPathAssert<SELF extends AbstractPathAssert<SELF>>
        extends AbstractAssert<SELF, Path>
        implements EnumerableAssert<SELF, Path.Node> {

    // ------------------------------------------------------------------------------------------------------- mixins
    // The following interfaces are implemented by several node assertion classes which do not share a common base
    // beyond _AbstractNodeAssert. Java has no multiple class inheritance, so, unlike every other assertion contract in
    // this package, they cannot be expressed as abstract classes. They are self-bounded, the way assertj-core bounds
    // its own mixins such as org.assertj.core.api.NumberAssert and org.assertj.core.api.EnumerableAssert.

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
        default AbstractClassAssert<?> extractingContainerClass() {
            return extractingContainerClass(InstanceOfAssertFactories.CLASS);
        }

        /**
         * Verifies that the {@code actual} value's {@code containerClass} satisfies specified consumer.
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
            extractingContainerClass().isEqualTo(expectedContainerClass);
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
        default AbstractIntegerAssert<?> extractingTypeArgumentIndex() {
            return extractingTypeArgumentIndex(InstanceOfAssertFactories.INTEGER);
        }

        /**
         * Verifies that the {@code actual} value's {@code typeArgumentIndex} satisfies specified consumer.
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
            extractingTypeArgumentIndex().isEqualTo(expectedTypeArgumentIndex);
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

    /**
     * An interface for node assertions whose actual value has {@code parameterTypes}.
     *
     * @param <SELF> self type parameter
     */
    public interface HasParameterTypes<SELF extends HasParameterTypes<SELF>> {

        /**
         * Extracts an assertion for verifying the {@code actual} node's {@code parameterTypes}, using specified
         * assertion factory.
         *
         * @param <ASSERT> assertion type parameter
         * @param factory  the assertion factory.
         * @return the extracted assertion.
         */
        <ASSERT extends AbstractListAssert<?, List<Class<?>>, Class<?>, ? extends AbstractClassAssert<?>>>
        ASSERT extractingParameterTypes(AssertFactory<? super List<Class<?>>, ? extends ASSERT> factory);

        /**
         * Verifies that the {@code actual} node's {@code parameterTypes} satisfies specified consumer, using an
         * assertion created by specified factory.
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
        default AbstractListAssert<?, List<Class<?>>, Class<?>, ? extends AbstractClassAssert<?>> extractingParameterTypes() {
            return extractingParameterTypes(
                    a -> Assertions.<List<Class<?>>, Class<?>, ClassAssert>assertThat(a, ClassAssert::new)
            );
        }

        /**
         * Verifies that the {@code actual} node's {@code parameterTypes} satisfies specified consumer.
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

    /**
     * An interface for node assertions whose actual value has a {@code parameterIndex}.
     *
     * @param <SELF> self type parameter
     */
    public interface HasParameterIndex<SELF extends HasParameterIndex<SELF>> {

        /**
         * Extracts an assertion for verifying the {@code actual} value's {@code parameterIndex}.
         *
         * @param <ASSERT> assertion type parameter
         * @param factory  the assertion factory.
         * @return the extracted assertion.
         */
        <ASSERT extends AbstractIntegerAssert<? extends ASSERT>> ASSERT extractingParameterIndex(
                AssertFactory<? super Integer, ? extends ASSERT> factory
        );

        /**
         * Verifies that the {@code actual} node's {@code parameterIndex} satisfies specified consumer, using an
         * assertion created by specified factory.
         *
         * @param <ASSERT> assertion type parameter
         * @param factory  the assertion factory.
         * @param consumer the consumer accepting the extracted assertion.
         * @return this assertion object.
         */
        @SuppressWarnings({"unchecked"})
        default <ASSERT extends AbstractIntegerAssert<? extends ASSERT>> SELF hasParameterIndexSatisfying(
                final AssertFactory<? super Integer, ? extends ASSERT> factory,
                final Consumer<? super ASSERT> consumer) {
            Objects.requireNonNull(consumer, "consumer is null");
            consumer.accept(extractingParameterIndex(factory));
            return (SELF) this;
        }

        /**
         * Extracts an assertion for verifying the {@code actual} value's {@code parameterIndex}.
         *
         * @return the extracted assertion.
         */
        default AbstractIntegerAssert<?> extractingParameterIndex() {
            return extractingParameterIndex(InstanceOfAssertFactories.INTEGER);
        }

        /**
         * Verifies that the {@code actual} value's {@code parameterIndex} satisfies specified consumer.
         *
         * @param consumer the consumer accepting the extracted assertion.
         * @return this assertion object.
         */
        default SELF hasParameterIndexSatisfying(final Consumer<? super AbstractIntegerAssert<?>> consumer) {
            return hasParameterIndexSatisfying(InstanceOfAssertFactories.INTEGER, consumer);
        }

        /**
         * Verifies that the {@code actual} node's {@code parameterIndex} is equal to specified value.
         *
         * @param expectedParameterIndex the expected value of {@code parameterIndex}.
         * @return this assertion object.
         */
        @SuppressWarnings({
                "unchecked"
        })
        default SELF hasParameterIndex(final Integer expectedParameterIndex) {
            extractingParameterIndex().isEqualTo(expectedParameterIndex);
            return (SELF) this;
        }

        /**
         * Verifies that the {@code actual} value has no {@code parameterIndex}.
         *
         * @return this assertion object.
         */
        default SELF doesNotHaveParameterIndex() {
            return hasParameterIndexSatisfying(AbstractAssert::isNull);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * An abstract class for verifying {@link Path.Node} values.
     *
     * @param <SELF>   self type parameter
     * @param <ACTUAL> type of {@link Path.Node}
     * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
     */
    @SuppressWarnings({
            "java:S101" // class _Abstract...
    })
    abstract static class _AbstractNodeAssert<
            SELF extends _AbstractNodeAssert<SELF, ACTUAL>, ACTUAL extends Path.Node>
            extends AbstractAssert<SELF, ACTUAL> {

        protected _AbstractNodeAssert(final ACTUAL actual, final Class<?> selfType) {
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
        public <ASSERT extends AbstractIntegerAssert<? extends ASSERT>> ASSERT extractingIndex(
                final AssertFactory<? super Integer, ? extends ASSERT> factory) {
            return isNotNull()
                    .extracting(Path.Node::getIndex, factory);
        }

        /**
         * Verifies that the {@code actual} value's {@code index} satisfies specified consumer.
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
        public AbstractIntegerAssert<?> extractingIndex() {
            return extractingIndex(InstanceOfAssertFactories.INTEGER);
        }

        /**
         * Verifies that {@link Path.Node#getIndex() actual.index} value satisfies according to specified consumer.
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
        public <KEY, ASSERT extends AbstractObjectAssert<ASSERT, ? extends KEY>> ASSERT extractingKey(
                final Function<? super Path.Node, ? extends KEY> extractor,
                final AssertFactory<? super KEY, ? extends ASSERT> factory) {
            Objects.requireNonNull(extractor, "extractor is null");
            return isNotNull()
                    .extracting(extractor, factory);
        }

        /**
         * Verifies that the {@code actual} value's {@code key} satisfies specified consumer.
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
        public AbstractObjectAssert<?, Object> extractingKey() {
            return extractingKey(Path.Node::getKey, new ObjectAssertFactory<>());
        }

        /**
         * Verifies that the {@code actual} value's {@code key} satisfies specified consumer.
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
         * @implSpec This method invokes the {@link #hasKey(Object)} method with {@code null}, and returns the result.
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
        public <ASSERT extends AbstractComparableAssert<?, ElementKind>> ASSERT extractingKind(
                final AssertFactory<? super ElementKind, ? extends ASSERT> factory) {
            return isNotNull()
                    .extracting(Path.Node::getKind, factory);
        }

        /**
         * Verifies that the {@code actual} value's {@code kind} satisfies specified consumer.
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
        public AbstractComparableAssert<?, ElementKind> extractingKind() {
            return extractingKind(InstanceOfAssertFactories.comparable(ElementKind.class));
        }

        /**
         * Verifies that the {@code actual} value's {@code kind} satisfies specified consumer.
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
        public <ASSERT extends AbstractStringAssert<? extends ASSERT>> ASSERT extractingName(
                final AssertFactory<? super String, ? extends ASSERT> factory) {
            return isNotNull()
                    .extracting(Path.Node::getName, factory);
        }

        /**
         * Verifies that the {@code actual} value's {@code name} satisfies specified consumer.
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
        public AbstractStringAssert<?> extractingName() {
            return extractingName(InstanceOfAssertFactories.STRING);
        }

        /**
         * Verifies that the {@code actual} value's {@code name} satisfies specified consumer.
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
         * @apiNote A node's name is {@code null} only for a leaf node representing an entity, in particular the node
         * representing the root object. Every other kind has a name, including the literals {@code <cross-parameter>},
         * {@code <return value>} and {@code <list element>}.
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
        public <ASSERT extends AbstractBooleanAssert<? extends ASSERT>> ASSERT extractingInIterable(
                final AssertFactory<? super Boolean, ? extends ASSERT> factory) {
            return isNotNull()
                    .extracting(Path.Node::isInIterable, factory);
        }

        /**
         * Verifies that the {@code actual} value's {@code inIterable} satisfies specified consumer.
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
        public AbstractBooleanAssert<?> extractingInIterable() {
            return extractingInIterable(InstanceOfAssertFactories.BOOLEAN);
        }

        /**
         * Verifies that the {@code actual} value's {@code inIterable} satisfies specified consumer.
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

    /**
     * An abstract assertion class for verifying {@link Path.Node} values.
     *
     * @param <SELF> self type parameter
     */
    public abstract static class AbstractNodeAssert<SELF extends AbstractNodeAssert<SELF>>
            extends _AbstractNodeAssert<SELF, Path.Node> {

        /**
         * Creates a new instance with specified arguments.
         *
         * @param actual   the actual value to verify.
         * @param selfType a class of {@code SELF}.
         */
        protected AbstractNodeAssert(final Path.Node actual, final Class<?> selfType) {
            super(actual, selfType);
        }
    }

    // -------------------------------------------------------------------------------------------------------- BeanNode

    /**
     * An abstract assertion class for verifying {@link Path.BeanNode} values.
     *
     * @param <SELF> self type parameter
     */
    public abstract static class AbstractBeanNodeAssert<SELF extends AbstractBeanNodeAssert<SELF>>
            extends _AbstractNodeAssert<SELF, Path.BeanNode>
            implements HasContainerClass<SELF>, HasTypeArgumentIndex<SELF> {

        /**
         * Creates a new instance with specified arguments.
         *
         * @param actual   the actual value to verify.
         * @param selfType a class of {@code SELF}.
         */
        protected AbstractBeanNodeAssert(final Path.BeanNode actual, final Class<?> selfType) {
            super(actual, selfType);
        }

        @Override
        public <ASSERT extends AbstractClassAssert<? extends ASSERT>> ASSERT extractingContainerClass(
                final AssertFactory<? super Class<?>, ? extends ASSERT> factory) {
            return isNotNull()
                    .extracting(Path.BeanNode::getContainerClass, factory);
        }

        @Override
        public <ASSERT extends AbstractIntegerAssert<? extends ASSERT>> ASSERT extractingTypeArgumentIndex(
                final AssertFactory<? super Integer, ? extends ASSERT> factory) {
            return isNotNull()
                    .extracting(Path.BeanNode::getTypeArgumentIndex, factory);
        }
    }

    // ------------------------------------------------------------------------------------------------- ConstructorNode

    /**
     * An abstract assertion class for verifying {@link Path.ConstructorNode} values.
     *
     * @param <SELF> self type parameter
     */
    public abstract static class AbstractConstructorNodeAssert<SELF extends AbstractConstructorNodeAssert<SELF>>
            extends _AbstractNodeAssert<SELF, Path.ConstructorNode>
            implements HasParameterTypes<SELF> {

        /**
         * Creates a new instance with specified arguments.
         *
         * @param actual   the actual value to verify.
         * @param selfType a class of {@code SELF}.
         */
        protected AbstractConstructorNodeAssert(final Path.ConstructorNode actual, final Class<?> selfType) {
            super(actual, selfType);
        }

        @Override
        public <ASSERT extends AbstractListAssert<?, List<Class<?>>, Class<?>, ? extends AbstractClassAssert<?>>>
        ASSERT extractingParameterTypes(final AssertFactory<? super List<Class<?>>, ? extends ASSERT> factory) {
            return isNotNull()
                    .extracting(Path.ConstructorNode::getParameterTypes, factory);
        }
    }

    // -------------------------------------------------------------------------------------------- ContainerElementNode

    /**
     * An abstract assertion class for verifying {@link Path.ContainerElementNode} values.
     *
     * @param <SELF> self type parameter
     */
    public abstract static class AbstractContainerElementNodeAssert<SELF extends AbstractContainerElementNodeAssert<SELF>>
            extends _AbstractNodeAssert<SELF, Path.ContainerElementNode>
            implements HasContainerClass<SELF>, HasTypeArgumentIndex<SELF> {

        /**
         * Creates a new instance with specified arguments.
         *
         * @param actual   the actual value to verify.
         * @param selfType a class of {@code SELF}.
         */
        protected AbstractContainerElementNodeAssert(final Path.ContainerElementNode actual, final Class<?> selfType) {
            super(actual, selfType);
        }

        @Override
        public <ASSERT extends AbstractClassAssert<? extends ASSERT>> ASSERT extractingContainerClass(
                final AssertFactory<? super Class<?>, ? extends ASSERT> factory) {
            return isNotNull()
                    .extracting(Path.ContainerElementNode::getContainerClass, factory);
        }

        @Override
        public <ASSERT extends AbstractIntegerAssert<? extends ASSERT>> ASSERT extractingTypeArgumentIndex(
                final AssertFactory<? super Integer, ? extends ASSERT> factory) {
            return isNotNull()
                    .extracting(Path.ContainerElementNode::getTypeArgumentIndex, factory);
        }
    }

    // ---------------------------------------------------------------------------------------------- CrossParameterNode

    /**
     * An abstract assertion class for verifying {@link Path.CrossParameterNode} values.
     *
     * @param <SELF> self type parameter
     */
    public abstract static class AbstractCrossParameterNodeAssert<
            SELF extends AbstractCrossParameterNodeAssert<SELF>>
            extends _AbstractNodeAssert<SELF, Path.CrossParameterNode> {

        /**
         * Creates a new instance with specified arguments.
         *
         * @param actual   the actual value to verify.
         * @param selfType a class of {@code SELF}.
         */
        protected AbstractCrossParameterNodeAssert(final Path.CrossParameterNode actual, final Class<?> selfType) {
            super(actual, selfType);
        }
    }

    // ------------------------------------------------------------------------------------------------------ MethodNode

    /**
     * An abstract assertion class for verifying {@link Path.MethodNode} values.
     *
     * @param <SELF> self type parameter
     */
    public abstract static class AbstractMethodNodeAssert<SELF extends AbstractMethodNodeAssert<SELF>>
            extends _AbstractNodeAssert<SELF, Path.MethodNode>
            implements HasParameterTypes<SELF> {

        /**
         * Creates a new instance with specified arguments.
         *
         * @param actual   the actual value to verify.
         * @param selfType a class of {@code SELF}.
         */
        protected AbstractMethodNodeAssert(final Path.MethodNode actual, final Class<?> selfType) {
            super(actual, selfType);
        }

        @Override
        public <ASSERT extends AbstractListAssert<?, List<Class<?>>, Class<?>, ? extends AbstractClassAssert<?>>>
        ASSERT extractingParameterTypes(final AssertFactory<? super List<Class<?>>, ? extends ASSERT> factory) {
            return isNotNull()
                    .extracting(Path.MethodNode::getParameterTypes, factory);
        }
    }

    // --------------------------------------------------------------------------------------------------- ParameterNode

    /**
     * An abstract assertion class for verifying {@link Path.ParameterNode} values.
     *
     * @param <SELF> self type parameter
     */
    public abstract static class AbstractParameterNodeAssert<SELF extends AbstractParameterNodeAssert<SELF>>
            extends _AbstractNodeAssert<SELF, Path.ParameterNode>
            implements HasParameterIndex<SELF> {

        /**
         * Creates a new instance with specified arguments.
         *
         * @param actual   the actual value to verify.
         * @param selfType a class of {@code SELF}.
         */
        protected AbstractParameterNodeAssert(final Path.ParameterNode actual, final Class<?> selfType) {
            super(actual, selfType);
        }

        @Override
        public <ASSERT extends AbstractIntegerAssert<? extends ASSERT>> ASSERT extractingParameterIndex(
                final AssertFactory<? super Integer, ? extends ASSERT> factory) {
            return isNotNull()
                    .extracting(Path.ParameterNode::getParameterIndex, factory);
        }
    }

    // ---------------------------------------------------------------------------------------------------- PropertyNode

    /**
     * An abstract assertion class for verifying {@link Path.PropertyNode} values.
     *
     * @param <SELF> self type parameter
     */
    public abstract static class AbstractPropertyNodeAssert<SELF extends AbstractPropertyNodeAssert<SELF>>
            extends _AbstractNodeAssert<SELF, Path.PropertyNode>
            implements HasContainerClass<SELF>, HasTypeArgumentIndex<SELF> {

        /**
         * Creates a new instance with specified arguments.
         *
         * @param actual   the actual value to verify.
         * @param selfType a class of {@code SELF}.
         */
        protected AbstractPropertyNodeAssert(final Path.PropertyNode actual, final Class<?> selfType) {
            super(actual, selfType);
        }

        @Override
        public <ASSERT extends AbstractClassAssert<? extends ASSERT>> ASSERT extractingContainerClass(
                final AssertFactory<? super Class<?>, ? extends ASSERT> factory) {
            return isNotNull()
                    .extracting(Path.PropertyNode::getContainerClass, factory);
        }

        @Override
        public <ASSERT extends AbstractIntegerAssert<? extends ASSERT>> ASSERT extractingTypeArgumentIndex(
                final AssertFactory<? super Integer, ? extends ASSERT> factory) {
            return isNotNull()
                    .extracting(Path.PropertyNode::getTypeArgumentIndex, factory);
        }
    }

    // ------------------------------------------------------------------------------------------------- ReturnValueNode

    /**
     * An abstract assertion class for verifying {@link Path.ReturnValueNode} values.
     *
     * @param <SELF> self type parameter
     */
    public abstract static class AbstractReturnValueNodeAssert<SELF extends AbstractReturnValueNodeAssert<SELF>>
            extends _AbstractNodeAssert<SELF, Path.ReturnValueNode> {

        /**
         * Creates a new instance with specified arguments.
         *
         * @param actual   the actual value to verify.
         * @param selfType a class of {@code SELF}.
         */
        protected AbstractReturnValueNodeAssert(final Path.ReturnValueNode actual, final Class<?> selfType) {
            super(actual, selfType);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the node at specified index of specified iterable of nodes.
     *
     * @param iterable the iterable of nodes; must be not {@code null}.
     * @param index    the index of the node.
     * @return the extracted assertion.
     */
    protected static Path.Node nodeAt(final Iterable<? extends Path.Node> iterable, final int index) {
        Objects.requireNonNull(iterable, "iterable is null");
        if (index < 0) {
            throw new IllegalArgumentException("negative index: " + index);
        }
        final Iterator<? extends Path.Node> iterator = iterable.iterator();
        Path.Node node = iterator.next(); // NoSuchElementException
        for (int i = 0; i < index; i++) {
            node = iterator.next(); // NoSuchElementException
        }
        return node;
    }

    /**
     * Returns the node at specified index of specified iterable of nodes.
     *
     * @param <N>      node type parameter
     * @param iterable the iterable of nodes; must be not {@code null}.
     * @param index    the index of the node.
     * @param nodeType the type of the node.
     * @return the extracted assertion.
     */
    protected static <N extends Path.Node> N nodeAt(final Iterable<? extends Path.Node> iterable, final int index,
                                                    final Class<N> nodeType) {
        final Path.Node node = nodeAt(iterable, index);
        if (nodeType == Path.Node.class) {
            @SuppressWarnings({"unchecked"})
            final N casted = (N) node;
            return casted;
        }
        requireKindOf(node, nodeType, () -> String.format("the node at index %1$d", index));
        return node.as(nodeType);
    }

    /**
     * Verifies that specified node is of the {@link ElementKind} that specified node type represents, failing as an
     * assertion rather than letting {@link Path.Node#as(Class)} throw.
     * <p>
     * {@link Path.Node#as(Class)} is specified to throw a {@link ClassCastException}, and says the kind &quot;should be
     * checked before by calling {@link Path.Node#getKind()}&quot;. {@link Class#isInstance} is no substitute: Hibernate
     * Validator's node implements <em>every</em> {@code Path.Node} subtype at once, so an {@code instanceof} test
     * accepts any node regardless of its kind. {@link Path.Node#getKind()} is the only reliable discriminator.
     *
     * @param node        the node to check.
     * @param nodeType    the node type it is about to be narrowed to.
     * @param description supplies a description of where the node came from.
     */
    private static void requireKindOf(final Path.Node node, final Class<? extends Path.Node> nodeType,
                                      final Supplier<String> description) {
        final ElementKind expected = KIND_BY_NODE_TYPE.get(nodeType);
        if (expected == null || node.getKind() == expected) {
            return;
        }
        throw new AssertionError(String.format(
                "%nExpecting %1$s%n"
                + "  <%2$s>%n"
                + "to be of kind%n"
                + "  <%3$s>%n"
                + "so that it can be narrowed to <%4$s>, but its kind is%n"
                + "  <%5$s>",
                description.get(), node, expected, nodeType.getSimpleName(), node.getKind()));
    }

    /**
     * The {@link ElementKind} each {@link Path.Node} subtype represents.
     */
    private static final Map<Class<? extends Path.Node>, ElementKind> KIND_BY_NODE_TYPE = Map.of(
            Path.BeanNode.class, ElementKind.BEAN,
            Path.PropertyNode.class, ElementKind.PROPERTY,
            Path.MethodNode.class, ElementKind.METHOD,
            Path.ConstructorNode.class, ElementKind.CONSTRUCTOR,
            Path.ParameterNode.class, ElementKind.PARAMETER,
            Path.CrossParameterNode.class, ElementKind.CROSS_PARAMETER,
            Path.ReturnValueNode.class, ElementKind.RETURN_VALUE,
            Path.ContainerElementNode.class, ElementKind.CONTAINER_ELEMENT
    );

    /**
     * Verifies that specified node is of the kind specified node type represents, for callers outside the positional
     * navigation &mdash; the assertion factories in {@link ValidationInstanceOfAssertFactories}, which cannot rely on
     * {@code instanceof}.
     *
     * @param node     the node to check.
     * @param nodeType the node type it is about to be narrowed to.
     * @param <N>      node type parameter
     * @return {@code node}, narrowed.
     */
    static <N extends Path.Node> N requireKind(final Path.Node node, final Class<N> nodeType) {
        requireKindOf(node, nodeType, () -> "the node");
        return node.as(nodeType);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance with specified arguments.
     *
     * @param actual   the actual value to verify.
     * @param selfType a class of {@code SELF}.
     */
    protected AbstractPathAssert(final Path actual, final Class<?> selfType) {
        super(actual, selfType);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Extracts an assertion for verifying the {@code actual} value's {@code node}.
     *
     * @param <A>      assertion type parameter
     * @param <N>      node type parameter
     * @param index    the index of the node.
     * @param nodeType the type of the node.
     * @param factory  the assertion factory.
     * @return the extracted assertion.
     */
    public <A extends AbstractAssert<?, ? extends N>, N extends Path.Node> A extractingNode(
            final int index, final Class<N> nodeType, final AssertFactory<? super N, ? extends A> factory) {
        return isNotNull()
                .extracting(a -> nodeAt(a, index, nodeType), factory);
    }

    /**
     * Verifies that the {@code actual} value's {@code node} satisfies specified consumer.
     *
     * @param <A>      assertion type parameter
     * @param <N>      node type parameter
     * @param index    the index of the node.
     * @param nodeType the type of the node.
     * @param factory  the assertion factory.
     * @param consumer the consumer accepting the extracted assertion.
     * @return this assertion object.
     */
    public <A extends AbstractAssert<?, ? extends N>, N extends Path.Node> SELF hasNodeSatisfying(
            final int index, final Class<N> nodeType, final AssertFactory<? super N, ? extends A> factory,
            final Consumer<? super A> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        consumer.accept(extractingNode(index, nodeType, factory));
        return myself;
    }

    /**
     * Extracts an assertion for verifying the {@code actual} value's {@code node}.
     *
     * @param <A>     assertion type parameter
     * @param index   the index of the node.
     * @param factory the assertion factory.
     * @return the extracted assertion.
     */
    public <A extends AbstractNodeAssert<? extends A>> A extractingNode(
            final int index, final AssertFactory<? super Path.Node, ? extends A> factory) {
        return extractingNode(index, Path.Node.class, factory);
    }

    /**
     * Extracts an assertion for verifying the {@code actual} value's {@code node}.
     *
     * @param index the index of the node.
     * @return the extracted assertion.
     */
    public AbstractNodeAssert<?> extractingNode(final int index) {
        return extractingNode(index, DefaultPathAssert.DefaultNodeAssert::new);
    }

    /**
     * Verifies that the {@code actual} value's {@code node} satisfies specified consumer.
     *
     * @param index    the index of the node.
     * @param consumer the consumer accepting the extracted assertion.
     * @return this assertion object.
     */
    public SELF hasNodeSatisfying(final int index, final Consumer<? super AbstractNodeAssert<?>> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        consumer.accept(extractingNode(index));
        return myself;
    }

    /**
     * Extracts an assertion for verifying the {@code actual} value's {@code beanNode}.
     *
     * @param <A>     assertion type parameter
     * @param index   the index of the node.
     * @param factory the assertion factory.
     * @return the extracted assertion.
     */
    public <A extends AbstractBeanNodeAssert<? extends A>> A extractingBeanNode(
            final int index, final AssertFactory<? super Path.BeanNode, ? extends A> factory) {
        return extractingNode(index, Path.BeanNode.class, factory);
    }

    /**
     * Extracts an assertion for verifying the {@code actual} value's {@code beanNode}.
     *
     * @param index the index of the node.
     * @return the extracted assertion.
     */
    public AbstractBeanNodeAssert<?> extractingBeanNode(final int index) {
        return extractingBeanNode(index, DefaultPathAssert.DefaultNodeAssert.DefaultBeanNodeAssert::new);
    }

    /**
     * Extracts an assertion for verifying the {@code actual} value's {@code propertyNode}.
     *
     * @param <A>     assertion type parameter
     * @param index   the index of the node.
     * @param factory the assertion factory.
     * @return the extracted assertion.
     */
    public <A extends AbstractPropertyNodeAssert<? extends A>> A extractingPropertyNode(
            final int index, final AssertFactory<? super Path.PropertyNode, ? extends A> factory) {
        return extractingNode(index, Path.PropertyNode.class, factory);
    }

    /**
     * Extracts an assertion for verifying the {@code actual} value's {@code propertyNode}.
     *
     * @param index the index of the node.
     * @return the extracted assertion.
     */
    public AbstractPropertyNodeAssert<?> extractingPropertyNode(final int index) {
        return extractingPropertyNode(index, DefaultPathAssert.DefaultNodeAssert.DefaultPropertyNodeAssert::new);
    }

    /**
     * Verifies that the {@code actual} value's {@code propertyNode} satisfies specified consumer.
     *
     * @param index    the index of the node.
     * @param consumer the consumer accepting the extracted assertion.
     * @return this assertion object.
     */
    public SELF hasPropertyNodeSatisfying(final int index,
                                          final Consumer<? super AbstractPropertyNodeAssert<?>> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        consumer.accept(extractingPropertyNode(index));
        return myself;
    }

    /**
     * Extracts an assertion for verifying the {@link Path.ConstructorNode node} at specified index, using specified
     * assertion factory.
     *
     * @param index   the index of the node.
     * @param factory the assertion factory.
     * @param <A>     assertion type parameter
     * @return an instance of {@link A}.
     */
    public <A extends AbstractConstructorNodeAssert<? extends A>> A extractingConstructorNode(
            final int index, final AssertFactory<? super Path.ConstructorNode, ? extends A> factory) {
        return extractingNode(index, Path.ConstructorNode.class, factory);
    }

    /**
     * Extracts an assertion for verifying the {@link Path.ConstructorNode node} at specified index.
     *
     * @param index the index of the node.
     * @return an assertion for the node at {@code index}.
     */
    public AbstractConstructorNodeAssert<?> extractingConstructorNode(final int index) {
        return extractingConstructorNode(index, DefaultPathAssert.DefaultNodeAssert.DefaultConstructorNodeAssert::new);
    }

    /**
     * Verifies that the {@link Path.ConstructorNode node} at specified index satisfies specified consumer.
     *
     * @param index    the index of the node.
     * @param consumer the consumer verifying the node.
     * @return this assertion object.
     */
    public SELF hasConstructorNodeSatisfying(final int index, final Consumer<? super AbstractConstructorNodeAssert<?>> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        consumer.accept(extractingConstructorNode(index));
        return myself;
    }

    /**
     * Extracts an assertion for verifying the {@link Path.ContainerElementNode node} at specified index, using
     * specified assertion factory.
     *
     * @param index   the index of the node.
     * @param factory the assertion factory.
     * @param <A>     assertion type parameter
     * @return an instance of {@link A}.
     */
    public <A extends AbstractContainerElementNodeAssert<? extends A>> A extractingContainerElementNode(
            final int index, final AssertFactory<? super Path.ContainerElementNode, ? extends A> factory) {
        return extractingNode(index, Path.ContainerElementNode.class, factory);
    }

    /**
     * Extracts an assertion for verifying the {@link Path.ContainerElementNode node} at specified index.
     *
     * @param index the index of the node.
     * @return an assertion for the node at {@code index}.
     */
    public AbstractContainerElementNodeAssert<?> extractingContainerElementNode(final int index) {
        return extractingContainerElementNode(index, DefaultPathAssert.DefaultNodeAssert.DefaultContainerElementNodeAssert::new);
    }

    /**
     * Verifies that the {@link Path.ContainerElementNode node} at specified index satisfies specified consumer.
     *
     * @param index    the index of the node.
     * @param consumer the consumer verifying the node.
     * @return this assertion object.
     */
    public SELF hasContainerElementNodeSatisfying(final int index, final Consumer<? super AbstractContainerElementNodeAssert<?>> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        consumer.accept(extractingContainerElementNode(index));
        return myself;
    }

    /**
     * Extracts an assertion for verifying the {@link Path.CrossParameterNode node} at specified index, using specified
     * assertion factory.
     *
     * @param index   the index of the node.
     * @param factory the assertion factory.
     * @param <A>     assertion type parameter
     * @return an instance of {@link A}.
     */
    public <A extends AbstractCrossParameterNodeAssert<? extends A>> A extractingCrossParameterNode(
            final int index, final AssertFactory<? super Path.CrossParameterNode, ? extends A> factory) {
        return extractingNode(index, Path.CrossParameterNode.class, factory);
    }

    /**
     * Extracts an assertion for verifying the {@link Path.CrossParameterNode node} at specified index.
     *
     * @param index the index of the node.
     * @return an assertion for the node at {@code index}.
     */
    public AbstractCrossParameterNodeAssert<?> extractingCrossParameterNode(final int index) {
        return extractingCrossParameterNode(index, DefaultPathAssert.DefaultNodeAssert.DefaultCrossParameterNodeAssert::new);
    }

    /**
     * Verifies that the {@link Path.CrossParameterNode node} at specified index satisfies specified consumer.
     *
     * @param index    the index of the node.
     * @param consumer the consumer verifying the node.
     * @return this assertion object.
     */
    public SELF hasCrossParameterNodeSatisfying(final int index, final Consumer<? super AbstractCrossParameterNodeAssert<?>> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        consumer.accept(extractingCrossParameterNode(index));
        return myself;
    }

    /**
     * Extracts an assertion for verifying the {@link Path.MethodNode node} at specified index, using specified
     * assertion factory.
     *
     * @param index   the index of the node.
     * @param factory the assertion factory.
     * @param <A>     assertion type parameter
     * @return an instance of {@link A}.
     */
    public <A extends AbstractMethodNodeAssert<? extends A>> A extractingMethodNode(
            final int index, final AssertFactory<? super Path.MethodNode, ? extends A> factory) {
        return extractingNode(index, Path.MethodNode.class, factory);
    }

    /**
     * Extracts an assertion for verifying the {@link Path.MethodNode node} at specified index.
     *
     * @param index the index of the node.
     * @return an assertion for the node at {@code index}.
     */
    public AbstractMethodNodeAssert<?> extractingMethodNode(final int index) {
        return extractingMethodNode(index, DefaultPathAssert.DefaultNodeAssert.DefaultMethodNodeAssert::new);
    }

    /**
     * Verifies that the {@link Path.MethodNode node} at specified index satisfies specified consumer.
     *
     * @param index    the index of the node.
     * @param consumer the consumer verifying the node.
     * @return this assertion object.
     */
    public SELF hasMethodNodeSatisfying(final int index, final Consumer<? super AbstractMethodNodeAssert<?>> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        consumer.accept(extractingMethodNode(index));
        return myself;
    }

    /**
     * Extracts an assertion for verifying the {@link Path.ParameterNode node} at specified index, using specified
     * assertion factory.
     *
     * @param index   the index of the node.
     * @param factory the assertion factory.
     * @param <A>     assertion type parameter
     * @return an instance of {@link A}.
     */
    public <A extends AbstractParameterNodeAssert<? extends A>> A extractingParameterNode(
            final int index, final AssertFactory<? super Path.ParameterNode, ? extends A> factory) {
        return extractingNode(index, Path.ParameterNode.class, factory);
    }

    /**
     * Extracts an assertion for verifying the {@link Path.ParameterNode node} at specified index.
     *
     * @param index the index of the node.
     * @return an assertion for the node at {@code index}.
     */
    public AbstractParameterNodeAssert<?> extractingParameterNode(final int index) {
        return extractingParameterNode(index, DefaultPathAssert.DefaultNodeAssert.DefaultParameterNodeAssert::new);
    }

    /**
     * Verifies that the {@link Path.ParameterNode node} at specified index satisfies specified consumer.
     *
     * @param index    the index of the node.
     * @param consumer the consumer verifying the node.
     * @return this assertion object.
     */
    public SELF hasParameterNodeSatisfying(final int index, final Consumer<? super AbstractParameterNodeAssert<?>> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        consumer.accept(extractingParameterNode(index));
        return myself;
    }

    /**
     * Extracts an assertion for verifying the {@link Path.ReturnValueNode node} at specified index, using specified
     * assertion factory.
     *
     * @param index   the index of the node.
     * @param factory the assertion factory.
     * @param <A>     assertion type parameter
     * @return an instance of {@link A}.
     */
    public <A extends AbstractReturnValueNodeAssert<? extends A>> A extractingReturnValueNode(
            final int index, final AssertFactory<? super Path.ReturnValueNode, ? extends A> factory) {
        return extractingNode(index, Path.ReturnValueNode.class, factory);
    }

    /**
     * Extracts an assertion for verifying the {@link Path.ReturnValueNode node} at specified index.
     *
     * @param index the index of the node.
     * @return an assertion for the node at {@code index}.
     */
    public AbstractReturnValueNodeAssert<?> extractingReturnValueNode(final int index) {
        return extractingReturnValueNode(index, DefaultPathAssert.DefaultNodeAssert.DefaultReturnValueNodeAssert::new);
    }

    /**
     * Verifies that the {@link Path.ReturnValueNode node} at specified index satisfies specified consumer.
     *
     * @param index    the index of the node.
     * @param consumer the consumer verifying the node.
     * @return this assertion object.
     */
    public SELF hasReturnValueNodeSatisfying(final int index, final Consumer<? super AbstractReturnValueNodeAssert<?>> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        consumer.accept(extractingReturnValueNode(index));
        return myself;
    }

    // ----------------------------------------------------------------------------------------------------------- nodes

    /**
     * Returns the {@code actual} path's nodes, in order.
     *
     * @return a list of the {@code actual} path's nodes.
     */
    private List<Path.Node> nodeList() {
        isNotNull();
        final List<Path.Node> list = new ArrayList<>();
        actual.forEach(list::add);
        return list;
    }

    /**
     * Returns an assertion for the {@code actual} path's nodes, as a list.
     * <p>
     * This is where the full collection surface lives &mdash; {@code filteredOn}, {@code contains}, {@code allSatisfy},
     * {@code extracting} and the rest. Filtering a list of nodes yields a list of nodes, so nothing has to pretend to
     * be a {@link Path}. For a node of a known kind at a known index, prefer the typed navigation, such as
     * {@link #extractingPropertyNode(int)}.
     * {@snippet lang = "java" id = "nodes":
     * assertThatPath(path).hasSize(2);                             // the size vocabulary, on the path
     * assertThatPath(path).nodes().filteredOn(n -> n.isInIterable()).isEmpty();
     *}
     *
     * @return a list assertion for the {@code actual} path's nodes.
     */
    public ListAssert<Path.Node> nodes() {
        return Assertions.assertThat(nodeList());
    }

    // ------------------------------------------------------------------------------------------------ EnumerableAssert

    @Override
    public void isNullOrEmpty() {
        if (actual != null && size() != 0) {
            throwAssertionError(ShouldBeNullOrEmpty.shouldBeNullOrEmpty(actual));
        }
    }

    @Override
    public void isEmpty() {
        if (sizeOfNonNull() != 0) {
            throwAssertionError(ShouldBeEmpty.shouldBeEmpty(actual));
        }
    }

    @Override
    public SELF isNotEmpty() {
        if (sizeOfNonNull() == 0) {
            throwAssertionError(ShouldNotBeEmpty.shouldNotBeEmpty());
        }
        return myself;
    }

    @Override
    public SELF hasSize(final int expected) {
        final int size = sizeOfNonNull();
        if (size != expected) {
            throwAssertionError(ShouldHaveSize.shouldHaveSize(actual, size, expected));
        }
        return myself;
    }

    @Override
    public SELF hasSizeGreaterThan(final int boundary) {
        final int size = sizeOfNonNull();
        if (size <= boundary) {
            throwAssertionError(ShouldHaveSizeGreaterThan.shouldHaveSizeGreaterThan(actual, size, boundary));
        }
        return myself;
    }

    @Override
    public SELF hasSizeGreaterThanOrEqualTo(final int boundary) {
        final int size = sizeOfNonNull();
        if (size < boundary) {
            throwAssertionError(
                    ShouldHaveSizeGreaterThanOrEqualTo.shouldHaveSizeGreaterThanOrEqualTo(actual, size, boundary));
        }
        return myself;
    }

    @Override
    public SELF hasSizeLessThan(final int boundary) {
        final int size = sizeOfNonNull();
        if (size >= boundary) {
            throwAssertionError(ShouldHaveSizeLessThan.shouldHaveSizeLessThan(actual, size, boundary));
        }
        return myself;
    }

    @Override
    public SELF hasSizeLessThanOrEqualTo(final int boundary) {
        final int size = sizeOfNonNull();
        if (size > boundary) {
            throwAssertionError(
                    ShouldHaveSizeLessThanOrEqualTo.shouldHaveSizeLessThanOrEqualTo(actual, size, boundary));
        }
        return myself;
    }

    @Override
    public SELF hasSizeBetween(final int lowerBoundary, final int higherBoundary) {
        if (higherBoundary < lowerBoundary) {
            throw new IllegalArgumentException(String.format(
                    "The higher boundary <%1$s> must be greater than the lower boundary <%2$s>.",
                    higherBoundary, lowerBoundary));
        }
        final int size = sizeOfNonNull();
        if (size < lowerBoundary || size > higherBoundary) {
            throwAssertionError(
                    ShouldHaveSizeBetween.shouldHaveSizeBetween(actual, size, lowerBoundary, higherBoundary));
        }
        return myself;
    }

    @Override
    public SELF hasSameSizeAs(final Iterable<?> other) {
        Objects.requireNonNull(other, "The Iterable to compare actual size with should not be null");
        int otherSize = 0;
        for (final Object ignored : other) {
            otherSize++;
        }
        return hasSameSizeAs(other, otherSize);
    }

    @Override
    public SELF hasSameSizeAs(final Object other) {
        Objects.requireNonNull(other, "The array to compare actual size with should not be null");
        if (!other.getClass().isArray()) {
            throw new IllegalArgumentException(String.format("The argument should be an array but was: <%1$s>", other));
        }
        return hasSameSizeAs(other, Array.getLength(other));
    }

    private SELF hasSameSizeAs(final Object other, final int otherSize) {
        final int size = sizeOfNonNull();
        if (size != otherSize) {
            throwAssertionError(ShouldHaveSameSizeAs.shouldHaveSameSizeAs(actual, other, size, otherSize));
        }
        return myself;
    }

    /**
     * Not supported.
     *
     * @param customComparator ignored.
     * @return never returns.
     * @throws UnsupportedOperationException always.
     * @implNote A {@link Path} has no element-comparison semantics to customize, and nothing here compares nodes to one
     * another. Use {@link #nodes()} and configure the comparator on the resulting list assertion instead.
     * {@code AbstractCharSequenceAssert} refuses the same pair for the same reason.
     */
    @Override
    public SELF usingElementComparator(final Comparator<? super Path.Node> customComparator) {
        throw new UnsupportedOperationException(
                "custom element comparator is not supported for a path; use nodes() instead");
    }

    /**
     * Not supported.
     *
     * @return never returns.
     * @throws UnsupportedOperationException always.
     * @implNote See {@link #usingElementComparator(Comparator)}.
     */
    @Override
    public SELF usingDefaultElementComparator() {
        throw new UnsupportedOperationException(
                "custom element comparator is not supported for a path; use nodes() instead");
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the number of nodes in the {@code actual} path, which must be not {@code null}.
     *
     * @return the number of nodes in the {@code actual} path.
     */
    private int size() {
        int size = 0;
        for (final Path.Node ignored : actual) {
            size++;
        }
        return size;
    }

    /**
     * Verifies that the {@code actual} path is not {@code null}, and returns the number of its nodes.
     *
     * @return the number of nodes in the {@code actual} path.
     */
    private int sizeOfNonNull() {
        isNotNull();
        return size();
    }
}
