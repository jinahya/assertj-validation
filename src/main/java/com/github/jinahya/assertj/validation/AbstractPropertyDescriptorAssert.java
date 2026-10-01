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

import jakarta.validation.metadata.PropertyDescriptor;
import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.InstanceOfAssertFactories;

/**
 * An abstract assertion class for verifying {@link PropertyDescriptor} values.
 *
 * @param <SELF> self type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S119" // <SELF ...>
})
public abstract class AbstractPropertyDescriptorAssert<SELF extends AbstractPropertyDescriptorAssert<SELF>>
        extends AbstractCascadableContainerDescriptorAssert<SELF, PropertyDescriptor> {

    /**
     * Creates a new instance with specified arguments.
     *
     * @param actual   the actual value to verify.
     * @param selfType a class of {@code SELF}.
     */
    protected AbstractPropertyDescriptorAssert(final PropertyDescriptor actual, final Class<?> selfType) {
        super(actual, selfType);
    }

    /**
     * Extracts an assertion for verifying the {@code actual} descriptor's
     * {@link PropertyDescriptor#getPropertyName() propertyName}.
     *
     * @return a string assertion for the {@code actual} descriptor's property name.
     * @see PropertyDescriptor#getPropertyName()
     */
    public AbstractStringAssert<?> extractingPropertyName() {
        return isNotNull()
                .extracting(PropertyDescriptor::getPropertyName, InstanceOfAssertFactories.STRING);
    }

    /**
     * Verifies that the {@link PropertyDescriptor#getPropertyName() actual.propertyName} is equal to specified value.
     *
     * @param expectedPropertyName the expected value of {@code actual.propertyName}.
     * @return this assertion object.
     * @see PropertyDescriptor#getPropertyName()
     */
    public SELF hasPropertyName(final String expectedPropertyName) {
        extractingPropertyName().isEqualTo(expectedPropertyName);
        return myself;
    }
}
