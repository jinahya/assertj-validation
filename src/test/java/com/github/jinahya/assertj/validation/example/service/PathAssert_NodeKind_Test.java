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

import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatPath;
import static com.github.jinahya.assertj.validation.ValidationInstanceOfAssertFactories.METHOD_NODE;
import static com.github.jinahya.assertj.validation.ValidationInstanceOfAssertFactories.PROPERTY_NODE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests that narrowing a {@link Path.Node} to the wrong kind fails as an assertion.
 * <p>
 * {@link Class#isInstance} cannot discriminate node kinds: Hibernate Validator's node implements every
 * {@link Path.Node} subtype at once. {@link Path.Node#getKind()} is the only reliable discriminator, and
 * {@link Path.Node#as(Class)} throws a {@link ClassCastException} rather than failing as an assertion.
 */
class PathAssert_NodeKind_Test {

    private static Path methodParameterPath() throws NoSuchMethodException {
        final Method method = Greeter.class.getMethod(Greeter.METHOD_NAME_GREET, String.class, int.class);
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            final ConstraintViolation<Greeter> violation = factory.getValidator().forExecutables()
                    .validateParameters(new Greeter("Jane"), method, new Object[]{"  ", 1})
                    .iterator().next();
            return violation.getPropertyPath();
        }
    }

    @DisplayName("the premise: every node is an instanceof every node subtype")
    @Test
    void premise__InstanceofCannotDiscriminate() throws Exception {
        final Path.Node node = methodParameterPath().iterator().next();
        assertThat(node.getKind()).isSameAs(ElementKind.METHOD);
        assertThat(Path.PropertyNode.class.isInstance(node))
                .as("instanceof accepts a PropertyNode even though the kind is METHOD")
                .isTrue();
    }

    @DisplayName("extracting*Node of the wrong kind fails as an assertion, not a ClassCastException")
    @Test
    void extractingWrongKind__AssertionError() throws Exception {
        final Path path = methodParameterPath();
        assertThatThrownBy(() -> assertThatPath(path).extractingPropertyNode(0))
                .isInstanceOf(AssertionError.class)
                .isNotInstanceOf(ClassCastException.class)
                .hasMessageContaining("PROPERTY")
                .hasMessageContaining("METHOD");
    }

    @DisplayName("extracting*Node of the right kind still works")
    @Test
    void extractingRightKind__() throws Exception {
        final Path path = methodParameterPath();
        assertThatCode(() -> assertThatPath(path).extractingMethodNode(0).hasName(Greeter.METHOD_NAME_GREET))
                .doesNotThrowAnyException();
    }

    @DisplayName("asInstanceOf with the wrong node factory fails as an assertion")
    @Test
    void asInstanceOfWrongKind__AssertionError() throws Exception {
        final Path.Node node = methodParameterPath().iterator().next();
        assertThatThrownBy(() -> assertThat((Object) node).asInstanceOf(PROPERTY_NODE))
                .as("without the kind check this silently succeeded, then failed inside the provider")
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("PROPERTY")
                .hasMessageContaining("METHOD");
    }

    @DisplayName("asInstanceOf with the right node factory still narrows")
    @Test
    void asInstanceOfRightKind__() throws Exception {
        final Path.Node node = methodParameterPath().iterator().next();
        assertThat((Object) node).asInstanceOf(METHOD_NODE)
                .hasName(Greeter.METHOD_NAME_GREET)
                .extractingParameterTypes().containsExactly(String.class, int.class);
    }
}
