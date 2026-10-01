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

import jakarta.validation.metadata.ParameterDescriptor;
import org.assertj.core.api.AbstractIntegerAssert;
import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.InstanceOfAssertFactories;

/**
 * An abstract assertion class for verifying {@link ParameterDescriptor} values.
 *
 * @param <SELF> self type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S119" // <SELF ...>
})
public abstract class AbstractParameterDescriptorAssert<SELF extends AbstractParameterDescriptorAssert<SELF>>
        extends AbstractCascadableContainerDescriptorAssert<SELF, ParameterDescriptor> {

    /**
     * Creates a new instance with specified arguments.
     *
     * @param actual   the actual value to verify.
     * @param selfType a class of {@code SELF}.
     */
    protected AbstractParameterDescriptorAssert(final ParameterDescriptor actual, final Class<?> selfType) {
        super(actual, selfType);
    }

    /**
     * Extracts an assertion for verifying the {@code actual} descriptor's
     * {@link ParameterDescriptor#getIndex() index}.
     *
     * @return an integer assertion for the {@code actual} descriptor's index.
     * @see ParameterDescriptor#getIndex()
     */
    public AbstractIntegerAssert<?> extractingIndex() {
        return isNotNull()
                .extracting(ParameterDescriptor::getIndex, InstanceOfAssertFactories.INTEGER);
    }

    /**
     * Verifies that the {@link ParameterDescriptor#getIndex() actual.index} is equal to specified value.
     *
     * @param expectedIndex the expected value of {@code actual.index}.
     * @return this assertion object.
     * @see ParameterDescriptor#getIndex()
     */
    public SELF hasIndex(final int expectedIndex) {
        extractingIndex().isEqualTo(expectedIndex);
        return myself;
    }

    /**
     * Extracts an assertion for verifying the {@code actual} descriptor's {@link ParameterDescriptor#getName() name}.
     *
     * @return a string assertion for the {@code actual} descriptor's name.
     * @see ParameterDescriptor#getName()
     */
    public AbstractStringAssert<?> extractingName() {
        return isNotNull()
                .extracting(ParameterDescriptor::getName, InstanceOfAssertFactories.STRING);
    }

    /**
     * Verifies that the {@link ParameterDescriptor#getName() actual.name} is equal to specified value.
     *
     * @param expectedName the expected value of {@code actual.name}.
     * @return this assertion object.
     * @see ParameterDescriptor#getName()
     */
    public SELF hasName(final String expectedName) {
        extractingName().isEqualTo(expectedName);
        return myself;
    }
}
