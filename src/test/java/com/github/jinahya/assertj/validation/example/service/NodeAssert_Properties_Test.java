package com.github.jinahya.assertj.validation.example.service;

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

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ElementKind;
import jakarta.validation.Path;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatPath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests the node assertions added to close the gaps in the property matrix: every node property now offers
 * {@code extracting}, {@code ...Satisfying}, a direct {@code has...} and, where the value can be absent, a
 * negative.
 */
class NodeAssert_Properties_Test {

    private static Set<ConstraintViolation<Bagged>> violations() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            return factory.getValidator().validate(new Bagged("  "));
        }
    }

    private static Path pathOfKind(final ElementKind kind) {
        return violations().stream()
                .map(ConstraintViolation::getPropertyPath)
                .filter(p -> p.iterator().next().getKind() == kind)
                .findFirst().orElseThrow();
    }

    // ------------------------------------------------------------------------------------- doesNotHaveName
    @DisplayName("a BEAN node has no name, and doesNotHaveName() says so")
    @Test
    void doesNotHaveName__BeanNode() {
        final Path path = pathOfKind(ElementKind.BEAN);
        assertThat(path.iterator().next().getName()).as("the premise").isNull();
        assertThatPath(path).extractingBeanNode(0)
                .hasKind(ElementKind.BEAN)
                .doesNotHaveName()
                .doesNotHaveIndex()
                .doesNotHaveKey()
                .isNotInIterable();
    }

    @DisplayName("doesNotHaveName() fails on a node that has one")
    @Test
    void doesNotHaveName__Fails() {
        final Path path = pathOfKind(ElementKind.PROPERTY);
        assertThatThrownBy(() -> assertThatPath(path).extractingPropertyNode(0).doesNotHaveName())
                .isInstanceOf(AssertionError.class);
    }

    // ------------------------------------------------------------------- doesNotHaveContainerClass / inIterable
    @DisplayName("a property node outside a container has no containerClass")
    @Test
    void doesNotHaveContainerClass__PropertyNode() {
        assertThatPath(pathOfKind(ElementKind.PROPERTY)).extractingPropertyNode(0)
                .hasName(Bagged.PROPERTY_NAME_TAGS)
                .doesNotHaveContainerClass()
                .doesNotHaveTypeArgumentIndex()
                .isNotInIterable();
    }

    @DisplayName("a container element node carries its container, index and in-iterable flag")
    @Test
    void containerElementNode__() {
        assertThatPath(pathOfKind(ElementKind.PROPERTY)).extractingContainerElementNode(1)
                .hasKind(ElementKind.CONTAINER_ELEMENT)
                .hasContainerClass(List.class)
                .hasTypeArgumentIndex(0)
                .hasIndex(0)
                .isInIterable();
    }

    @DisplayName("doesNotHaveContainerClass() fails when there is one")
    @Test
    void doesNotHaveContainerClass__Fails() {
        assertThatThrownBy(() -> assertThatPath(pathOfKind(ElementKind.PROPERTY))
                .extractingContainerElementNode(1).doesNotHaveContainerClass())
                .isInstanceOf(AssertionError.class);
    }

    // ------------------------------------------------------------------------------------ hasParameterTypes
    private static Path methodPath() throws NoSuchMethodException {
        final Method method = Greeter.class.getMethod(Greeter.METHOD_NAME_GREET, String.class, int.class);
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            return factory.getValidator().forExecutables()
                    .validateParameters(new Greeter("Jane"), method, new Object[]{"  ", 1})
                    .iterator().next().getPropertyPath();
        }
    }

    @DisplayName("hasParameterTypes(..) asserts the types directly, in order")
    @Test
    void hasParameterTypes__() throws Exception {
        assertThatPath(methodPath()).extractingMethodNode(0)
                .hasParameterTypes(String.class, int.class);
    }

    @DisplayName("hasParameterTypes() with no argument asserts there are none")
    @Test
    void hasParameterTypes__Empty() throws Exception {
        assertThatThrownBy(() -> assertThatPath(methodPath()).extractingMethodNode(0).hasParameterTypes())
                .as("greet takes two parameters, so asserting none must fail")
                .isInstanceOf(AssertionError.class);
    }

    @DisplayName("hasParameterTypesSatisfying(consumer) reaches the list assertion")
    @Test
    void hasParameterTypesSatisfying__() throws Exception {
        assertThatPath(methodPath()).extractingMethodNode(0)
                .hasParameterTypesSatisfying(a -> a.hasSize(2).first().isEqualTo(String.class));
    }

    @DisplayName("hasParameterTypes(..) fails on the wrong types")
    @Test
    void hasParameterTypes__Fails() throws Exception {
        assertThatThrownBy(() -> assertThatPath(methodPath()).extractingMethodNode(0)
                .hasParameterTypes(int.class, String.class))
                .isInstanceOf(AssertionError.class);
    }
}
