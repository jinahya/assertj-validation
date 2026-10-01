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
        return new IterableOfConstraintViolationsAssert<>(actual);
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
    public static AbstractPathAssert<?, ?> assertThatPath(final Path actual) {
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

    /**
     * Creates a new instance.
     */
    private ValidationAssertions() {
        throw new AssertionError("instantiation is not allowed");
    }
}
