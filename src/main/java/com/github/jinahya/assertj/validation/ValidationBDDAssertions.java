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
import jakarta.validation.ConstraintViolationException;
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
 * BDD-style entry points: a {@code then…} for each {@code assertThat…} of {@link ValidationAssertions}, as AssertJ's
 * {@link org.assertj.core.api.BDDAssertions} offers {@code then(..)} beside {@code assertThat(..)}.
 * {@snippet lang = "java":
 * thenBean(user).isValid();
 *}
 *
 * @see ValidationAssertions
 */
public final class ValidationBDDAssertions {

    /**
     * Delegates to {@link ValidationAssertions#assertThatBean(Object)}.
     *
     * @param actual the value to verify.
     * @param <ACTUAL> type parameter
     * @return a new assertion object for verifying {@code actual}.
     */
    @CheckReturnValue
    public static <ACTUAL> AbstractBeanAssert<?, ACTUAL> thenBean(final ACTUAL actual) {
        return ValidationAssertions.assertThatBean(actual);
    }

    /**
     * Delegates to {@link ValidationAssertions#assertThatProperty(Object)}.
     *
     * @param actual the value to verify.
     * @param <ACTUAL> type parameter
     * @return a new assertion object for verifying {@code actual}.
     */
    @CheckReturnValue
    public static <ACTUAL> AbstractPropertyAssert<?, ACTUAL> thenProperty(final ACTUAL actual) {
        return ValidationAssertions.assertThatProperty(actual);
    }

    /**
     * Delegates to {@link ValidationAssertions#assertThatConstraintDescriptor(ConstraintDescriptor)}.
     *
     * @param actual the value to verify.
     * @param <T> type parameter
     * @return a new assertion object for verifying {@code actual}.
     */
    @CheckReturnValue
    public static <T extends Annotation> AbstractConstraintDescriptorAssert<?, ?, T> thenConstraintDescriptor(
            final ConstraintDescriptor<T> actual) {
        return ValidationAssertions.assertThatConstraintDescriptor(actual);
    }

    /**
     * Delegates to {@link ValidationAssertions#assertThatConstraintViolation(ConstraintViolation)}.
     *
     * @param actual the value to verify.
     * @param <T> type parameter
     * @return a new assertion object for verifying {@code actual}.
     */
    @CheckReturnValue
    public static <T> AbstractConstraintViolationAssert<?, ConstraintViolation<T>, T> thenConstraintViolation(
            final ConstraintViolation<T> actual) {
        return ValidationAssertions.assertThatConstraintViolation(actual);
    }

    /**
     * Delegates to {@link ValidationAssertions#assertThatIterableOfConstraintViolations(Iterable)}.
     *
     * @param actual the value to verify.
     * @param <T> type parameter
     * @return a new assertion object for verifying {@code actual}.
     */
    @CheckReturnValue
    public static <T> AbstractIterableOfConstraintViolationsAssert<?, T> thenIterableOfConstraintViolations(
            final Iterable<? extends ConstraintViolation<T>> actual) {
        return ValidationAssertions.assertThatIterableOfConstraintViolations(actual);
    }

    /**
     * Delegates to {@link ValidationAssertions#assertThatConstraintViolations(Set)}.
     *
     * @param actual the value to verify.
     * @param <T> type parameter
     * @return a new assertion object for verifying {@code actual}.
     */
    @CheckReturnValue
    public static <T> AbstractIterableOfConstraintViolationsAssert<?, T> thenConstraintViolations(
            final Set<? extends ConstraintViolation<T>> actual) {
        return ValidationAssertions.assertThatConstraintViolations(actual);
    }

    /**
     * Delegates to {@link ValidationAssertions#assertThatConstraintViolationException(ConstraintViolationException)}.
     *
     * @param actual the value to verify.
     * @return a new assertion object for verifying {@code actual}.
     */
    @CheckReturnValue
    public static AbstractConstraintViolationExceptionAssert<?> thenConstraintViolationException(
            final ConstraintViolationException actual) {
        return ValidationAssertions.assertThatConstraintViolationException(actual);
    }

    /**
     * Delegates to {@link ValidationAssertions#assertThatPath(Path)}.
     *
     * @param actual the value to verify.
     * @return a new assertion object for verifying {@code actual}.
     */
    @CheckReturnValue
    public static AbstractPathAssert<?> thenPath(final Path actual) {
        return ValidationAssertions.assertThatPath(actual);
    }

