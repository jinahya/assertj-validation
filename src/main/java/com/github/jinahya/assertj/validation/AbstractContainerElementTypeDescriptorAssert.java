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

import jakarta.validation.metadata.ContainerElementTypeDescriptor;
import org.assertj.core.api.AbstractClassAssert;
import org.assertj.core.api.AbstractIntegerAssert;
import org.assertj.core.api.InstanceOfAssertFactories;

/**
 * An abstract assertion class for verifying {@link ContainerElementTypeDescriptor} values.
 *
 * @param <SELF> self type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S119" // <SELF ...>
})
public abstract class AbstractContainerElementTypeDescriptorAssert<
        SELF extends AbstractContainerElementTypeDescriptorAssert<SELF>>
        extends AbstractCascadableContainerDescriptorAssert<SELF, ContainerElementTypeDescriptor> {

    protected AbstractContainerElementTypeDescriptorAssert(final ContainerElementTypeDescriptor actual,
                                                           final Class<?> selfType) {
        super(actual, selfType);
    }

    /**
     * Extracts an assertion for verifying the {@code actual} descriptor's
     * {@link ContainerElementTypeDescriptor#getContainerClass() containerClass}.
     *
     * @return a class assertion for the {@code actual} descriptor's container class.
     * @see ContainerElementTypeDescriptor#getContainerClass()
     */
    public AbstractClassAssert<?> extractingContainerClass() {
        return isNotNull()
                .extracting(ContainerElementTypeDescriptor::getContainerClass, InstanceOfAssertFactories.CLASS);
    }

    /**
     * Verifies that the {@link ContainerElementTypeDescriptor#getContainerClass() actual.containerClass} is equal to
     * specified value.
     *
     * @param expectedContainerClass the expected value of {@code actual.containerClass}.
     * @return this assertion object.
     * @see ContainerElementTypeDescriptor#getContainerClass()
     */
    public SELF hasContainerClass(final Class<?> expectedContainerClass) {
        extractingContainerClass().isEqualTo(expectedContainerClass);
        return myself;
    }

    /**
     * Extracts an assertion for verifying the {@code actual} descriptor's
     * {@link ContainerElementTypeDescriptor#getTypeArgumentIndex() typeArgumentIndex}.
     *
     * @return an integer assertion for the {@code actual} descriptor's type argument index.
     * @see ContainerElementTypeDescriptor#getTypeArgumentIndex()
     */
    public AbstractIntegerAssert<?> extractingTypeArgumentIndex() {
        return isNotNull()
                .extracting(ContainerElementTypeDescriptor::getTypeArgumentIndex, InstanceOfAssertFactories.INTEGER);
    }

    /**
     * Verifies that the {@link ContainerElementTypeDescriptor#getTypeArgumentIndex() actual.typeArgumentIndex} is
     * equal to specified value.
     *
     * @param expectedTypeArgumentIndex the expected value of {@code actual.typeArgumentIndex}.
     * @return this assertion object.
     * @see ContainerElementTypeDescriptor#getTypeArgumentIndex()
     */
    public SELF hasTypeArgumentIndex(final Integer expectedTypeArgumentIndex) {
        extractingTypeArgumentIndex().isEqualTo(expectedTypeArgumentIndex);
        return myself;
    }
}
