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

import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.metadata.BeanDescriptor;
import jakarta.validation.metadata.MethodType;
import org.junit.jupiter.api.Test;

import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatBeanDescriptor;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests the assertions for the Jakarta Validation metadata API, reached through
 * {@link jakarta.validation.Validator#getConstraintsForClass(Class)}.
 */
class BeanDescriptorAssert_Test {

    private static BeanDescriptor descriptor() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            return factory.getValidator().getConstraintsForClass(Greeter.class);
        }
    }

    @Test
    void __beanConstrained() {
        assertThatBeanDescriptor(descriptor())
                .isBeanConstrained()
                .hasElementClass(Greeter.class);
    }

    @Test
    void __constrainedProperties() {
        assertThatBeanDescriptor(descriptor())
                .extractingConstrainedProperties()
                .isNotEmpty();
    }

    @Test
    void __constraintsForProperty() {
        assertThatBeanDescriptor(descriptor())
                .hasConstraintsForProperty("name")
                .extractingConstraintsForProperty("name")
                .hasPropertyName("name")
                .hasConstraints()
                .isNotCascaded();
    }

    @Test
    void __constraintsForUnknownPropertyIsNull() {
        assertThatBeanDescriptor(descriptor())
                .extractingConstraintsForProperty("nosuch")
                .isNull();
    }

    @Test
    void __constraintsForMethod() {
        assertThatBeanDescriptor(descriptor())
                .extractingConstraintsForMethod(Greeter.METHOD_NAME_GREET, String.class, int.class)
                .hasName(Greeter.METHOD_NAME_GREET)
                .hasConstrainedParameters()
                .hasConstrainedReturnValue();
    }

    @Test
    void __methodParameterDescriptor() {
        assertThatBeanDescriptor(descriptor())
                .extractingConstraintsForMethod(Greeter.METHOD_NAME_GREET, String.class, int.class)
                .extractingParameterDescriptor(0)
                .hasIndex(0)
                .hasElementClass(String.class)
                .hasConstraints();
    }

    @Test
    void __methodReturnValueDescriptor() {
        assertThatBeanDescriptor(descriptor())
                .extractingConstraintsForMethod(Greeter.METHOD_NAME_GREET, String.class, int.class)
                .extractingReturnValueDescriptor()
                .hasConstraints()
                .hasElementClass(String.class);
    }

    @Test
    void __methodCrossParameterDescriptor() {
        assertThatBeanDescriptor(descriptor())
                .extractingConstraintsForMethod(Greeter.METHOD_NAME_GREET, String.class, int.class)
                .extractingCrossParameterDescriptor()
                .doesNotHaveConstraints();
    }

    @Test
    void __constrainedMethods() {
        assertThatBeanDescriptor(descriptor())
                .extractingConstrainedMethods(MethodType.NON_GETTER)
                .isNotEmpty();
    }

    @Test
    void __constraintsForConstructor() {
        assertThatBeanDescriptor(descriptor())
                .extractingConstraintsForConstructor(String.class)
                .hasConstrainedParameters()
                .hasConstrainedReturnValue();
    }

    @Test
    void __constrainedConstructors() {
        assertThatBeanDescriptor(descriptor())
                .extractingConstrainedConstructors()
                .isNotEmpty();
    }

    @Test
    void __constraintDescriptors() {
        assertThatBeanDescriptor(descriptor())
                .extractingConstraintsForProperty("name")
                .extractingConstraintDescriptors()
                .isNotEmpty();
    }

    @Test
    void __failsWhenNotSatisfied() {
        assertThatThrownBy(() -> assertThatBeanDescriptor(descriptor()).isNotBeanConstrained())
                .isInstanceOf(AssertionError.class);
        assertThatCode(() -> assertThatBeanDescriptor(descriptor()).isBeanConstrained())
                .doesNotThrowAnyException();
    }
}