    /**
     * Delegates to {@link ValidationAssertions#assertThatConstructor(Constructor)}.
     *
     * @param actual the value to verify.
     * @param <T> type parameter
     * @return a new assertion object for verifying {@code actual}.
     */
    @CheckReturnValue
    public static <T> AbstractConstructorAssert<?, T> thenConstructor(final Constructor<? extends T> actual) {
        return ValidationAssertions.assertThatConstructor(actual);
    }

    /**
     * Delegates to {@link ValidationAssertions#assertThatBeanDescriptor(BeanDescriptor)}.
     *
     * @param actual the value to verify.
     * @return a new assertion object for verifying {@code actual}.
     */
    @CheckReturnValue
    public static AbstractBeanDescriptorAssert<?> thenBeanDescriptor(final BeanDescriptor actual) {
        return ValidationAssertions.assertThatBeanDescriptor(actual);
    }

    /**
     * Delegates to {@link ValidationAssertions#assertThatPropertyDescriptor(PropertyDescriptor)}.
     *
     * @param actual the value to verify.
     * @return a new assertion object for verifying {@code actual}.
     */
    @CheckReturnValue
    public static AbstractPropertyDescriptorAssert<?> thenPropertyDescriptor(final PropertyDescriptor actual) {
        return ValidationAssertions.assertThatPropertyDescriptor(actual);
    }

    /**
     * Delegates to {@link ValidationAssertions#assertThatMethodDescriptor(MethodDescriptor)}.
     *
     * @param actual the value to verify.
     * @return a new assertion object for verifying {@code actual}.
     */
    @CheckReturnValue
    public static AbstractMethodDescriptorAssert<?> thenMethodDescriptor(final MethodDescriptor actual) {
        return ValidationAssertions.assertThatMethodDescriptor(actual);
    }

    /**
     * Delegates to {@link ValidationAssertions#assertThatConstructorDescriptor(ConstructorDescriptor)}.
     *
     * @param actual the value to verify.
     * @return a new assertion object for verifying {@code actual}.
     */
    @CheckReturnValue
    public static AbstractConstructorDescriptorAssert<?> thenConstructorDescriptor(final ConstructorDescriptor actual) {
        return ValidationAssertions.assertThatConstructorDescriptor(actual);
    }

    /**
     * Delegates to {@link ValidationAssertions#assertThatParameterDescriptor(ParameterDescriptor)}.
     *
     * @param actual the value to verify.
     * @return a new assertion object for verifying {@code actual}.
     */
    @CheckReturnValue
    public static AbstractParameterDescriptorAssert<?> thenParameterDescriptor(final ParameterDescriptor actual) {
        return ValidationAssertions.assertThatParameterDescriptor(actual);
    }

    /**
     * Delegates to {@link ValidationAssertions#assertThatReturnValueDescriptor(ReturnValueDescriptor)}.
     *
     * @param actual the value to verify.
     * @return a new assertion object for verifying {@code actual}.
     */
    @CheckReturnValue
    public static AbstractReturnValueDescriptorAssert<?> thenReturnValueDescriptor(final ReturnValueDescriptor actual) {
        return ValidationAssertions.assertThatReturnValueDescriptor(actual);
    }

    /**
     * Delegates to {@link ValidationAssertions#assertThatCrossParameterDescriptor(CrossParameterDescriptor)}.
     *
     * @param actual the value to verify.
     * @return a new assertion object for verifying {@code actual}.
     */
    @CheckReturnValue
    public static AbstractCrossParameterDescriptorAssert<?> thenCrossParameterDescriptor(
            final CrossParameterDescriptor actual) {
        return ValidationAssertions.assertThatCrossParameterDescriptor(actual);
    }

    /**
     * Delegates to {@link ValidationAssertions#assertThatContainerElementTypeDescriptor(ContainerElementTypeDescriptor)}.
     *
     * @param actual the value to verify.
     * @return a new assertion object for verifying {@code actual}.
     */
    @CheckReturnValue
    public static AbstractContainerElementTypeDescriptorAssert<?> thenContainerElementTypeDescriptor(
            final ContainerElementTypeDescriptor actual) {
        return ValidationAssertions.assertThatContainerElementTypeDescriptor(actual);
    }

    /**
     * Delegates to {@link ValidationAssertions#assertThatGroupConversionDescriptor(GroupConversionDescriptor)}.
     *
     * @param actual the value to verify.
     * @return a new assertion object for verifying {@code actual}.
     */
    @CheckReturnValue
    public static AbstractGroupConversionDescriptorAssert<?> thenGroupConversionDescriptor(
            final GroupConversionDescriptor actual) {
        return ValidationAssertions.assertThatGroupConversionDescriptor(actual);
    }

