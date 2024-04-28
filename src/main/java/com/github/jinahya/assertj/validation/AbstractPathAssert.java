package com.github.jinahya.assertj.validation;

/*-
 * #%L
 * assertj-bean-validation-javax
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
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractClassAssert;
import org.assertj.core.api.AbstractComparableAssert;
import org.assertj.core.api.AbstractIntegerAssert;
import org.assertj.core.api.AbstractIterableAssert;
import org.assertj.core.api.AbstractListAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.AssertFactory;

import javax.validation.ElementKind;
import javax.validation.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * An abstract class for verifying {@link Path} values.
 *
 * @param <SELF> self type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S119" // <SELF>, <ACTUAL>
})
public abstract class AbstractPathAssert<
        SELF extends AbstractPathAssert<SELF, NODE_ASSERT>,
        NODE_ASSERT extends AbstractPathAssert.AbstractNodeAssert<NODE_ASSERT>>
        extends AbstractIterableAssert<SELF, Path, Path.Node, NODE_ASSERT>
        implements PathAssert<SELF> {

    // -----------------------------------------------------------------------------------------------------------------
    @SuppressWarnings({
            "java:S101" // class _Abstract...
    })
    abstract static class _AbstractNodeAssert<
            SELF extends _AbstractNodeAssert<SELF, ACTUAL>, ACTUAL extends Path.Node>
            extends AbstractAssert<SELF, ACTUAL>
            implements PathAssert.NodeAssert<SELF, ACTUAL> {

        _AbstractNodeAssert(final ACTUAL actual, final Class<?> selfType) {
            super(actual, selfType);
        }

        // ------------------------------------------------------------------------------------------------------- index
        @Override
        public <ASSERT extends AbstractIntegerAssert<? extends ASSERT>> ASSERT extractingIndex(
                final AssertFactory<? super Integer, ? extends ASSERT> factory) {
            return isNotNull()
                    .extracting(Path.Node::getIndex, factory);
        }

        // --------------------------------------------------------------------------------------------------------- key
        @Override
        public <KEY, ASSERT extends AbstractObjectAssert<ASSERT, ? extends KEY>> ASSERT extractingKey(
                final Function<? super Path.Node, ? extends KEY> extractor,
                final AssertFactory<? super KEY, ? extends ASSERT> factory) {
            Objects.requireNonNull(extractor, "extractor is null");
            return isNotNull()
                    .extracting(extractor, factory);
        }

        // -------------------------------------------------------------------------------------------------------- kind

        @Override
        public <ASSERT extends AbstractComparableAssert<?, ElementKind>> ASSERT extractingKind(
                final AssertFactory<? super ElementKind, ? extends ASSERT> factory) {
            return isNotNull()
                    .extracting(Path.Node::getKind, factory);
        }

        // -------------------------------------------------------------------------------------------------------- name

        @Override
        public <ASSERT extends AbstractStringAssert<? extends ASSERT>> ASSERT extractingName(
                final AssertFactory<? super String, ? extends ASSERT> factory) {
            return isNotNull()
                    .extracting(Path.Node::getName, factory);
        }

        // -------------------------------------------------------------------------------------------------- inIterable

        @Override
        public <ASSERT extends AbstractBooleanAssert<? extends ASSERT>> ASSERT extractingInIterable(
                final AssertFactory<? super Boolean, ? extends ASSERT> factory) {
            return isNotNull()
                    .extracting(Path.Node::isInIterable, factory);
        }
    }

    public abstract static class AbstractNodeAssert<SELF extends AbstractNodeAssert<SELF>>
            extends _AbstractNodeAssert<SELF, Path.Node> {

        AbstractNodeAssert(final Path.Node actual, final Class<?> selfType) {
            super(actual, selfType);
        }
    }

    abstract static class AbstractBeanNodeAssert<SELF extends AbstractBeanNodeAssert<SELF>>
            extends _AbstractNodeAssert<SELF, Path.BeanNode>
            implements BeanNodeAssert<SELF> {

        AbstractBeanNodeAssert(final Path.BeanNode actual, final Class<?> selfType) {
            super(actual, selfType);
        }

        @Override
        public <ASSERT extends AbstractClassAssert<? extends ASSERT>> ASSERT extractingContainerClass(
                AssertFactory<? super Class<?>, ? extends ASSERT> factory) {
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

    abstract static class AbstractConstructorNodeAssert<SELF extends AbstractConstructorNodeAssert<SELF>>
            extends _AbstractNodeAssert<SELF, Path.ConstructorNode>
            implements ConstructorNodeAssert<SELF> {

        AbstractConstructorNodeAssert(final Path.ConstructorNode actual, final Class<?> selfType) {
            super(actual, selfType);
        }

        @Override
        public <ASSERT extends AbstractListAssert<?, List<Class<?>>, Class<?>, ? extends AbstractClassAssert<?>>>
        ASSERT extractingParameterTypes(final AssertFactory<? super List<Class<?>>, ? extends ASSERT> factory) {
            return isNotNull()
                    .extracting(Path.ConstructorNode::getParameterTypes, factory);
        }
    }

    abstract static class AbstractContainerElementNodeAssert<SELF extends AbstractContainerElementNodeAssert<SELF>>
            extends _AbstractNodeAssert<SELF, Path.ContainerElementNode>
            implements ContainerElementNodeAssert<SELF> {

        AbstractContainerElementNodeAssert(final Path.ContainerElementNode actual, final Class<?> selfType) {
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

    abstract static class AbstractCrossParameterNodeAssert<
            SELF extends AbstractCrossParameterNodeAssert<SELF>>
            extends _AbstractNodeAssert<SELF, Path.CrossParameterNode>
            implements CrossParameterNodeAssert<SELF> {

        AbstractCrossParameterNodeAssert(final Path.CrossParameterNode actual, final Class<?> selfType) {
            super(actual, selfType);
        }
    }

    abstract static class AbstractMethodNodeAssert<SELF extends AbstractMethodNodeAssert<SELF>>
            extends _AbstractNodeAssert<SELF, Path.MethodNode>
            implements MethodNodeAssert<SELF> {

        protected AbstractMethodNodeAssert(final Path.MethodNode actual, Class<?> selfType) {
            super(actual, selfType);
        }

        @Override
        public <ASSERT extends AbstractListAssert<?, List<Class<?>>, Class<?>, ? extends AbstractClassAssert<?>>>
        ASSERT extractingParameterTypes(final AssertFactory<? super List<Class<?>>, ? extends ASSERT> factory) {
            return isNotNull()
                    .extracting(Path.MethodNode::getParameterTypes, factory);
        }
    }

    public abstract static class AbstractParameterNodeAssert<SELF extends AbstractParameterNodeAssert<SELF>>
            extends _AbstractNodeAssert<SELF, Path.ParameterNode>
            implements ParameterNodeAssert<SELF> {

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

    public abstract static class AbstractPropertyNodeAssert<SELF extends AbstractPropertyNodeAssert<SELF>>
            extends _AbstractNodeAssert<SELF, Path.PropertyNode>
            implements PropertyNodeAssert<SELF> {

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

    public abstract static class AbstractReturnValueNodeAssert<SELF extends AbstractReturnValueNodeAssert<SELF>>
            extends _AbstractNodeAssert<SELF, Path.ReturnValueNode>
            implements ReturnValueNodeAssert<SELF> {

//        public static class DefaultReturnValueNodeAssert
//                extends AbstractReturnValueNodeAssert<DefaultReturnValueNodeAssert, Path.ReturnValueNode> {
//
//            public DefaultReturnValueNodeAssert(final Path.ReturnValueNode actual) {
//                super(actual, DefaultReturnValueNodeAssert.class);
//            }
//        }

        protected AbstractReturnValueNodeAssert(final Path.ReturnValueNode actual, final Class<?> selfType) {
            super(actual, selfType);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected static Path.Node nodeAt(final Iterable<? extends Path.Node> iterable, final int index) {
        Objects.requireNonNull(iterable, "iterable is null");
        if (index < 0) {
            throw new IllegalArgumentException("negative index: " + index);
        }
        final Iterator<? extends Path.Node> iterator = iterable.iterator();
        Path.Node node = iterator.next(); // NoSuchElementException
        for (int i = 1; i < index; i++) {
            node = iterator.next(); // NoSuchElementException
        }
        return node;
    }

    protected static <N extends Path.Node> N nodeAt(final Iterable<? extends Path.Node> iterable, final int index,
                                                    final Class<N> nodeType) {
        final Path.Node node = nodeAt(iterable, index);
        if (nodeType == Path.Node.class) {
            @SuppressWarnings({"unchecked"})
            final N casted = (N) node;
            return casted;
        }
        return node.as(nodeType);
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected AbstractPathAssert(final Path actual, final Class<?> selfType) {
        super(actual, selfType);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public <A extends _AbstractNodeAssert<? extends A, ? extends N>, N extends Path.Node> A extractingNode(
            final int index, final Class<N> nodeType, final AssertFactory<? super N, ? extends A> factory) {
        return isNotNull()
                .extracting(a -> nodeAt(a, index, nodeType), factory);
    }

    @Override
    public <A extends AbstractNodeAssert<? extends A>> A extractingNode(
            final int index, final AssertFactory<? super Path.Node, ? extends A> factory) {
        return extractingNode(index, Path.Node.class, factory);
    }

    @Override
    public AbstractNodeAssert<?> extractingNode(final int index) {
        return extractingNode(index, DefaultPathAssert.DefaultNodeAssert::new);
    }

    @Override
    public <A extends AbstractBeanNodeAssert<? extends A>> A extractingBeanNode(
            final int index, AssertFactory<? super Path.BeanNode, ? extends A> factory) {
        return extractingNode(index, Path.BeanNode.class, factory);
    }

    @Override
    public AbstractBeanNodeAssert<?> extractingBeanNode(final int index) {
        return extractingBeanNode(index, DefaultPathAssert.DefaultNodeAssert.DefaultBeanNodeAssert::new);
    }

    @Override
    public <A extends AbstractPropertyNodeAssert<? extends A>> A extractingPropertyNode(
            final int index, AssertFactory<? super Path.PropertyNode, ? extends A> factory) {
        return extractingNode(index, Path.PropertyNode.class, factory);
    }

    @Override
    public AbstractPropertyNodeAssert<?> extractingPropertyNode(final int index) {
        return extractingPropertyNode(index, DefaultPathAssert.DefaultNodeAssert.DefaultPropertyNodeAssert::new);
    }
}
