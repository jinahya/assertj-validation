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

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.metadata.ConstraintDescriptor;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.AbstractClassAssert;
import org.assertj.core.api.AbstractObjectArrayAssert;
import org.assertj.core.api.Assert;
import org.assertj.core.api.AssertFactory;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.ObjectArrayAssert;
import org.assertj.core.api.ObjectAssertFactory;

import java.util.Objects;
import java.util.function.Function;

/**
 * An abstract assertion class for verifying {@link ConstraintViolation} values.
 * <p>
 * Unlike {@link AbstractBeanAssert} and its siblings, this does <em>not</em> extend
 * {@link AbstractValidationAssert}: a {@link ConstraintViolation} is a <em>result</em> of validation, so there is
 * nothing left to validate and no {@link jakarta.validation.Validator} to configure. Inheriting
 * {@code usingValidator} and {@code targetingGroups} here would offer settings that could not affect
 * anything.
 *
 * @param <SELF>   self type parameter
 * @param <ACTUAL> actual type parameter
 * @param <T>      root bean type parameter
 */
@SuppressWarnings({
        "java:S119" // <SELF ...>
})
public abstract class AbstractConstraintViolationAssert<
        SELF extends AbstractConstraintViolationAssert<SELF, ACTUAL, T>, ACTUAL extends ConstraintViolation<T>, T>
        extends AbstractAssert<SELF, ACTUAL> {

    /**
     * Creates a new instance with specified arguments.
     *
     * @param actual   an actual value to verify.
     * @param selfType self type.
     */
    protected AbstractConstraintViolationAssert(final ACTUAL actual, final Class<?> selfType) {
        super(actual, selfType);
    }

    // -------------------------------------------------------------------------------------------- constraintDescriptor

    /**
     * Extracts an assertion for verifying the {@code actual} constraint violation's
     * {@link ConstraintViolation#getConstraintDescriptor() constraintDescriptor}, using specified extractor and
     * assertion factory.
     *
     * @param descriptorExtractor the extractor for the constraint descriptor.
     * @param assertFactory       the assertion factory.
     * @param <D>                 constraint descriptor type parameter
     * @param <A>                 assertion type parameter
     * @return an instance of {@link A}.
     * @see ConstraintViolation#getConstraintDescriptor()
     */
    public <D extends ConstraintDescriptor<?>, A extends AbstractConstraintDescriptorAssert<?, ? extends D, ?>>
    A extractingConstraintDescriptor(
            final Function<? super ACTUAL, ? extends D> descriptorExtractor,
            final AssertFactory<? super D, ? extends A> assertFactory) {
        return isNotNull().extracting(descriptorExtractor, assertFactory);
    }

    /**
     * Extracts an assertion for verifying the {@code actual} constraint violation's
     * {@link ConstraintViolation#getConstraintDescriptor() constraintDescriptor}.
     *
     * @return an assertion for the {@code actual} constraint violation's constraint descriptor.
     * @see ConstraintViolation#getConstraintDescriptor()
     */
    public AbstractConstraintDescriptorAssert<?, ?, ?> extractingConstraintDescriptor() {
        return extractingConstraintDescriptor(
                ConstraintViolation::getConstraintDescriptor,
                // do not change following line into a method reference!!!
                (ConstraintDescriptor<?> cd) -> ValidationAssertions.assertThatConstraintDescriptor(cd)
        );
    }

    // -------------------------------------------------------------------------------------------- executableParameters

    /**
     * Extracts an assertion for verifying the {@code actual} constraint violation's
     * {@link ConstraintViolation#getExecutableParameters() executableParameters}, using specified extractor and
     * assertion factory.
     *
     * @param parametersExtractor the extractor for the executable parameters.
     * @param assertFactory       the assertion factory.
     * @param <A>                 assertion type parameter
     * @param <E>                 element type parameter
     * @return an instance of {@link A}.
     * @see ConstraintViolation#getExecutableParameters()
     */
    public <A extends AbstractObjectArrayAssert<?, ? extends E>, E> A extractingExecutableParameters(
            final Function<? super ACTUAL, ? extends E[]> parametersExtractor,
            final AssertFactory<? super E[], ? extends A> assertFactory) {
        return isNotNull()
                .extracting(parametersExtractor, assertFactory);
    }

    /**
     * Extracts an assertion for verifying the {@code actual} constraint violation's
     * {@link ConstraintViolation#getExecutableParameters() executableParameters}.
     *
     * @return an object array assertion for the {@code actual} constraint violation's executable parameters.
     * @see ConstraintViolation#getExecutableParameters()
     */
    public ObjectArrayAssert<Object> extractingExecutableParameters() {
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
    public SELF hasExecutableParameters(final Object[] expectedExecutableParameters) {
        extractingExecutableParameters().isEqualTo(expectedExecutableParameters);
        return myself;
    }

    /**
     * Verifies that the {@code actual} constraint violation has no
     * {@link ConstraintViolation#getExecutableParameters() executableParameters}.
     *
     * @return this assertion object.
     * @apiNote This method invokes {@link #hasExecutableParameters(Object[])} method with {@code null}, and returns the
     * result.
     * @see ConstraintViolation#getExecutableParameters()
     */
    public SELF hasNoExecutableParameters() {
        return hasExecutableParameters(null);
    }

    // ------------------------------------------------------------------------------------------- executableReturnValue

    /**
     * Extracts an assertion for verifying the {@code actual} constraint violation's
     * {@link ConstraintViolation#getExecutableReturnValue() executableReturnValue}, using specified extractor and
     * assertion factory.
     *
     * @param valueExtractor the extractor for the executable return value.
     * @param assertFactory  the assertion factory.
     * @param <V>            value type parameter
     * @param <A>            assertion type parameter
     * @return an instance of {@link A}.
     * @see ConstraintViolation#getExecutableReturnValue()
     */
    public <V, A extends AbstractAssert<?, ? extends V>> A extractingExecutableReturnValue(
            final Function<? super ACTUAL, ? extends V> valueExtractor,
            final AssertFactory<? super V, ? extends A> assertFactory) {
        return isNotNull().extracting(valueExtractor, assertFactory);
    }

    /**
     * Extracts an assertion for verifying the {@code actual} constraint violation's
     * {@link ConstraintViolation#getExecutableReturnValue() executableReturnValue}.
     *
     * @return an assertion for the {@code actual} constraint violation's executable return value.
     * @see ConstraintViolation#getExecutableReturnValue()
     */
    public Assert<?, Object> extractingExecutableReturnValue() {
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
    public SELF hasExecutableReturnValue(final Object expectedExecutableReturnValue) {
        extractingExecutableReturnValue().isEqualTo(expectedExecutableReturnValue);
        return myself;
    }

    /**
     * Verifies that the {@link ConstraintViolation#getExecutableReturnValue() actual.executableReturnValue} is
     * {@code null}.
     *
     * @return this assertion object.
     * @apiNote This method invokes {@link #hasExecutableReturnValue(Object)} method with {@code null}, and returns the
     * result.
     * @see ConstraintViolation#getExecutableReturnValue()
     */
    public SELF hasNoExecutableReturnValue() {
        return hasExecutableReturnValue(null);
    }

    // ---------------------------------------------------------------------------------------------------- invalidValue

    /**
     * Extracts an assertion for verifying the {@code actual} constraint violation's
     * {@link ConstraintViolation#getInvalidValue() invalidValue}, using specified extractor and assertion factory.
     *
     * @param valueExtractor the extractor for the invalid value.
     * @param assertFactory  the assertion factory.
     * @param <V>            value type parameter
     * @param <A>            assertion type parameter
     * @return an instance of {@link A}.
     * @see ConstraintViolation#getInvalidValue()
     */
    public <V, A extends AbstractAssert<?, ? extends V>> A extractingInvalidValue(
            final Function<? super ACTUAL, ? extends V> valueExtractor,
            final AssertFactory<? super V, ? extends A> assertFactory) {
        return isNotNull()
                .extracting(valueExtractor, assertFactory);
    }

    /**
     * Extracts an assertion for verifying the {@code actual} constraint violation's
     * {@link ConstraintViolation#getInvalidValue() invalidValue}.
     *
     * @return an assertion for the {@code actual} constraint violation's invalid value.
     * @see ConstraintViolation#getInvalidValue()
     */
    public Assert<?, Object> extractingInvalidValue() {
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
    public SELF hasInvalidValue(final Object expectedInvalidValue) {
        extractingInvalidValue().isEqualTo(expectedInvalidValue);
        return myself;
    }

    // -------------------------------------------------------------------------------------------------------- leafBean

    /**
     * Extracts an assertion for verifying the {@code actual} constraint violation's
     * {@link ConstraintViolation#getLeafBean() leafBean}, using specified extractor and assertion factory.
     *
     * @param beanExtractor the extractor for the leaf bean.
     * @param assertFactory the assertion factory.
     * @param <B>           leaf bean type parameter
     * @param <A>           assertion type parameter
     * @return an instance of {@link A}.
     * @see ConstraintViolation#getLeafBean()
     */
    public <B, A extends AbstractAssert<?, ? extends B>> A extractingLeafBean(
            final Function<? super ACTUAL, ? extends B> beanExtractor,
            final AssertFactory<? super B, ? extends A> assertFactory) {
        return isNotNull()
                .extracting(beanExtractor, assertFactory);
    }

    /**
     * Extracts an assertion for verifying the {@code actual} constraint violation's
     * {@link ConstraintViolation#getLeafBean() leafBean}, using specified assertion factory.
     *
     * @param assertFactory the assertion factory.
     * @param <B>           leaf bean type parameter
     * @param <A>           assertion type parameter
     * @return an instance of {@link A}.
     * @see ConstraintViolation#getLeafBean()
     */
    @SuppressWarnings({
            "unchecked"
    })
    public <B, A extends AbstractAssert<?, ? extends B>> A extractingLeafBean(
            final AssertFactory<? super B, ? extends A> assertFactory) {
        return extractingLeafBean(a -> (B) a.getLeafBean(), assertFactory);
    }

    /**
     * Returns an assert for verifying the {@code actual} constraint violation's
     * {@link ConstraintViolation#getLeafBean() leafBean}.
     *
     * @return an assert for verifying the {@code actual} constraint violation's
     * {@link ConstraintViolation#getLeafBean() leafBean}.
     */
    public Assert<?, ?> extractingLeafBean() {
        return isNotNull()
                .extracting(ConstraintViolation::getLeafBean, new ObjectAssertFactory<>());
    }

    /**
     * Verifies that the {@link ConstraintViolation#getLeafBean() actual.leafBean} is equal to specified value.
     *
     * @param expectedLeafBean the expected value of {@code actual.leafBean}.
     * @return this assertion object.
     * @see ConstraintViolation#getLeafBean()
     * @see #extractingLeafBean()
     */
    public SELF hasLeafBean(final Object expectedLeafBean) {
        extractingLeafBean().isEqualTo(expectedLeafBean);
        return myself;
    }

    /**
     * Verifies that the {@code actual} constraint violation has no {@link ConstraintViolation#getLeafBean() leafBean}.
     *
     * @return this assertion object.
     * @apiNote This method invokes {@link #hasLeafBean(Object)} method with {@code null}, and returns the result.
     */
    public SELF doesNotHaveLeafBean() {
        return hasLeafBean(null);
    }

    // --------------------------------------------------------------------------------------------------------- message

    /**
     * Verifies that the {@link ConstraintViolation#getMessage() actual.message} is equal to specified value.
     *
     * @param expectedMessage the expected value of {@code actual.message}.
     * @return this assertion object.
     * @see ConstraintViolation#getMessage()
     */
    public SELF hasMessage(final String expectedMessage) {
        isNotNull()
                .extracting(ConstraintViolation::getMessage, InstanceOfAssertFactories.STRING)
                .isEqualTo(expectedMessage);
        return myself;
    }

    // ------------------------------------------------------------------------------------------------- messageTemplate

    /**
     * Extracts an assertion for verifying the {@code actual} constraint violation's
     * {@link ConstraintViolation#getMessageTemplate() messageTemplate} &mdash; the <em>non-interpolated</em> message,
     * using specified assertion factory.
     *
     * @param assertFactory the assertion factory.
     * @param <A>           assertion type parameter
     * @return an instance of {@link A}.
     * @see ConstraintViolation#getMessageTemplate()
     */
    public <A extends AbstractStringAssert<?>> A extractingMessageTemplate(
            final AssertFactory<? super String, ? extends A> assertFactory) {
        return isNotNull()
                .extracting(ConstraintViolation::getMessageTemplate, assertFactory);
    }

    /**
     * Extracts an assertion for verifying the {@code actual} constraint violation's
     * {@link ConstraintViolation#getMessageTemplate() messageTemplate}.
     *
     * @return a string assertion for the {@code actual} constraint violation's message template.
     * @see ConstraintViolation#getMessageTemplate()
     */
    public AbstractStringAssert<?> extractingMessageTemplate() {
        return extractingMessageTemplate(InstanceOfAssertFactories.STRING);
    }

    /**
     * Verifies that the {@link ConstraintViolation#getMessageTemplate() actual.messageTemplate} is equal to specified
     * value.
     *
     * @param expectedMessageTemplate the expected value of {@code actual.messageTemplate}, e.g.
     *                                {@code "{jakarta.validation.constraints.NotNull.message}"}.
     * @return this assertion object.
     * @apiNote Unlike {@link #hasMessage(String)}, which asserts the <em>interpolated</em> message and is therefore
     * sensitive to the default locale and to any {@link jakarta.validation.MessageInterpolator}, this method asserts
     * the raw template as declared on the constraint.
     * @see ConstraintViolation#getMessageTemplate()
     * @see #hasMessage(String)
     */
    public SELF hasMessageTemplate(final String expectedMessageTemplate) {
        extractingMessageTemplate().isEqualTo(expectedMessageTemplate);
        return myself;
    }

    // ---------------------------------------------------------------------------------------------------------- unwrap

    /**
     * Extracts an assertion for verifying the {@code actual} constraint violation
     * {@link ConstraintViolation#unwrap(Class) unwrapped} as specified type, using specified assertion factory.
     *
     * @param type          the type to unwrap as.
     * @param assertFactory the assertion factory.
     * @param <U>           the type of the unwrapped object.
     * @param <A>           assertion type parameter
     * @return an instance of {@link A}.
     * @see ConstraintViolation#unwrap(Class)
     */
    public <U, A extends AbstractAssert<?, ? extends U>> A extractingAsUnwrapped(
            final Class<U> type,
            final AssertFactory<? super U, ? extends A> assertFactory) {
        Objects.requireNonNull(type, "type is null");
        return isNotNull()
                .extracting(a -> a.unwrap(type), assertFactory);
    }

    /**
     * Verifies that the {@code actual} constraint violation is equal to specified value when
     * {@link ConstraintViolation#unwrap(Class) unwrapped} as specified type.
     *
     * @param type                   the type to unwrap as.
     * @param expectedUnwrappedValue the expected unwrapped value.
     * @param <U>                    the type of the unwrapped object.
     * @return this assertion object.
     * @see ConstraintViolation#unwrap(Class)
     */
    public <U> SELF isEqualToWhenUnwrappedAs(final Class<U> type, final Object expectedUnwrappedValue) {
        extractingAsUnwrapped(type, new ObjectAssertFactory<>())
                .isEqualTo(expectedUnwrappedValue);
        return myself;
    }

    // ---------------------------------------------------------------------------------------------------- propertyPath

    /**
     * Extracts an assertion for verifying the {@code actual} constraint violation's
     * {@link ConstraintViolation#getPropertyPath() propertyPath}, using specified assertion factory.
     *
     * @param assertFactory the assertion factory.
     * @param <ASSERT>      assertion type parameter
     * @return an instance of {@link ASSERT}.
     * @see ConstraintViolation#getPropertyPath()
     */
    public <ASSERT extends AbstractPathAssert<?, ? extends AbstractPathAssert.AbstractNodeAssert<?>>>
    ASSERT extractingPropertyPath(final AssertFactory<? super Path, ? extends ASSERT> assertFactory) {
        return isNotNull()
                .extracting(ConstraintViolation::getPropertyPath, assertFactory::createAssert);
    }

    /**
     * Extracts an assertion for verifying the {@code actual} constraint violation's
     * {@link ConstraintViolation#getPropertyPath() propertyPath}.
     *
     * @return a path assertion for the {@code actual} constraint violation's property path.
     * @see ConstraintViolation#getPropertyPath()
     */
    public AbstractPathAssert<?, ? extends AbstractPathAssert.AbstractNodeAssert<?>> extractingPropertyPath() {
        return extractingPropertyPath(pp -> new DefaultPathAssert(pp));
    }

    // -------------------------------------------------------------------------------------------------------- rootBean

    /**
     * Extracts an assertion for verifying the {@code actual} constraint violation's
     * {@link ConstraintViolation#getRootBean() rootBean}, using specified assertion factory.
     *
     * @param assertFactory the assertion factory.
     * @param <ASSERT>      assertion type parameter
     * @return an instance of {@link ASSERT}.
     * @see ConstraintViolation#getRootBean()
     */
    public <ASSERT extends AbstractAssert<?, T>> ASSERT extractingRootBean(
            final AssertFactory<? super T, ? extends ASSERT> assertFactory) {
        return isNotNull()
                .extracting(ConstraintViolation::getRootBean, assertFactory);
    }

    /**
     * Extracts an assertion for verifying the {@code actual} constraint violation's
     * {@link ConstraintViolation#getRootBean() rootBean}.
     *
     * @return an assertion for the {@code actual} constraint violation's root bean.
     * @see ConstraintViolation#getRootBean()
     */
    public AbstractAssert<?, T> extractingRootBean() {
        return extractingRootBean(new ObjectAssertFactory<>());
    }

    /**
     * Verifies that the {@link ConstraintViolation#getRootBean() actual.rootBean} is equal to specified value.
     *
     * @param expectedRootBean the expected value of {@code actual.rootBean}.
     * @return this assertion object.
     * @see ConstraintViolation#getRootBean()
     */
    public SELF hasRootBean(final T expectedRootBean) {
        extractingRootBean().isEqualTo(expectedRootBean);
        return myself;
    }

    /**
     * Verifies that the {@code actual} constraint violation has no {@link ConstraintViolation#getRootBean() rootBean}.
     *
     * @return this assertion object.
     * @apiNote This method invokes {@link #hasRootBean(Object)} method with {@code null}, and returns the result.
     */
    public SELF doesNotHaveRootBean() {
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
    public <ASSERT extends AbstractClassAssert<?>> ASSERT extractingRootBeanClass(
            final AssertFactory<? super Class<T>, ? extends ASSERT> assertFactory) {
        Objects.requireNonNull(assertFactory, "assertFactory is null");
        return isNotNull()
                .extracting(ConstraintViolation::getRootBeanClass, assertFactory);
    }

    /**
     * Extracts an assertion for verifying {@code actual.rootBeanClass}.
     *
     * @return a class assert.
     */
    public AbstractClassAssert<?> extractingRootBeanClass() {
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
    public SELF hasRootBeanClass(final Class<T> expectedRootBeanClass) {
        Objects.requireNonNull(expectedRootBeanClass, "expectedRootBeanClass is null");
        extractingRootBeanClass().isEqualTo(expectedRootBeanClass);
        return myself;
    }
}