    /**
     * Delegates to {@link ValidationAssertions#assertThatNode(Path.Node)}.
     *
     * @param actual the value to verify.
     * @return a new assertion object for verifying {@code actual}.
     */
    @CheckReturnValue
    public static AbstractPathAssert.AbstractNodeAssert<?> thenNode(final Path.Node actual) {
        return ValidationAssertions.assertThatNode(actual);
    }

    /**
     * Delegates to {@link ValidationAssertions#assertThatBeanNode(Path.BeanNode)}.
     *
     * @param actual the value to verify.
     * @return a new assertion object for verifying {@code actual}.
     */
    @CheckReturnValue
    public static AbstractPathAssert.AbstractBeanNodeAssert<?> thenBeanNode(final Path.BeanNode actual) {
        return ValidationAssertions.assertThatBeanNode(actual);
    }

    /**
     * Delegates to {@link ValidationAssertions#assertThatConstructorNode(Path.ConstructorNode)}.
     *
     * @param actual the value to verify.
     * @return a new assertion object for verifying {@code actual}.
     */
    @CheckReturnValue
    public static AbstractPathAssert.AbstractConstructorNodeAssert<?> thenConstructorNode(
            final Path.ConstructorNode actual) {
        return ValidationAssertions.assertThatConstructorNode(actual);
    }

    /**
     * Delegates to {@link ValidationAssertions#assertThatContainerElementNode(Path.ContainerElementNode)}.
     *
     * @param actual the value to verify.
     * @return a new assertion object for verifying {@code actual}.
     */
    @CheckReturnValue
    public static AbstractPathAssert.AbstractContainerElementNodeAssert<?> thenContainerElementNode(
            final Path.ContainerElementNode actual) {
        return ValidationAssertions.assertThatContainerElementNode(actual);
    }

    /**
     * Delegates to {@link ValidationAssertions#assertThatCrossParameterNode(Path.CrossParameterNode)}.
     *
     * @param actual the value to verify.
     * @return a new assertion object for verifying {@code actual}.
     */
    @CheckReturnValue
    public static AbstractPathAssert.AbstractCrossParameterNodeAssert<?> thenCrossParameterNode(
            final Path.CrossParameterNode actual) {
        return ValidationAssertions.assertThatCrossParameterNode(actual);
    }

    /**
     * Delegates to {@link ValidationAssertions#assertThatMethodNode(Path.MethodNode)}.
     *
     * @param actual the value to verify.
     * @return a new assertion object for verifying {@code actual}.
     */
    @CheckReturnValue
    public static AbstractPathAssert.AbstractMethodNodeAssert<?> thenMethodNode(final Path.MethodNode actual) {
        return ValidationAssertions.assertThatMethodNode(actual);
    }

    /**
     * Delegates to {@link ValidationAssertions#assertThatParameterNode(Path.ParameterNode)}.
     *
     * @param actual the value to verify.
     * @return a new assertion object for verifying {@code actual}.
     */
    @CheckReturnValue
    public static AbstractPathAssert.AbstractParameterNodeAssert<?> thenParameterNode(final Path.ParameterNode actual) {
        return ValidationAssertions.assertThatParameterNode(actual);
    }

    /**
     * Delegates to {@link ValidationAssertions#assertThatPropertyNode(Path.PropertyNode)}.
     *
     * @param actual the value to verify.
     * @return a new assertion object for verifying {@code actual}.
     */
    @CheckReturnValue
    public static AbstractPathAssert.AbstractPropertyNodeAssert<?> thenPropertyNode(final Path.PropertyNode actual) {
        return ValidationAssertions.assertThatPropertyNode(actual);
    }

    /**
     * Delegates to {@link ValidationAssertions#assertThatReturnValueNode(Path.ReturnValueNode)}.
     *
     * @param actual the value to verify.
     * @return a new assertion object for verifying {@code actual}.
     */
    @CheckReturnValue
    public static AbstractPathAssert.AbstractReturnValueNodeAssert<?> thenReturnValueNode(
            final Path.ReturnValueNode actual) {
        return ValidationAssertions.assertThatReturnValueNode(actual);
    }

    private ValidationBDDAssertions() {
        throw new AssertionError("instantiation is not allowed");
    }
}
