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

import jakarta.validation.metadata.CrossParameterDescriptor;
import jakarta.validation.metadata.ExecutableDescriptor;
import jakarta.validation.metadata.ParameterDescriptor;
import jakarta.validation.metadata.ReturnValueDescriptor;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.AssertFactory;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.assertj.core.api.ListAssert;

/**
 * An abstract assertion class for verifying {@link ExecutableDescriptor} values.
 *
 * @param <SELF>   self type parameter
 * @param <ACTUAL> actual type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S119" // <SELF ...>
})
public abstract class AbstractExecutableDescriptorAssert<
        SELF extends AbstractExecutableDescriptorAssert<SELF, ACTUAL>, ACTUAL extends ExecutableDescriptor>
        extends AbstractElementDescriptorAssert<SELF, ACTUAL> {

    protected AbstractExecutableDescriptorAssert(final ACTUAL actual, final Class<?> selfType) {
        super(actual, selfType);
    }

    // ---------------------------------------------------------------------------------------------------------- name

    /**
     * Extracts an assertion for verifying the {@code actual} descriptor's
     * {@link ExecutableDescriptor#getName() name}.
     *
     * @return a string assertion for the {@code actual} descriptor's name.
     * @see ExecutableDescriptor#getName()
     */
    public AbstractStringAssert<?> extractingName() {
        return isNotNull()
                .extracting(ExecutableDescriptor::getName, InstanceOfAssertFactories.STRING);
    }

    /**
     * Verifies that the {@link ExecutableDescriptor#getName() actual.name} is equal to specified value.
     *
     * @param expectedName the expected value of {@code actual.name}.
     * @return this assertion object.
     * @see ExecutableDescriptor#getName()
     */
    public SELF hasName(final String expectedName) {
        extractingName().isEqualTo(expectedName);
        return myself;
    }

    // ---------------------------------------------------------------------------------------------------- parameters

    /**
     * Extracts an assertion for verifying the {@code actual} descriptor's
     * {@link ExecutableDescriptor#getParameterDescriptors() parameterDescriptors}.
     *
     * @return a list assertion for the {@code actual} descriptor's parameter descriptors.
     * @see ExecutableDescriptor#getParameterDescriptors()
     */
    public ListAssert<ParameterDescriptor> extractingParameterDescriptors() {
        return isNotNull()
                .extracting(ExecutableDescriptor::getParameterDescriptors, ListAssert::new);
    }

    /**
     * Extracts an assertion for verifying the parameter descriptor at specified index, using specified assertion
     * factory.
     *
     * @param index   the index of the parameter.
     * @param factory the assertion factory.
     * @param <A>     assertion type parameter
     * @return an instance of {@link A}.
     * @see ExecutableDescriptor#getParameterDescriptors()
     */
    public <A extends AbstractParameterDescriptorAssert<? extends A>> A extractingParameterDescriptor(
            final int index, final AssertFactory<? super ParameterDescriptor, ? extends A> factory) {
        if (index < 0) {
            throw new IllegalArgumentException("negative index: " + index);
        }
        return isNotNull()
                .extracting(a -> a.getParameterDescriptors().get(index), factory);
    }

    /**
     * Extracts an assertion for verifying the parameter descriptor at specified index.
     *
     * @param index the index of the parameter.
     * @return a parameter-descriptor assertion.
     * @see ExecutableDescriptor#getParameterDescriptors()
     */
    public AbstractParameterDescriptorAssert<?> extractingParameterDescriptor(final int index) {
        return extractingParameterDescriptor(index, DefaultParameterDescriptorAssert::new);
    }

    /**
     * Extracts an assertion for verifying whether the {@code actual} descriptor
     * {@link ExecutableDescriptor#hasConstrainedParameters() has constrained parameters}.
     *
     * @return a boolean assertion for the {@code actual} descriptor's has-constrained-parameters flag.
     * @see ExecutableDescriptor#hasConstrainedParameters()
     */
    public AbstractBooleanAssert<?> extractingHasConstrainedParameters() {
        return isNotNull()
                .extracting(ExecutableDescriptor::hasConstrainedParameters, InstanceOfAssertFactories.BOOLEAN);
    }

    /**
     * Verifies that the {@code actual} descriptor
     * {@link ExecutableDescriptor#hasConstrainedParameters() has constrained parameters}.
     *
     * @return this assertion object.
     * @see ExecutableDescriptor#hasConstrainedParameters()
     */
    public SELF hasConstrainedParameters() {
        extractingHasConstrainedParameters().isTrue();
        return myself;
    }

    /**
     * Verifies that the {@code actual} descriptor has no
     * {@link ExecutableDescriptor#hasConstrainedParameters() constrained parameter}.
     *
     * @return this assertion object.
     * @see ExecutableDescriptor#hasConstrainedParameters()
     */
    public SELF doesNotHaveConstrainedParameters() {
        extractingHasConstrainedParameters().isFalse();
        return myself;
    }

    // ----------------------------------------------------------------------------------------------- crossParameter

    /**
     * Extracts an assertion for verifying the {@code actual} descriptor's
     * {@link ExecutableDescriptor#getCrossParameterDescriptor() crossParameterDescriptor}, using specified assertion
     * factory.
     *
     * @param factory the assertion factory.
     * @param <A>     assertion type parameter
     * @return an instance of {@link A}.
     * @see ExecutableDescriptor#getCrossParameterDescriptor()
     */
    public <A extends AbstractCrossParameterDescriptorAssert<? extends A>> A extractingCrossParameterDescriptor(
            final AssertFactory<? super CrossParameterDescriptor, ? extends A> factory) {
        return isNotNull()
                .extracting(ExecutableDescriptor::getCrossParameterDescriptor, factory);
    }

    /**
     * Extracts an assertion for verifying the {@code actual} descriptor's
     * {@link ExecutableDescriptor#getCrossParameterDescriptor() crossParameterDescriptor}.
     *
     * @return a cross-parameter-descriptor assertion.
     * @see ExecutableDescriptor#getCrossParameterDescriptor()
     */
    public AbstractCrossParameterDescriptorAssert<?> extractingCrossParameterDescriptor() {
        return extractingCrossParameterDescriptor(DefaultCrossParameterDescriptorAssert::new);
    }

    // --------------------------------------------------------------------------------------------------- returnValue

    /**
     * Extracts an assertion for verifying the {@code actual} descriptor's
     * {@link ExecutableDescriptor#getReturnValueDescriptor() returnValueDescriptor}, using specified assertion
     * factory.
     *
     * @param factory the assertion factory.
     * @param <A>     assertion type parameter
     * @return an instance of {@link A}.
     * @see ExecutableDescriptor#getReturnValueDescriptor()
     */
    public <A extends AbstractReturnValueDescriptorAssert<? extends A>> A extractingReturnValueDescriptor(
            final AssertFactory<? super ReturnValueDescriptor, ? extends A> factory) {
        return isNotNull()
                .extracting(ExecutableDescriptor::getReturnValueDescriptor, factory);
    }

    /**
     * Extracts an assertion for verifying the {@code actual} descriptor's
     * {@link ExecutableDescriptor#getReturnValueDescriptor() returnValueDescriptor}.
     *
     * @return a return-value-descriptor assertion.
     * @see ExecutableDescriptor#getReturnValueDescriptor()
     */
    public AbstractReturnValueDescriptorAssert<?> extractingReturnValueDescriptor() {
        return extractingReturnValueDescriptor(DefaultReturnValueDescriptorAssert::new);
    }

    /**
     * Extracts an assertion for verifying whether the {@code actual} descriptor
     * {@link ExecutableDescriptor#hasConstrainedReturnValue() has a constrained return value}.
     *
     * @return a boolean assertion for the {@code actual} descriptor's has-constrained-return-value flag.
     * @see ExecutableDescriptor#hasConstrainedReturnValue()
     */
    public AbstractBooleanAssert<?> extractingHasConstrainedReturnValue() {
        return isNotNull()
                .extracting(ExecutableDescriptor::hasConstrainedReturnValue, InstanceOfAssertFactories.BOOLEAN);
    }

    /**
     * Verifies that the {@code actual} descriptor
     * {@link ExecutableDescriptor#hasConstrainedReturnValue() has a constrained return value}.
     *
     * @return this assertion object.
     * @see ExecutableDescriptor#hasConstrainedReturnValue()
     */
    public SELF hasConstrainedReturnValue() {
        extractingHasConstrainedReturnValue().isTrue();
        return myself;
    }

    /**
     * Verifies that the {@code actual} descriptor has no
     * {@link ExecutableDescriptor#hasConstrainedReturnValue() constrained return value}.
     *
     * @return this assertion object.
     * @see ExecutableDescriptor#hasConstrainedReturnValue()
     */
    public SELF doesNotHaveConstrainedReturnValue() {
        extractingHasConstrainedReturnValue().isFalse();
        return myself;
    }
}
