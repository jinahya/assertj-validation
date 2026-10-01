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

import jakarta.validation.metadata.ConstraintDescriptor;
import jakarta.validation.metadata.ElementDescriptor;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractClassAssert;
import org.assertj.core.api.CollectionAssert;
import org.assertj.core.api.InstanceOfAssertFactories;

import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * An abstract assertion class for verifying {@link ElementDescriptor} values, the common base of the Jakarta Validation
 * metadata API.
 *
 * @param <SELF>   self type parameter
 * @param <ACTUAL> actual type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see jakarta.validation.Validator#getConstraintsForClass(Class)
 */
@SuppressWarnings({
        "java:S119" // <SELF ...>
})
public abstract class AbstractElementDescriptorAssert<
        SELF extends AbstractElementDescriptorAssert<SELF, ACTUAL>, ACTUAL extends ElementDescriptor>
        extends AbstractAssert<SELF, ACTUAL> {

    /**
     * Creates a new instance with specified arguments.
     *
     * @param actual   the actual value to verify.
     * @param selfType a class of {@code SELF}.
     */
    protected AbstractElementDescriptorAssert(final ACTUAL actual, final Class<?> selfType) {
        super(actual, selfType);
    }

    // ---------------------------------------------------------------------------------------------------- constraints

    /**
     * Extracts an assertion for verifying whether the {@code actual} descriptor
     * {@link ElementDescriptor#hasConstraints() has constraints}.
     *
     * @return a boolean assertion for the {@code actual} descriptor's has-constraints flag.
     * @see ElementDescriptor#hasConstraints()
     */
    public AbstractBooleanAssert<?> extractingHasConstraints() {
        return isNotNull()
                .extracting(ElementDescriptor::hasConstraints, InstanceOfAssertFactories.BOOLEAN);
    }

    /**
     * Verifies that the {@code actual} descriptor {@link ElementDescriptor#hasConstraints() has} at least one
     * constraint.
     *
     * @return this assertion object.
     * @see ElementDescriptor#hasConstraints()
     */
    public SELF hasConstraints() {
        extractingHasConstraints().isTrue();
        return myself;
    }

    /**
     * Verifies that the {@code actual} descriptor has no {@link ElementDescriptor#hasConstraints() constraint}.
     *
     * @return this assertion object.
     * @see ElementDescriptor#hasConstraints()
     */
    public SELF doesNotHaveConstraints() {
        extractingHasConstraints().isFalse();
        return myself;
    }

    // ----------------------------------------------------------------------------------------------- constraintDescriptors

    /**
     * Extracts an assertion for verifying the {@code actual} descriptor's
     * {@link ElementDescriptor#getConstraintDescriptors() constraintDescriptors}.
     *
     * @return a collection assertion for the {@code actual} descriptor's constraint descriptors.
     * @see ElementDescriptor#getConstraintDescriptors()
     */
    public CollectionAssert<ConstraintDescriptor<?>> extractingConstraintDescriptors() {
        return isNotNull()
                .extracting(ElementDescriptor::getConstraintDescriptors, CollectionAssert::new);
    }

    /**
     * Extracts an assertion for verifying the constraint descriptors matched by the
     * {@link ElementDescriptor#findConstraints() constraint finder} configured by specified operator.
     *
     * @param configurer an operator configuring the constraint finder; must be not {@code null}.
     * @return a collection assertion for the matched constraint descriptors.
     * @see ElementDescriptor#findConstraints()
     * @see ElementDescriptor.ConstraintFinder
     */
    public CollectionAssert<ConstraintDescriptor<?>> extractingConstraintDescriptors(
            final UnaryOperator<ElementDescriptor.ConstraintFinder> configurer) {
        Objects.requireNonNull(configurer, "configurer is null");
        return isNotNull()
                .extracting(a -> configurer.apply(a.findConstraints()).getConstraintDescriptors(),
                            CollectionAssert::new);
    }

    // --------------------------------------------------------------------------------------------------- elementClass

    /**
     * Extracts an assertion for verifying the {@code actual} descriptor's
     * {@link ElementDescriptor#getElementClass() elementClass}.
     *
     * @return a class assertion for the {@code actual} descriptor's element class.
     * @see ElementDescriptor#getElementClass()
     */
    public AbstractClassAssert<?> extractingElementClass() {
        return isNotNull()
                .extracting(ElementDescriptor::getElementClass, InstanceOfAssertFactories.CLASS);
    }

    /**
     * Verifies that the {@link ElementDescriptor#getElementClass() actual.elementClass} is equal to specified value.
     *
     * @param expectedElementClass the expected value of {@code actual.elementClass}.
     * @return this assertion object.
     * @see ElementDescriptor#getElementClass()
     */
    public SELF hasElementClass(final Class<?> expectedElementClass) {
        extractingElementClass().isEqualTo(expectedElementClass);
        return myself;
    }
}
