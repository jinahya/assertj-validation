package com.github.jinahya.assertj.validation;

/*-
 * #%L
 * assertj-validation
 * %%
 * Copyright (C) 2021 Jinahya, Inc.
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

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Set;

/**
 * A class for creating assertion instances.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class ValidationAssertions {

    /**
     * Creates a new assertion object for verifying specified bean value.
     *
     * @param <ACTUAL> type of actual bean
     * @param actual   the bean value to verify.
     * @return a new assertion object for verifying {@code actual}.
     */
    public static <ACTUAL> AbstractBeanAssert<?, ACTUAL> assertThatBean(final ACTUAL actual) {
        return new DefaultBeanAssert<>(actual);
    }

    /**
     * Creates a new assertion object for verifying specified value against a property of specific bean type.
     *
     * @param <ACTUAL> type of actual value
     * @param actual   the value of the property to verify.
     * @return a new assertion instance for {@code actual}.
     */
    public static <ACTUAL> AbstractPropertyAssert<?, ACTUAL> assertThatProperty(final ACTUAL actual) {
        return new DefaultPropertyAssert<>(actual);
    }

    /**
     * Creates a new assertion object for verifying specified constraint descriptor.
     *
     * @param <T>    constraint annotation type parameter
     * @param actual the constraint descriptor to verify.
     * @return a new assertion instance for {@code actual}.
     */
    public static <T extends Annotation> AbstractConstraintDescriptorAssert<?, ?, T> assertThatConstraintDescriptor(
            final ConstraintDescriptor<T> actual) {
        return new DefaultConstraintDescriptorAssert<>(actual);
    }

    /**
     * Creates a new assertion object for verifying specified constraint violation value.
     *
     * @param <T>    actual type parameter
     * @param actual the constraint violation value to verify.
     * @return a new assertion instance for {@code actual}.
     */
    public static <T> AbstractConstraintViolationAssert<?, ConstraintViolation<T>, T> assertThatConstraintViolation(
            final ConstraintViolation<T> actual) {
        return new DefaultConstraintViolationAssert<>(actual);
    }

    /**
     * Creates a new assertion object for verifying specified iterable of constraint violations.
     *
     * @param <T>    root bean type parameter
     * @param actual the iterable of constraint violations to verify.
     * @return a new assertion instance for {@code actual}.
     * @see #assertThatConstraintViolations(Set)
     */
    public static <T> AbstractIterableOfConstraintViolationsAssert<?, T> assertThatIterableOfConstraintViolations(
            final Iterable<? extends ConstraintViolation<T>> actual) {
        return new DefaultIterableOfConstraintViolationsAssert<>(actual);
    }

    /**
     * Creates a new assertion object for verifying specified set of constraint violations, the type that
     * {@link jakarta.validation.Validator#validate(Object, Class[]) Validator.validate} and its siblings return.
     *
     * @param <T>    root bean type parameter
     * @param actual the set of constraint violations to verify.
     * @return a new assertion instance for {@code actual}.
     * @see jakarta.validation.Validator#validate(Object, Class[])
     * @see jakarta.validation.Validator#validateProperty(Object, String, Class[])
     * @see jakarta.validation.Validator#validateValue(Class, String, Object, Class[])
     */
    public static <T> AbstractIterableOfConstraintViolationsAssert<?, T> assertThatConstraintViolations(
            final Set<? extends ConstraintViolation<T>> actual) {
        return assertThatIterableOfConstraintViolations(actual);
    }

    /**
     * Creates a new assertion object for verifying specified path value.
     *
     * @param actual the path value to verify.
     * @return a new assertion instance for {@code actual}.
     */
    public static AbstractPathAssert<?> assertThatPath(final Path actual) {
        return new DefaultPathAssert(actual);
    }


    // ------------------------------------------------------------------------------------------ executable validation

    /**
     * Creates a new assertion object for verifying specified constructor's parameters and return value.
     *
     * @param <T>    the type declaring the constructor
     * @param actual the constructor to verify.
     * @return a new assertion instance for {@code actual}.
     * @see jakarta.validation.executable.ExecutableValidator
     */
    public static <T> AbstractConstructorAssert<?, T> assertThatConstructor(final Constructor<? extends T> actual) {
        return new DefaultConstructorAssert<>(actual);
    }

    // ----------------------------------------------------------------------------------------------------- metadata

    /**
     * Creates a new assertion object for verifying specified bean descriptor.
     *
     * @param actual the bean descriptor to verify.
     * @return a new assertion instance for {@code actual}.
     * @see jakarta.validation.Validator#getConstraintsForClass(Class)
     */
    public static AbstractBeanDescriptorAssert<?> assertThatBeanDescriptor(final BeanDescriptor actual) {
        return new DefaultBeanDescriptorAssert(actual);
    }

    /**
     * Creates a new assertion object for verifying specified property descriptor.
     *
     * @param actual the property descriptor to verify.
     * @return a new assertion instance for {@code actual}.
     */
    public static AbstractPropertyDescriptorAssert<?> assertThatPropertyDescriptor(final PropertyDescriptor actual) {
        return new DefaultPropertyDescriptorAssert(actual);
    }

    /**
     * Creates a new assertion object for verifying specified method descriptor.
     *
     * @param actual the method descriptor to verify.
     * @return a new assertion instance for {@code actual}.
     */
    public static AbstractMethodDescriptorAssert<?> assertThatMethodDescriptor(final MethodDescriptor actual) {
        return new DefaultMethodDescriptorAssert(actual);
    }

    /**
     * Creates a new assertion object for verifying specified constructor descriptor.
     *
     * @param actual the constructor descriptor to verify.
     * @return a new assertion instance for {@code actual}.
     */
    public static AbstractConstructorDescriptorAssert<?> assertThatConstructorDescriptor(
            final ConstructorDescriptor actual) {
        return new DefaultConstructorDescriptorAssert(actual);
    }

    /**
     * Creates a new assertion object for verifying specified parameter descriptor.
     *
     * @param actual the parameter descriptor to verify.
     * @return a new assertion instance for {@code actual}.
     */
    public static AbstractParameterDescriptorAssert<?> assertThatParameterDescriptor(
            final ParameterDescriptor actual) {
        return new DefaultParameterDescriptorAssert(actual);
    }

    /**
     * Creates a new assertion object for verifying specified return value descriptor.
     *
     * @param actual the return value descriptor to verify.
     * @return a new assertion instance for {@code actual}.
     */
    public static AbstractReturnValueDescriptorAssert<?> assertThatReturnValueDescriptor(
            final ReturnValueDescriptor actual) {
        return new DefaultReturnValueDescriptorAssert(actual);
    }

    /**
     * Creates a new assertion object for verifying specified cross parameter descriptor.
     *
     * @param actual the cross parameter descriptor to verify.
     * @return a new assertion instance for {@code actual}.
     */
    public static AbstractCrossParameterDescriptorAssert<?> assertThatCrossParameterDescriptor(
            final CrossParameterDescriptor actual) {
        return new DefaultCrossParameterDescriptorAssert(actual);
    }

    /**
     * Creates a new assertion object for verifying specified container element type descriptor.
     *
     * @param actual the container element type descriptor to verify.
     * @return a new assertion instance for {@code actual}.
     */
    public static AbstractContainerElementTypeDescriptorAssert<?> assertThatContainerElementTypeDescriptor(
            final ContainerElementTypeDescriptor actual) {
        return new DefaultContainerElementTypeDescriptorAssert(actual);
    }

    /**
     * Creates a new assertion object for verifying specified group conversion descriptor.
     *
     * @param actual the group conversion descriptor to verify.
     * @return a new assertion instance for {@code actual}.
     */
    public static AbstractGroupConversionDescriptorAssert<?> assertThatGroupConversionDescriptor(
            final GroupConversionDescriptor actual) {
        return new DefaultGroupConversionDescriptorAssert(actual);
    }

    // --------------------------------------------------------------------------------------------------- nodes

    /**
     * Creates a new assertion object for verifying specified node.
     *
     * @param actual the node to verify.
     * @return a new assertion instance for {@code actual}.
     * @see AbstractPathAssert#extractingNode(int)
     */
    public static AbstractPathAssert.AbstractNodeAssert<?> assertThatNode(final Path.Node actual) {
        return new DefaultPathAssert.DefaultNodeAssert(actual);
    }

    /**
     * Creates a new assertion object for verifying specified bean node.
     *
     * @param actual the bean node to verify.
     * @return a new assertion instance for {@code actual}.
     * @see AbstractPathAssert#extractingBeanNode(int)
     */
    public static AbstractPathAssert.AbstractBeanNodeAssert<?> assertThatBeanNode(final Path.BeanNode actual) {
        return new DefaultPathAssert.DefaultNodeAssert.DefaultBeanNodeAssert(actual);
    }

    /**
     * Creates a new assertion object for verifying specified constructor node.
     *
     * @param actual the constructor node to verify.
     * @return a new assertion instance for {@code actual}.
     * @see AbstractPathAssert#extractingConstructorNode(int)
     */
    public static AbstractPathAssert.AbstractConstructorNodeAssert<?> assertThatConstructorNode(final Path.ConstructorNode actual) {
        return new DefaultPathAssert.DefaultNodeAssert.DefaultConstructorNodeAssert(actual);
    }

    /**
     * Creates a new assertion object for verifying specified container element node.
     *
     * @param actual the container element node to verify.
     * @return a new assertion instance for {@code actual}.
     * @see AbstractPathAssert#extractingContainerElementNode(int)
     */
    public static AbstractPathAssert.AbstractContainerElementNodeAssert<?> assertThatContainerElementNode(final Path.ContainerElementNode actual) {
        return new DefaultPathAssert.DefaultNodeAssert.DefaultContainerElementNodeAssert(actual);
    }

    /**
     * Creates a new assertion object for verifying specified cross parameter node.
     *
     * @param actual the cross parameter node to verify.
     * @return a new assertion instance for {@code actual}.
     * @see AbstractPathAssert#extractingCrossParameterNode(int)
     */
    public static AbstractPathAssert.AbstractCrossParameterNodeAssert<?> assertThatCrossParameterNode(final Path.CrossParameterNode actual) {
        return new DefaultPathAssert.DefaultNodeAssert.DefaultCrossParameterNodeAssert(actual);
    }

    /**
     * Creates a new assertion object for verifying specified method node.
     *
     * @param actual the method node to verify.
     * @return a new assertion instance for {@code actual}.
     * @see AbstractPathAssert#extractingMethodNode(int)
     */
    public static AbstractPathAssert.AbstractMethodNodeAssert<?> assertThatMethodNode(final Path.MethodNode actual) {
        return new DefaultPathAssert.DefaultNodeAssert.DefaultMethodNodeAssert(actual);
    }

    /**
     * Creates a new assertion object for verifying specified parameter node.
     *
     * @param actual the parameter node to verify.
     * @return a new assertion instance for {@code actual}.
     * @see AbstractPathAssert#extractingParameterNode(int)
     */
    public static AbstractPathAssert.AbstractParameterNodeAssert<?> assertThatParameterNode(final Path.ParameterNode actual) {
        return new DefaultPathAssert.DefaultNodeAssert.DefaultParameterNodeAssert(actual);
    }

    /**
     * Creates a new assertion object for verifying specified property node.
     *
     * @param actual the property node to verify.
     * @return a new assertion instance for {@code actual}.
     * @see AbstractPathAssert#extractingPropertyNode(int)
     */
    public static AbstractPathAssert.AbstractPropertyNodeAssert<?> assertThatPropertyNode(final Path.PropertyNode actual) {
        return new DefaultPathAssert.DefaultNodeAssert.DefaultPropertyNodeAssert(actual);
    }

    /**
     * Creates a new assertion object for verifying specified return value node.
     *
     * @param actual the return value node to verify.
     * @return a new assertion instance for {@code actual}.
     * @see AbstractPathAssert#extractingReturnValueNode(int)
     */
    public static AbstractPathAssert.AbstractReturnValueNodeAssert<?> assertThatReturnValueNode(final Path.ReturnValueNode actual) {
        return new DefaultPathAssert.DefaultNodeAssert.DefaultReturnValueNodeAssert(actual);
    }

    /**
     * Creates a new instance.
     */
    private ValidationAssertions() {
        throw new AssertionError("instantiation is not allowed");
    }
}
