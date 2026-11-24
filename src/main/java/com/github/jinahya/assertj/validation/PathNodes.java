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

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Looks up and narrows Jakarta Validation path nodes for assertions and assertion factories.
 */
final class PathNodes {

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

    static void checkIndex(final int index) {
        if (index < 0) {
            throw new IllegalArgumentException("negative index: " + index);
        }
    }

    static Path.Node nodeAt(final Iterable<? extends Path.Node> iterable, final int index,
                            final Function<String, AssertionError> failure) {
        int size = 0;
        for (final Path.Node node : iterable) {
            if (size++ == index) {
                return node;
            }
        }
        throw failure.apply(String.format(
                "%nExpecting a node at index %1$d of%n  <%2$s>%nbut it has %3$d node(s)",
                index,
                iterable,
                size
        ));
    }

    static <N extends Path.Node> N narrowAt(final Path.Node node, final int index, final Class<N> nodeType,
                                            final Function<String, AssertionError> failure) {
        if (nodeType == Path.Node.class) {
            return nodeType.cast(node);
        }
        return narrow(node, nodeType, () -> String.format("the node at index %1$d", index), failure);
    }

    static <N extends Path.Node> N narrowForFactory(final Path.Node node, final Class<N> nodeType) {
        if (node == null) {
            return null;
        }
        return narrow(node, nodeType, () -> "the node", AssertionError::new);
    }

    private static <N extends Path.Node> N narrow(final Path.Node node, final Class<N> nodeType,
                                                  final Supplier<String> description,
                                                  final Function<String, AssertionError> failure) {
        final ElementKind expected = KIND_BY_NODE_TYPE.get(nodeType);
        if (expected != null) {
            // Hibernate Validator implements several node interfaces with one class. The kind determines which
            // interface is valid for this node.
            final ElementKind kind = node.getKind();
            if (kind != expected) {
                throw failure.apply(String.format(
                        "%nExpecting %1$s%n"
                        + "  <%2$s>%n"
                        + "to be of kind%n"
                        + "  <%3$s>%n"
                        + "so that it can be narrowed to <%4$s>, but its kind is%n"
                        + "  <%5$s>",
                        description.get(),
                        node,
                        expected,
                        nodeType.getSimpleName(),
                        kind
                ));
            }
        }
        return node.as(nodeType);
    }

    private PathNodes() {
        throw new AssertionError("instantiation is not allowed");
    }
}
