package com.github.jinahya.assertj.validation;

/*-
 * #%L
 * assertj-validation
 * %%
 * Copyright (C) 2021 - 2024 Jinahya, Inc.
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
import jakarta.validation.Path;
import jakarta.validation.metadata.BeanDescriptor;
import jakarta.validation.metadata.ConstraintDescriptor;
import jakarta.validation.metadata.ConstructorDescriptor;
import jakarta.validation.metadata.ContainerElementTypeDescriptor;
import jakarta.validation.metadata.CrossParameterDescriptor;
import jakarta.validation.metadata.GroupConversionDescriptor;
import jakarta.validation.metadata.MethodDescriptor;
import jakarta.validation.metadata.ParameterDescriptor;
import jakarta.validation.metadata.PropertyDescriptor;
import jakarta.validation.metadata.ReturnValueDescriptor;
import org.assertj.core.api.InstanceOfAssertFactory;

import java.lang.annotation.Annotation;

/**
 * {@link InstanceOfAssertFactory} constants and methods for the Jakarta Validation types, for use with
 * {@link org.assertj.core.api.AbstractAssert#asInstanceOf(InstanceOfAssertFactory) asInstanceOf} and the
 * {@code extracting} navigation methods.
 * <p>
 * Following assertj-core's own {@link org.assertj.core.api.InstanceOfAssertFactories}, the non-generic types are
 * constants and the generic ones are methods.
 * <p>
 * The {@link Path.Node} factories check {@link Path.Node#getKind()} themselves. The {@code instanceof} test
 * {@link org.assertj.core.api.AbstractAssert#asInstanceOf(InstanceOfAssertFactory) asInstanceOf} performs first
 * cannot discriminate them: Hibernate Validator's node implements <em>every</em> {@code Path.Node} subtype at
 * once, so without the kind check {@code asInstanceOf(PROPERTY_NODE)} would accept a method node and fail later,
 * from inside the provider, rather than as an assertion.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class ValidationInstanceOfAssertFactories {

    // ------------------------------------------------------------------------------------------------------- results

    /**
     * Returns a factory for a {@link ConstraintViolation}.
     *
     * @param <T> root bean type parameter
     * @return a factory for a constraint violation.
     */
    @SuppressWarnings({"unchecked"})
    public static <T>
    InstanceOfAssertFactory<ConstraintViolation<T>, AbstractConstraintViolationAssert<?, ConstraintViolation<T>, T>>
    constraintViolation() {
        final Class<ConstraintViolation<T>> type = (Class<ConstraintViolation<T>>) (Object) ConstraintViolation.class;
        return new InstanceOfAssertFactory<>(type, ValidationAssertions::assertThatConstraintViolation);
    }

    /**
     * Returns a factory for a {@link ConstraintDescriptor}.
     *
     * @param <T> constraint annotation type parameter
     * @return a factory for a constraint descriptor.
     */
    @SuppressWarnings({"unchecked"})
    public static <T extends Annotation>
    InstanceOfAssertFactory<ConstraintDescriptor<T>, AbstractConstraintDescriptorAssert<?, ?, T>>
    constraintDescriptor() {
        final Class<ConstraintDescriptor<T>> type = (Class<ConstraintDescriptor<T>>) (Object) ConstraintDescriptor.class;
        return new InstanceOfAssertFactory<>(type, ValidationAssertions::assertThatConstraintDescriptor);
    }

    /**
     * A factory for a {@link Path}.
     */
    public static final InstanceOfAssertFactory<Path, AbstractPathAssert<?>> PATH =
            new InstanceOfAssertFactory<>(Path.class, ValidationAssertions::assertThatPath);

    // --------------------------------------------------------------------------------------------------------- nodes

    /**
     * A factory for a {@link Path.Node}.
     */
    public static final InstanceOfAssertFactory<Path.Node, AbstractPathAssert.AbstractNodeAssert<?>> NODE =
            new InstanceOfAssertFactory<>(Path.Node.class, ValidationAssertions::assertThatNode);

    /**
     * A factory for a {@link Path.BeanNode}.
     */
    public static final InstanceOfAssertFactory<Path.BeanNode, AbstractPathAssert.AbstractBeanNodeAssert<?>> BEAN_NODE =
            new InstanceOfAssertFactory<>(Path.BeanNode.class,
                                          n -> ValidationAssertions.assertThatBeanNode(
                                                  AbstractPathAssert.requireKind(n, Path.BeanNode.class)));

    /**
     * A factory for a {@link Path.ConstructorNode}.
     */
    public static final InstanceOfAssertFactory<Path.ConstructorNode, AbstractPathAssert.AbstractConstructorNodeAssert<?>> CONSTRUCTOR_NODE =
            new InstanceOfAssertFactory<>(Path.ConstructorNode.class,
                                          n -> ValidationAssertions.assertThatConstructorNode(
                                                  AbstractPathAssert.requireKind(n, Path.ConstructorNode.class)));

    /**
     * A factory for a {@link Path.ContainerElementNode}.
     */
    public static final InstanceOfAssertFactory<Path.ContainerElementNode, AbstractPathAssert.AbstractContainerElementNodeAssert<?>> CONTAINER_ELEMENT_NODE =
            new InstanceOfAssertFactory<>(Path.ContainerElementNode.class,
                                          n -> ValidationAssertions.assertThatContainerElementNode(
                                                  AbstractPathAssert.requireKind(n, Path.ContainerElementNode.class)));

    /**
     * A factory for a {@link Path.CrossParameterNode}.
     */
    public static final InstanceOfAssertFactory<Path.CrossParameterNode, AbstractPathAssert.AbstractCrossParameterNodeAssert<?>> CROSS_PARAMETER_NODE =
            new InstanceOfAssertFactory<>(Path.CrossParameterNode.class,
                                          n -> ValidationAssertions.assertThatCrossParameterNode(
                                                  AbstractPathAssert.requireKind(n, Path.CrossParameterNode.class)));

    /**
     * A factory for a {@link Path.MethodNode}.
     */
    public static final InstanceOfAssertFactory<Path.MethodNode, AbstractPathAssert.AbstractMethodNodeAssert<?>> METHOD_NODE =
            new InstanceOfAssertFactory<>(Path.MethodNode.class,
                                          n -> ValidationAssertions.assertThatMethodNode(
                                                  AbstractPathAssert.requireKind(n, Path.MethodNode.class)));

    /**
     * A factory for a {@link Path.ParameterNode}.
     */
    public static final InstanceOfAssertFactory<Path.ParameterNode, AbstractPathAssert.AbstractParameterNodeAssert<?>> PARAMETER_NODE =
            new InstanceOfAssertFactory<>(Path.ParameterNode.class,
                                          n -> ValidationAssertions.assertThatParameterNode(
                                                  AbstractPathAssert.requireKind(n, Path.ParameterNode.class)));

    /**
     * A factory for a {@link Path.PropertyNode}.
     */
    public static final InstanceOfAssertFactory<Path.PropertyNode, AbstractPathAssert.AbstractPropertyNodeAssert<?>> PROPERTY_NODE =
            new InstanceOfAssertFactory<>(Path.PropertyNode.class,
                                          n -> ValidationAssertions.assertThatPropertyNode(
                                                  AbstractPathAssert.requireKind(n, Path.PropertyNode.class)));

    /**
     * A factory for a {@link Path.ReturnValueNode}.
     */
    public static final InstanceOfAssertFactory<Path.ReturnValueNode, AbstractPathAssert.AbstractReturnValueNodeAssert<?>> RETURN_VALUE_NODE =
            new InstanceOfAssertFactory<>(Path.ReturnValueNode.class,
                                          n -> ValidationAssertions.assertThatReturnValueNode(
                                                  AbstractPathAssert.requireKind(n, Path.ReturnValueNode.class)));

    // ------------------------------------------------------------------------------------------------------ metadata

    /**
     * A factory for a {@link BeanDescriptor}.
     */
    public static final InstanceOfAssertFactory<BeanDescriptor, AbstractBeanDescriptorAssert<?>> BEAN_DESCRIPTOR =
            new InstanceOfAssertFactory<>(BeanDescriptor.class, ValidationAssertions::assertThatBeanDescriptor);

    /**
     * A factory for a {@link PropertyDescriptor}.
     */
    public static final InstanceOfAssertFactory<PropertyDescriptor, AbstractPropertyDescriptorAssert<?>> PROPERTY_DESCRIPTOR =
            new InstanceOfAssertFactory<>(PropertyDescriptor.class, ValidationAssertions::assertThatPropertyDescriptor);

    /**
     * A factory for a {@link MethodDescriptor}.
     */
    public static final InstanceOfAssertFactory<MethodDescriptor, AbstractMethodDescriptorAssert<?>> METHOD_DESCRIPTOR =
            new InstanceOfAssertFactory<>(MethodDescriptor.class, ValidationAssertions::assertThatMethodDescriptor);

    /**
     * A factory for a {@link ConstructorDescriptor}.
     */
    public static final InstanceOfAssertFactory<ConstructorDescriptor, AbstractConstructorDescriptorAssert<?>> CONSTRUCTOR_DESCRIPTOR =
            new InstanceOfAssertFactory<>(ConstructorDescriptor.class, ValidationAssertions::assertThatConstructorDescriptor);

    /**
     * A factory for a {@link ParameterDescriptor}.
     */
    public static final InstanceOfAssertFactory<ParameterDescriptor, AbstractParameterDescriptorAssert<?>> PARAMETER_DESCRIPTOR =
            new InstanceOfAssertFactory<>(ParameterDescriptor.class, ValidationAssertions::assertThatParameterDescriptor);

    /**
     * A factory for a {@link ReturnValueDescriptor}.
     */
    public static final InstanceOfAssertFactory<ReturnValueDescriptor, AbstractReturnValueDescriptorAssert<?>> RETURN_VALUE_DESCRIPTOR =
            new InstanceOfAssertFactory<>(ReturnValueDescriptor.class, ValidationAssertions::assertThatReturnValueDescriptor);

    /**
     * A factory for a {@link CrossParameterDescriptor}.
     */
    public static final InstanceOfAssertFactory<CrossParameterDescriptor, AbstractCrossParameterDescriptorAssert<?>> CROSS_PARAMETER_DESCRIPTOR =
            new InstanceOfAssertFactory<>(CrossParameterDescriptor.class, ValidationAssertions::assertThatCrossParameterDescriptor);

    /**
     * A factory for a {@link ContainerElementTypeDescriptor}.
     */
    public static final InstanceOfAssertFactory<ContainerElementTypeDescriptor, AbstractContainerElementTypeDescriptorAssert<?>> CONTAINER_ELEMENT_TYPE_DESCRIPTOR =
            new InstanceOfAssertFactory<>(ContainerElementTypeDescriptor.class, ValidationAssertions::assertThatContainerElementTypeDescriptor);

    /**
     * A factory for a {@link GroupConversionDescriptor}.
     */
    public static final InstanceOfAssertFactory<GroupConversionDescriptor, AbstractGroupConversionDescriptorAssert<?>> GROUP_CONVERSION_DESCRIPTOR =
            new InstanceOfAssertFactory<>(GroupConversionDescriptor.class, ValidationAssertions::assertThatGroupConversionDescriptor);

    /**
     * Creates a new instance.
     */
    private ValidationInstanceOfAssertFactories() {
        throw new AssertionError("instantiation is not allowed");
    }
}
