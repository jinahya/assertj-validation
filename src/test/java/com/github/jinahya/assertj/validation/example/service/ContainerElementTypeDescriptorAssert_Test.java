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

import jakarta.validation.metadata.ContainerElementTypeDescriptor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatBeanDescriptor;
import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatContainerElementTypeDescriptor;
import static com.github.jinahya.assertj.validation.ValidationAssertionsTestUtils.applyValidator;
import static com.github.jinahya.assertj.validation.ValidationInstanceOfAssertFactories.CONTAINER_ELEMENT_TYPE_DESCRIPTOR;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests the assertion for {@link ContainerElementTypeDescriptor}, reached through the {@code List<@NotBlank String>}
 * property of {@link Bagged}.
 */
class ContainerElementTypeDescriptorAssert_Test {

    private static ContainerElementTypeDescriptor descriptor() {
        return applyValidator(v -> v.getConstraintsForClass(Bagged.class)
                .getConstraintsForProperty(Bagged.PROPERTY_NAME_TAGS)
                .getConstrainedContainerElementTypes()
                .iterator().next());
    }

    @Test
    void __navigatedFromProperty() {
        assertThatBeanDescriptor(applyValidator(v -> v.getConstraintsForClass(Bagged.class)))
                .extractingConstraintsForProperty(Bagged.PROPERTY_NAME_TAGS)
                .extractingConstrainedContainerElementTypes()
                .singleElement(CONTAINER_ELEMENT_TYPE_DESCRIPTOR)
                .hasContainerClass(List.class)
                .hasTypeArgumentIndex(0)
                .hasElementClass(String.class)
                .hasConstraints()
                .isNotCascaded();
    }

    @Test
    void __extracting() {
        final var assertion = assertThatContainerElementTypeDescriptor(descriptor());
        assertion.extractingContainerClass().isEqualTo(List.class);
        assertion.extractingTypeArgumentIndex().isZero();
        assertion.extractingConstraintDescriptors().hasSize(1);
    }

    @Test
    void __mismatch() {
        assertThatThrownBy(() -> assertThatContainerElementTypeDescriptor(descriptor())
                .hasContainerClass(java.util.Set.class))
                .isInstanceOf(AssertionError.class);
        assertThatThrownBy(() -> assertThatContainerElementTypeDescriptor(descriptor())
                .hasTypeArgumentIndex(1))
                .isInstanceOf(AssertionError.class);
    }

    @Test
    void __null() {
        assertThatThrownBy(() -> assertThatContainerElementTypeDescriptor(null).hasContainerClass(List.class))
                .isInstanceOf(AssertionError.class);
        assertThatThrownBy(() -> assertThatContainerElementTypeDescriptor(null).hasTypeArgumentIndex(0))
                .isInstanceOf(AssertionError.class);
    }
}
