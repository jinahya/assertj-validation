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
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.AssertFactory;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.EnumerableAssert;
import org.assertj.core.api.ListAssert;
import org.assertj.core.api.WritableAssertionInfo;
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
import java.util.List;
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
 * <p>
 * The node assertions' {@code has…Satisfying(Consumer)} methods, such as {@code hasNameSatisfying(..)}, hand the
 * consumer an <em>assertion</em> on the attribute, carrying the node assertion's description and representation, not
 * the attribute's value as AssertJ's own {@code hasValueSatisfying(..)} does:
 * {@code hasNameSatisfying(a -> a.startsWith("na"))}.
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

    /**
     * Returns the node at specified index of specified iterable of nodes.
     *
     * @param iterable the iterable of nodes; must be not {@code null}.
     * @param index    the index of the node; must be not negative.
     * @return the extracted assertion.
     * @throws IllegalArgumentException when {@code index} is negative.
     * @throws AssertionError           when {@code iterable} has no node at {@code index}.
     */
    protected static Path.Node nodeAt(final Iterable<? extends Path.Node> iterable, final int index) {
        Objects.requireNonNull(iterable, "iterable is null");
        PathNodes.checkIndex(index);
        return PathNodes.nodeAt(iterable, index, AssertionError::new);
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
        return PathNodes.narrowAt(nodeAt(iterable, index), index, nodeType, AssertionError::new);
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
     * @param index    the index of the node; must be not negative.
     * @param nodeType the type of the node.
     * @param factory  the assertion factory.
     * @return the extracted assertion.
     * @throws IllegalArgumentException when {@code index} is negative.
     * @throws AssertionError           when the {@code actual} is {@code null}, has no node at {@code index}, or its
     *                                  node at {@code index} is not of the kind {@code nodeType} represents.
     */
    @CheckReturnValue
    public <A extends AbstractAssert<?, ? extends N>, N extends Path.Node> A extractingNode(
            final int index, final Class<N> nodeType, final AssertFactory<? super N, ? extends A> factory) {
        PathNodes.checkIndex(index);
        isNotNull();
        // Use the origin assertion's failure handling for both lookup and narrowing.
        final Function<String, AssertionError> failure = message -> failure("%s", message);
        final Path.Node node = PathNodes.nodeAt(actual, index, failure);
        final N narrowed = PathNodes.narrowAt(node, index, nodeType, failure);
        return inheritInfo(factory.createAssert(narrowed));
    }

    /**
     * Verifies that the {@code actual} value's {@code node} satisfies specified consumer.
     * <p>
     * The consumer receives an <em>assertion</em> on the node, carrying this assertion's description and
     * representation, not the value itself, e.g. {@code hasNodeSatisfying(a -> a.isNotNull())}.
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
    @CheckReturnValue
    public <A extends AbstractNodeAssert<? extends A, Path.Node>> A extractingNode(
            final int index, final AssertFactory<? super Path.Node, ? extends A> factory) {
        return extractingNode(index, Path.Node.class, factory);
    }

    /**
     * Extracts an assertion for verifying the {@code actual} value's {@code node}.
     *
     * @param index the index of the node.
     * @return the extracted assertion.
     */
    @CheckReturnValue
    public AbstractNodeAssert<?, Path.Node> extractingNode(final int index) {
        return extractingNode(index, NodeAssert::new);
    }

    /**
     * Verifies that the {@code actual} value's {@code node} satisfies specified consumer.
     * <p>
     * The consumer receives an <em>assertion</em> on the node, carrying this assertion's description and
     * representation, not the value itself, e.g. {@code hasNodeSatisfying(a -> a.isNotNull())}.
     *
     * @param index    the index of the node.
     * @param consumer the consumer accepting the extracted assertion.
     * @return this assertion object.
     */
    public SELF hasNodeSatisfying(final int index, final Consumer<? super AbstractNodeAssert<?, Path.Node>> consumer) {
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
    @CheckReturnValue
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
    @CheckReturnValue
    public AbstractBeanNodeAssert<?> extractingBeanNode(final int index) {
        return extractingBeanNode(index, BeanNodeAssert::new);
    }

    /**
     * Extracts an assertion for verifying the {@code actual} value's {@code propertyNode}.
     *
     * @param <A>     assertion type parameter
     * @param index   the index of the node.
     * @param factory the assertion factory.
     * @return the extracted assertion.
     */
    @CheckReturnValue
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
    @CheckReturnValue
    public AbstractPropertyNodeAssert<?> extractingPropertyNode(final int index) {
        return extractingPropertyNode(index, PropertyNodeAssert::new);
    }

    /**
     * Verifies that the {@code actual} value's {@code propertyNode} satisfies specified consumer.
     * <p>
     * The consumer receives an <em>assertion</em> on the property node, carrying this assertion's description and
     * representation, not the value itself, e.g. {@code hasPropertyNodeSatisfying(a -> a.isNotNull())}.
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
    @CheckReturnValue
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
    @CheckReturnValue
    public AbstractConstructorNodeAssert<?> extractingConstructorNode(final int index) {
        return extractingConstructorNode(index, ConstructorNodeAssert::new);
    }

    /**
     * Verifies that the {@link Path.ConstructorNode node} at specified index satisfies specified consumer.
     * <p>
     * The consumer receives an <em>assertion</em> on the constructor node, carrying this assertion's description and
     * representation, not the value itself, e.g. {@code hasConstructorNodeSatisfying(a -> a.isNotNull())}.
     *
     * @param index    the index of the node.
     * @param consumer the consumer verifying the node.
     * @return this assertion object.
     */
    public SELF hasConstructorNodeSatisfying(final int index,
                                             final Consumer<? super AbstractConstructorNodeAssert<?>> consumer) {
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
    @CheckReturnValue
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
    @CheckReturnValue
    public AbstractContainerElementNodeAssert<?> extractingContainerElementNode(final int index) {
        return extractingContainerElementNode(index, ContainerElementNodeAssert::new);
    }

    /**
     * Verifies that the {@link Path.ContainerElementNode node} at specified index satisfies specified consumer.
     * <p>
     * The consumer receives an <em>assertion</em> on the container element node, carrying this assertion's description
     * and representation, not the value itself, e.g. {@code hasContainerElementNodeSatisfying(a -> a.isNotNull())}.
     *
     * @param index    the index of the node.
     * @param consumer the consumer verifying the node.
     * @return this assertion object.
     */
    public SELF hasContainerElementNodeSatisfying(final int index,
                                                  final Consumer<? super AbstractContainerElementNodeAssert<?>> consumer) {
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
    @CheckReturnValue
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
    @CheckReturnValue
    public AbstractCrossParameterNodeAssert<?> extractingCrossParameterNode(final int index) {
        return extractingCrossParameterNode(index, CrossParameterNodeAssert::new);
    }

    /**
     * Verifies that the {@link Path.CrossParameterNode node} at specified index satisfies specified consumer.
     * <p>
     * The consumer receives an <em>assertion</em> on the cross parameter node, carrying this assertion's description
     * and representation, not the value itself, e.g. {@code hasCrossParameterNodeSatisfying(a -> a.isNotNull())}.
     *
     * @param index    the index of the node.
     * @param consumer the consumer verifying the node.
     * @return this assertion object.
     */
    public SELF hasCrossParameterNodeSatisfying(final int index,
                                                final Consumer<? super AbstractCrossParameterNodeAssert<?>> consumer) {
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
    @CheckReturnValue
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
    @CheckReturnValue
    public AbstractMethodNodeAssert<?> extractingMethodNode(final int index) {
        return extractingMethodNode(index, MethodNodeAssert::new);
    }

    /**
     * Verifies that the {@link Path.MethodNode node} at specified index satisfies specified consumer.
     * <p>
     * The consumer receives an <em>assertion</em> on the method node, carrying this assertion's description and
     * representation, not the value itself, e.g. {@code hasMethodNodeSatisfying(a -> a.isNotNull())}.
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
    @CheckReturnValue
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
    @CheckReturnValue
    public AbstractParameterNodeAssert<?> extractingParameterNode(final int index) {
        return extractingParameterNode(index, ParameterNodeAssert::new);
    }

    /**
     * Verifies that the {@link Path.ParameterNode node} at specified index satisfies specified consumer.
     * <p>
     * The consumer receives an <em>assertion</em> on the parameter node, carrying this assertion's description and
     * representation, not the value itself, e.g. {@code hasParameterNodeSatisfying(a -> a.isNotNull())}.
     *
     * @param index    the index of the node.
     * @param consumer the consumer verifying the node.
     * @return this assertion object.
     */
    public SELF hasParameterNodeSatisfying(final int index,
                                           final Consumer<? super AbstractParameterNodeAssert<?>> consumer) {
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
    @CheckReturnValue
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
    @CheckReturnValue
    public AbstractReturnValueNodeAssert<?> extractingReturnValueNode(final int index) {
        return extractingReturnValueNode(index, ReturnValueNodeAssert::new);
    }

    /**
     * Verifies that the {@link Path.ReturnValueNode node} at specified index satisfies specified consumer.
     * <p>
     * The consumer receives an <em>assertion</em> on the return value node, carrying this assertion's description and
     * representation, not the value itself, e.g. {@code hasReturnValueNodeSatisfying(a -> a.isNotNull())}.
     *
     * @param index    the index of the node.
     * @param consumer the consumer verifying the node.
     * @return this assertion object.
     */
    public SELF hasReturnValueNodeSatisfying(final int index,
                                             final Consumer<? super AbstractReturnValueNodeAssert<?>> consumer) {
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
        return inheritInfo(Assertions.assertThat(nodeList()));
    }

    /**
     * Hands this assertion object's description, representation and overriding error message to specified assertion,
     * which navigation created.
     * <p>
     * The overriding message is handed over lazily, as a supplier reading this object's, so that a message supplied
     * with {@code withFailMessage(Supplier)} is evaluated only if the navigated assertion fails.
     * {@code AbstractAssert#extracting} would copy it eagerly, and evaluate it on success too.
     *
     * @param navigated the assertion navigation created.
     * @param <A>       assertion type parameter
     * @return {@code navigated}.
     */
    private <A extends AbstractAssert<?, ?>> A inheritInfo(final A navigated) {
        final WritableAssertionInfo source = getWritableAssertionInfo();
        final WritableAssertionInfo target = navigated.getWritableAssertionInfo();
        target.description(source.description());
        target.useRepresentation(source.representation());
        target.overridingErrorMessage((Supplier<String>) source::overridingErrorMessage);
        return navigated;
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
                    higherBoundary,
                    lowerBoundary
            ));
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
     * @implNote A {@link Path} has no element-comparison semantics to customize, and nothing here compares
     *         nodes to one another. Use {@link #nodes()} and configure the comparator on the resulting list assertion
     *         instead. {@code AbstractCharSequenceAssert} refuses the same pair for the same reason.
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
