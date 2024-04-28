package com.github.jinahya.assertj.validation;

/*-
 * #%L
 * assertj-bean-validation
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
import org.assertj.core.api.ObjectAssertFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;
import java.util.Objects;
import java.util.function.Function;

/**
 * An abstract assertion class for verifying {@link ConstraintViolation} values.
 *
 * @param <SELF>   self type parameter
 * @param <ACTUAL> actual type parameter
 * @param <T>      root bean type parameter
 */
public abstract class AbstractConstraintViolationAssert<
        SELF extends AbstractConstraintViolationAssert<SELF, ACTUAL, T>, ACTUAL extends ConstraintViolation<T>, T>
        extends AbstractValidationAssert<SELF, ACTUAL>
        implements ConstraintViolationAssert<SELF, ACTUAL, T> {

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
    @Override
    public <D extends ConstraintDescriptor<?>, A extends AbstractConstraintDescriptorAssert<?, ? extends D, ?>>
    A extractingConstraintDescriptor(
            final Function<? super ACTUAL, ? extends D> descriptorExtractor,
            final AssertFactory<? super D, ? extends A> assertFactory) {
        return isNotNull().extracting(descriptorExtractor, assertFactory);
    }

    // -------------------------------------------------------------------------------------------- executableParameters
    @Override
    public <A extends AbstractObjectArrayAssert<?, ? extends E>, E> A extractingExecutableParameters(
            final Function<? super ACTUAL, ? extends E[]> parametersExtractor,
            final AssertFactory<? super E[], ? extends A> assertFactory) {
        return isNotNull()
                .extracting(parametersExtractor, assertFactory);
    }

    // ------------------------------------------------------------------------------------------- executableReturnValue
    @Override
    public <V, A extends AbstractAssert<?, ? extends V>> A extractingExecutableReturnValue(
            final Function<? super ACTUAL, ? extends V> valueExtractor,
            final AssertFactory<? super V, ? extends A> assertFactory) {
        return isNotNull().extracting(valueExtractor, assertFactory);
    }

    // ---------------------------------------------------------------------------------------------------- invalidValue
    @Override
    public <V, A extends AbstractAssert<?, ? extends V>> A extractingInvalidValue(
            final Function<? super ACTUAL, ? extends V> valueExtractor,
            final AssertFactory<? super V, ? extends A> assertFactory) {
        return isNotNull()
                .extracting(valueExtractor, assertFactory);
    }

    // -------------------------------------------------------------------------------------------------------- leafBean

    @Override
    public <B, A extends AbstractAssert<?, ? extends B>> A extractingLeafBean(
            final Function<? super ACTUAL, ? extends B> beanExtractor,
            final AssertFactory<? super B, ? extends A> assertFactory) {
        return isNotNull()
                .extracting(beanExtractor, assertFactory);
    }

    @Override
    public Assert<?, ?> extractingLeafBean() {
        return isNotNull()
                .extracting(ConstraintViolation::getLeafBean, new ObjectAssertFactory<>());
    }

    // --------------------------------------------------------------------------------------------------------- message

    @Override
    public SELF hasMessage(final String expectedMessage) {
        isNotNull()
                .extracting(ConstraintViolation::getMessage, InstanceOfAssertFactories.STRING)
                .isEqualTo(expectedMessage);
        return (SELF) this;
    }

    // ---------------------------------------------------------------------------------------------------- propertyPath
    @Override
    public <ASSERT extends AbstractPathAssert<?, ? extends AbstractPathAssert.AbstractNodeAssert<?>>>
    ASSERT extractingPropertyPath(final AssertFactory<? super Path, ? extends ASSERT> assertFactory) {
        return isNotNull()
                .extracting(ConstraintViolation::getPropertyPath, assertFactory::createAssert);
    }

    // -------------------------------------------------------------------------------------------------------- rootBean
    @Override
    public <ASSERT extends AbstractAssert<?, T>> ASSERT extractingRootBean(
            final AssertFactory<? super T, ? extends ASSERT> assertFactory) {
        return isNotNull()
                .extracting(ConstraintViolation::getRootBean, assertFactory);
    }

    // --------------------------------------------------------------------------------------------------- rootBeanClass
    @Override
    public <ASSERT extends AbstractClassAssert<?>> ASSERT extractingRootBeanClass(
            final AssertFactory<? super Class<T>, ? extends ASSERT> assertFactory) {
        Objects.requireNonNull(assertFactory, "assertFactory is null");
        return isNotNull()
                .extracting(ConstraintViolation::getRootBeanClass, assertFactory);
    }
}
