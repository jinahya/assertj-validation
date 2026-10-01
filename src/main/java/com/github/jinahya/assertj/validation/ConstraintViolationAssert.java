package com.github.jinahya.assertj.validation;

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

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.AbstractClassAssert;
import org.assertj.core.api.AbstractObjectArrayAssert;
import org.assertj.core.api.Assert;
import org.assertj.core.api.AssertFactory;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.assertj.core.api.ObjectArrayAssert;
import org.assertj.core.api.ObjectAssertFactory;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.metadata.ConstraintDescriptor;
import java.util.Objects;
import java.util.function.Function;

/**
 * An assertion interface for verifying {@link ConstraintViolation} values.
 *
 * @param <SELF>   self type parameter
 * @param <ACTUAL> actual type parameter
 * @param <T>      root bean type parameter
 */
@SuppressWarnings({
        "java:S119" // <SELF ...>
})
public interface ConstraintViolationAssert<
        SELF extends ConstraintViolationAssert<SELF, ACTUAL, T>,
        ACTUAL extends ConstraintViolation<T>,
        T>
        extends Assert<SELF, ACTUAL> {

    // -------------------------------------------------------------------------------------------- constraintDescriptor
    <D extends ConstraintDescriptor<?>, A extends AbstractConstraintDescriptorAssert<?, ? extends D, ?>>
    A extractingConstraintDescriptor(
            Function<? super ACTUAL, ? extends D> descriptorExtractor,
            AssertFactory<? super D, ? extends A> assertFactory);

    default ConstraintDescriptorAssert<?, ?, ?> extractingConstraintDescriptor() {
        return extractingConstraintDescriptor(
                ConstraintViolation::getConstraintDescriptor,
                // do not change following line into a method reference!!!
                (ConstraintDescriptor<?> cd) -> ValidationAssertions.assertThatConstraintDescriptor(cd)
        );
    }

    // -------------------------------------------------------------------------------------------- executableParameters
    <A extends AbstractObjectArrayAssert<?, ? extends E>, E> A extractingExecutableParameters(
            final Function<? super ACTUAL, ? extends E[]> parametersExtractor,
            final AssertFactory<? super E[], ? extends A> assertFactory);

    default ObjectArrayAssert<Object> extractingExecutableParameters() {
        return extractingExecutableParameters(ConstraintViolation::getExecutableParameters,
                                              InstanceOfAssertFactories.ARRAY);
    }

    /**
     * Verifies that the {@link ConstraintViolation#getExecutableParameters() actual.executableParameters} is equal to
     * specified value.
     *
     * @param expectedExecutableParameters the expected value of {@code actual.executableParameters}.
     * @return this assertion object.
     * @see ConstraintViolation#getExecutableParameters()
     * @see #extractingExecutableParameters()
     */
    @SuppressWarnings({
            "unchecked"
    })
    default SELF hasExecutableParameters(final Object[] expectedExecutableParameters) {
        extractingExecutableParameters().isEqualTo(expectedExecutableParameters);
        return (SELF) this;
    }

    default SELF hasNoExecutableParameters() {
        return hasExecutableParameters(null);
    }

    // ------------------------------------------------------------------------------------------- executableReturnValue
    <V, A extends AbstractAssert<?, ? extends V>> A extractingExecutableReturnValue(
            final Function<? super ACTUAL, ? extends V> valueExtractor,
            final AssertFactory<? super V, ? extends A> assertFactory);

    default Assert<?, Object> extractingExecutableReturnValue() {
        return extractingExecutableReturnValue(
                ConstraintViolation::getExecutableReturnValue,
                new ObjectAssertFactory<>()
        );
    }

    /**
     * Verifies that the {@link ConstraintViolation#getExecutableReturnValue() actual.executableReturnValue} is
     * {@link Assert#isEqualTo(Object) equal} to specified value.
     *
     * @param expectedExecutableReturnValue the expected value of
     *                                      {@link ConstraintViolation#getExecutableReturnValue()
     *                                      actual.executableReturnValue}.
     * @return this assertion object.
     * @see ConstraintViolation#getExecutableReturnValue()
     */
    @SuppressWarnings({
            "unchecked"
    })
    default SELF hasExecutableReturnValue(final Object expectedExecutableReturnValue) {
        extractingExecutableReturnValue().isEqualTo(expectedExecutableReturnValue);
        return (SELF) this;
    }

    /**
     * Verifies that the {@link ConstraintViolation#getExecutableReturnValue() actual.executableReturnValue} is
     * {@code null}.
     *
     * @return this assertion object.
     * @apiNote Default implementation invokes {@link #hasExecutableReturnValue(Object)} method with {@code null}, and
     * returns the result.
     * @see ConstraintViolation#getExecutableReturnValue()
     */
    default SELF hasNoExecutableReturnValue() {
        return hasExecutableReturnValue(null);
    }

    // ---------------------------------------------------------------------------------------------------- invalidValue
    <INVALID_VALUE, ASSERT extends AbstractAssert<?, ? extends INVALID_VALUE>> ASSERT extractingInvalidValue(
            final Function<? super ACTUAL, ? extends INVALID_VALUE> valueExtractor,
            final AssertFactory<? super INVALID_VALUE, ? extends ASSERT> assertFactory);

    default Assert<?, Object> extractingInvalidValue() {
        return extractingInvalidValue(ConstraintViolation::getInvalidValue, new ObjectAssertFactory<>());
    }

    /**
     * Verifies that the {@code actual} constraint violation's
     * {@link ConstraintViolation#getInvalidValue() invalidValue} is equal to specified value.
     *
     * @param expectedInvalidValue the expected value of
     *                             {@link ConstraintViolation#getInvalidValue() actual.invalidValue}.
     * @return this assertion object.
     * @see ConstraintViolation#getInvalidValue()
     */
    @SuppressWarnings({
            "unchecked"
    })
    default SELF hasInvalidValue(final Object expectedInvalidValue) {
        extractingInvalidValue().isEqualTo(expectedInvalidValue);
        return (SELF) this;
    }

    // -------------------------------------------------------------------------------------------------------- leafBean
    <LEAF_BEAN, ASSERT extends AbstractAssert<?, ? extends LEAF_BEAN>> ASSERT extractingLeafBean(
            final Function<? super ACTUAL, ? extends LEAF_BEAN> beanExtractor,
            final AssertFactory<? super LEAF_BEAN, ? extends ASSERT> assertFactory);

    @SuppressWarnings({
            "unchecked"
    })
    default <LEAF_BEAN, ASSERT extends AbstractAssert<?, ? extends LEAF_BEAN>> ASSERT extractingLeafBean(
            final AssertFactory<? super LEAF_BEAN, ? extends ASSERT> assertFactory) {
        return extractingLeafBean(a -> (LEAF_BEAN) a.getLeafBean(), assertFactory);
    }

    /**
     * Returns an assert for verifying the {@code actual} constraint violation's
     * {@link ConstraintViolation#getRootBean() rootBean}.
     *
     * @return an assert for verifying the {@code actual} constraint violation's
     * {@link ConstraintViolation#getRootBean() rootBean}.
     */
    Assert<?, ?> extractingLeafBean();

    /**
     * Verifies that the {@link ConstraintViolation#getLeafBean() actual.leafBean} is equal to specified value.
     *
     * @param expectedLeafBean the expected value of {@code actual.leafBean}.
     * @return this assertion object.
     * @see ConstraintViolation#getLeafBean()
     * @see #extractingLeafBean()
     */
    @SuppressWarnings({
            "unchecked"
    })
    default SELF hasLeafBean(final Object expectedLeafBean) {
        extractingLeafBean().isEqualTo(expectedLeafBean);
        return (SELF) this;
    }

    /**
     * Verifies that the {@code actual} constraint violation has no {@link ConstraintViolation#getLeafBean() leafBean}.
     *
     * @return this assertion object.
     * @apiNote Default implementation invokes {@link #hasLeafBean(Object)} method with {@code null}, and returns the
     * result.
     */
    default SELF doesNotHaveLeafBean() {
        return hasLeafBean(null);
    }

    // --------------------------------------------------------------------------------------------------------- message
    SELF hasMessage(final String expectedMessage);

    // ---------------------------------------------------------------------------------------------------- propertyPath
    <ASSERT extends AbstractPathAssert<?, ? extends AbstractPathAssert.AbstractNodeAssert<?>>> ASSERT extractingPropertyPath(
            final AssertFactory<? super Path, ? extends ASSERT> assertFactory);

    default AbstractPathAssert<?, ? extends AbstractPathAssert.AbstractNodeAssert<?>> extractingPropertyPath() {
        return extractingPropertyPath(pp -> new DefaultPathAssert(pp));
    }

    // -------------------------------------------------------------------------------------------------------- rootBean
    <ASSERT extends AbstractAssert<?, T>> ASSERT extractingRootBean(
            final AssertFactory<? super T, ? extends ASSERT> assertFactory);

    default AbstractAssert<?, T> extractingRootBean() {
        return extractingRootBean(new ObjectAssertFactory<>());
    }

    /**
     * Verifies that the {@link ConstraintViolation#getRootBean() actual.rootBean} is equal to specified value.
     *
     * @param expectedRootBean the expected value of {@code actual.rootBean}.
     * @return this assertion object.
     * @see ConstraintViolation#getRootBean()
     */
    @SuppressWarnings({
            "unchecked"
    })
    default SELF hasRootBean(final T expectedRootBean) {
        extractingRootBean().isEqualTo(expectedRootBean);
        return (SELF) this;
    }

    /**
     * Verifies that the {@code actual} constraint violation has no {@link ConstraintViolation#getRootBean() rootBean}.
     *
     * @return this assertion object.
     * @apiNote This method is equivalent to invoking {@link #hasRootBean(Object)} method with {@code null}.
     * @apiNote Default implementation invokes {@link #hasRootBean(Object)} method with {@code null}, and returns the
     * result.
     */
    default SELF doesNotHaveRootBean() {
        return hasRootBean(null);
    }

    // --------------------------------------------------------------------------------------------------- rootBeanClass

    /**
     * Extracts an assertion for verifying {@code actual.rootBeanClass} using specified assertion factory.
     *
     * @param assertFactory the assertion factory.
     * @param <ASSERT>      assert type parameter
     * @return in instance of {@link ASSERT}.
     */
    <ASSERT extends AbstractClassAssert<?>> ASSERT extractingRootBeanClass(
            final AssertFactory<? super Class<T>, ? extends ASSERT> assertFactory);

    /**
     * Extracts an assertion for verifying {@code actual.rootBeanClass}.
     *
     * @return a class assert.
     */
    default AbstractClassAssert<?> extractingRootBeanClass() {
        return extractingRootBeanClass(InstanceOfAssertFactories.CLASS);
    }

    /**
     * Verifies that the {@code actual} constraint violation has specified class as its
     * {@link ConstraintViolation#getRootBeanClass() rootBeanClass}.
     *
     * @param expectedRootBeanClass the expected value of {@code actual.rootBeanClass}.
     * @return this assertion object.
     * @see ConstraintViolation#getRootBeanClass()
     */
    @SuppressWarnings({
            "unchecked"
    })
    default SELF hasRootBeanClass(final Class<T> expectedRootBeanClass) {
        Objects.requireNonNull(expectedRootBeanClass, "expectedRootBeanClass is null");
        extractingRootBeanClass().isEqualTo(expectedRootBeanClass);
        return (SELF) this;
    }
}
