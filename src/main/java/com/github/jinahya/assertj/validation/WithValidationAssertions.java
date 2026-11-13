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
 * The entry points of {@link ValidationAssertions} as default methods, for a test class to implement instead of
 * importing them statically, as AssertJ's {@link org.assertj.core.api.WithAssertions} offers its own.
 * {@snippet lang = "java":
 * class UserTest implements WithValidationAssertions {
 *     void test() {
 *         assertThatBean(user).isValid();
 *     }
 * }
 *}
 *
 * @see ValidationAssertions
 */
public interface WithValidationAssertions {

    /**
     * Delegates to {@link ValidationAssertions#assertThatBean(Object)}.
     *
     * @param actual the value to verify.
     * @param <ACTUAL> type parameter
     * @return a new assertion object for verifying {@code actual}.
     */
    @CheckReturnValue
    default <ACTUAL> AbstractBeanAssert<?, ACTUAL> assertThatBean(final ACTUAL actual) {
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
    default <ACTUAL> AbstractPropertyAssert<?, ACTUAL> assertThatProperty(final ACTUAL actual) {
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
    default <T extends Annotation> AbstractConstraintDescriptorAssert<?, ?, T> assertThatConstraintDescriptor(
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
    default <T> AbstractConstraintViolationAssert<?, ConstraintViolation<T>, T> assertThatConstraintViolation(
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
    default <T> AbstractIterableOfConstraintViolationsAssert<?, T> assertThatIterableOfConstraintViolations(
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
    default <T> AbstractIterableOfConstraintViolationsAssert<?, T> assertThatConstraintViolations(
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
    default AbstractConstraintViolationExceptionAssert<?> assertThatConstraintViolationException(
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
    default AbstractPathAssert<?> assertThatPath(final Path actual) {
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
    default <T> AbstractConstructorAssert<?, T> assertThatConstructor(final Constructor<? extends T> actual) {
        return ValidationAssertions.assertThatConstructor(actual);
    }

    /**
     * Delegates to {@link ValidationAssertions#assertThatBeanDescriptor(BeanDescriptor)}.
     *
     * @param actual the value to verify.
     * @return a new assertion object for verifying {@code actual}.
     */
    @CheckReturnValue
    default AbstractBeanDescriptorAssert<?> assertThatBeanDescriptor(final BeanDescriptor actual) {
        return ValidationAssertions.assertThatBeanDescriptor(actual);
    }

    /**
     * Delegates to {@link ValidationAssertions#assertThatPropertyDescriptor(PropertyDescriptor)}.
     *
     * @param actual the value to verify.
     * @return a new assertion object for verifying {@code actual}.
     */
    @CheckReturnValue
    default AbstractPropertyDescriptorAssert<?> assertThatPropertyDescriptor(final PropertyDescriptor actual) {
        return ValidationAssertions.assertThatPropertyDescriptor(actual);
    }

    /**
     * Delegates to {@link ValidationAssertions#assertThatMethodDescriptor(MethodDescriptor)}.
     *
     * @param actual the value to verify.
     * @return a new assertion object for verifying {@code actual}.
     */
    @CheckReturnValue
    default AbstractMethodDescriptorAssert<?> assertThatMethodDescriptor(final MethodDescriptor actual) {
        return ValidationAssertions.assertThatMethodDescriptor(actual);
    }

    /**
     * Delegates to {@link ValidationAssertions#assertThatConstructorDescriptor(ConstructorDescriptor)}.
     *
     * @param actual the value to verify.
     * @return a new assertion object for verifying {@code actual}.
     */
    @CheckReturnValue
    default AbstractConstructorDescriptorAssert<?> assertThatConstructorDescriptor(final ConstructorDescriptor actual) {
        return ValidationAssertions.assertThatConstructorDescriptor(actual);
    }

    /**
     * Delegates to {@link ValidationAssertions#assertThatParameterDescriptor(ParameterDescriptor)}.
     *
     * @param actual the value to verify.
     * @return a new assertion object for verifying {@code actual}.
     */
    @CheckReturnValue
    default AbstractParameterDescriptorAssert<?> assertThatParameterDescriptor(final ParameterDescriptor actual) {
        return ValidationAssertions.assertThatParameterDescriptor(actual);
    }

    /**
     * Delegates to {@link ValidationAssertions#assertThatReturnValueDescriptor(ReturnValueDescriptor)}.
     *
     * @param actual the value to verify.
     * @return a new assertion object for verifying {@code actual}.
     */
    @CheckReturnValue
    default AbstractReturnValueDescriptorAssert<?> assertThatReturnValueDescriptor(final ReturnValueDescriptor actual) {
        return ValidationAssertions.assertThatReturnValueDescriptor(actual);
    }

    /**
     * Delegates to {@link ValidationAssertions#assertThatCrossParameterDescriptor(CrossParameterDescriptor)}.
     *
     * @param actual the value to verify.
     * @return a new assertion object for verifying {@code actual}.
     */
    @CheckReturnValue
    default AbstractCrossParameterDescriptorAssert<?> assertThatCrossParameterDescriptor(
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
    default AbstractContainerElementTypeDescriptorAssert<?> assertThatContainerElementTypeDescriptor(
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
    default AbstractGroupConversionDescriptorAssert<?> assertThatGroupConversionDescriptor(
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
    default AbstractPathAssert.AbstractNodeAssert<?> assertThatNode(final Path.Node actual) {
        return ValidationAssertions.assertThatNode(actual);
    }

    /**
     * Delegates to {@link ValidationAssertions#assertThatBeanNode(Path.BeanNode)}.
     *
     * @param actual the value to verify.
     * @return a new assertion object for verifying {@code actual}.
     */
    @CheckReturnValue
    default AbstractPathAssert.AbstractBeanNodeAssert<?> assertThatBeanNode(final Path.BeanNode actual) {
        return ValidationAssertions.assertThatBeanNode(actual);
    }

    /**
     * Delegates to {@link ValidationAssertions#assertThatConstructorNode(Path.ConstructorNode)}.
     *
     * @param actual the value to verify.
     * @return a new assertion object for verifying {@code actual}.
     */
    @CheckReturnValue
    default AbstractPathAssert.AbstractConstructorNodeAssert<?> assertThatConstructorNode(
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
    default AbstractPathAssert.AbstractContainerElementNodeAssert<?> assertThatContainerElementNode(
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
    default AbstractPathAssert.AbstractCrossParameterNodeAssert<?> assertThatCrossParameterNode(
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
    default AbstractPathAssert.AbstractMethodNodeAssert<?> assertThatMethodNode(final Path.MethodNode actual) {
        return ValidationAssertions.assertThatMethodNode(actual);
    }

    /**
     * Delegates to {@link ValidationAssertions#assertThatParameterNode(Path.ParameterNode)}.
     *
     * @param actual the value to verify.
     * @return a new assertion object for verifying {@code actual}.
     */
    @CheckReturnValue
    default AbstractPathAssert.AbstractParameterNodeAssert<?> assertThatParameterNode(final Path.ParameterNode actual) {
        return ValidationAssertions.assertThatParameterNode(actual);
    }

    /**
     * Delegates to {@link ValidationAssertions#assertThatPropertyNode(Path.PropertyNode)}.
     *
     * @param actual the value to verify.
     * @return a new assertion object for verifying {@code actual}.
     */
    @CheckReturnValue
    default AbstractPathAssert.AbstractPropertyNodeAssert<?> assertThatPropertyNode(final Path.PropertyNode actual) {
        return ValidationAssertions.assertThatPropertyNode(actual);
    }

    /**
     * Delegates to {@link ValidationAssertions#assertThatReturnValueNode(Path.ReturnValueNode)}.
     *
     * @param actual the value to verify.
     * @return a new assertion object for verifying {@code actual}.
     */
    @CheckReturnValue
    default AbstractPathAssert.AbstractReturnValueNodeAssert<?> assertThatReturnValueNode(
            final Path.ReturnValueNode actual) {
        return ValidationAssertions.assertThatReturnValueNode(actual);
    }
}
