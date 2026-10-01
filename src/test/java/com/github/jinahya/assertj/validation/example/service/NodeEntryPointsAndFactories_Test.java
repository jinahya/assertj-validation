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

import com.github.jinahya.assertj.validation.ValidationInstanceOfAssertFactories;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ElementKind;
import jakarta.validation.Path;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Iterator;

import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatMethodNode;
import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatNode;
import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatParameterNode;
import static com.github.jinahya.assertj.validation.ValidationInstanceOfAssertFactories.METHOD_DESCRIPTOR;
import static com.github.jinahya.assertj.validation.ValidationInstanceOfAssertFactories.METHOD_NODE;
import static com.github.jinahya.assertj.validation.ValidationInstanceOfAssertFactories.PATH;
import static com.github.jinahya.assertj.validation.ValidationInstanceOfAssertFactories.PROPERTY_DESCRIPTOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests the standalone {@code assertThat*Node} entry points and the {@code InstanceOfAssertFactory} constants.
 */
class NodeEntryPointsAndFactories_Test {

    private static ConstraintViolation<Greeter> parameterViolation() throws NoSuchMethodException {
        final Method method = Greeter.class.getMethod(Greeter.METHOD_NAME_GREET, String.class, int.class);
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            return factory.getValidator().forExecutables()
                    .validateParameters(new Greeter("Jane"), method, new Object[]{"  ", 1})
                    .iterator().next();
        }
    }

    // ------------------------------------------------------------------------------- standalone node entry points
    @Test
    void assertThatNode__onABareNode() throws Exception {
        final Iterator<Path.Node> i = parameterViolation().getPropertyPath().iterator();
        assertThatNode(i.next()).hasKind(ElementKind.METHOD).hasName(Greeter.METHOD_NAME_GREET);
        assertThatNode(i.next()).hasKind(ElementKind.PARAMETER);
    }

    @Test
    void assertThatNode__overAnIteration() throws Exception {
        // the shape that had no entry point before: a node reached without an index
        parameterViolation().getPropertyPath()
                .forEach(node -> assertThatNode(node).extractingKind().isNotNull());
    }

    @Test
    void assertThatTypedNode__fromAs() throws Exception {
        final Iterator<Path.Node> i = parameterViolation().getPropertyPath().iterator();
        assertThatMethodNode(i.next().as(Path.MethodNode.class))
                .hasName(Greeter.METHOD_NAME_GREET)
                .extractingParameterTypes().containsExactly(String.class, int.class);
        assertThatParameterNode(i.next().as(Path.ParameterNode.class))
                .hasParameterIndex(0);
    }

    // ---------------------------------------------------------------------------------- InstanceOfAssertFactory
    @Test
    void factory__path() throws Exception {
        assertThat((Object) parameterViolation().getPropertyPath())
                .asInstanceOf(PATH)
                .hasSize(2);
    }

    @Test
    void factory__methodNode() throws Exception {
        final Path.Node node = parameterViolation().getPropertyPath().iterator().next();
        assertThat((Object) node)
                .asInstanceOf(METHOD_NODE)
                .hasName(Greeter.METHOD_NAME_GREET);
    }

    @Test
    void factory__metadataDescriptors() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            final var bean = factory.getValidator().getConstraintsForClass(Greeter.class);
            assertThat((Object) bean.getConstraintsForMethod(Greeter.METHOD_NAME_GREET, String.class, int.class))
                    .asInstanceOf(METHOD_DESCRIPTOR)
                    .hasName(Greeter.METHOD_NAME_GREET);
            assertThat((Object) bean.getConstraintsForProperty("name"))
                    .asInstanceOf(PROPERTY_DESCRIPTOR)
                    .hasPropertyName("name");
        }
    }

    @Test
    void factory__everyConstantIsPresentAndNonNull() throws Exception {
        // guards against a constant being dropped: 10 node/path + 9 metadata
        final var fields = java.util.Arrays.stream(ValidationInstanceOfAssertFactories.class.getFields())
                .filter(f -> java.lang.reflect.Modifier.isStatic(f.getModifiers()))
                .filter(f -> f.getType() == org.assertj.core.api.InstanceOfAssertFactory.class)
                .toList();
        assertThat(fields).hasSize(19);
        for (final var f : fields) {
            assertThat(f.get(null)).as(f.getName()).isNotNull();
        }
    }

    @Test
    void factories__cannotBeInstantiated() throws Exception {
        final var constructor = ValidationInstanceOfAssertFactories.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        assertThatThrownBy(constructor::newInstance)
                .hasCauseInstanceOf(AssertionError.class);
    }
}
