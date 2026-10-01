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

import jakarta.validation.metadata.GroupConversionDescriptor;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.AbstractClassAssert;
import org.assertj.core.api.InstanceOfAssertFactories;

/**
 * An abstract assertion class for verifying {@link GroupConversionDescriptor} values.
 *
 * @param <SELF> self type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S119" // <SELF ...>
})
public abstract class AbstractGroupConversionDescriptorAssert<
        SELF extends AbstractGroupConversionDescriptorAssert<SELF>>
        extends AbstractAssert<SELF, GroupConversionDescriptor> {

    /**
     * Creates a new instance with specified arguments.
     *
     * @param actual   the actual value to verify.
     * @param selfType a class of {@code SELF}.
     */
    protected AbstractGroupConversionDescriptorAssert(final GroupConversionDescriptor actual,
                                                      final Class<?> selfType) {
        super(actual, selfType);
    }

    /**
     * Extracts an assertion for verifying the {@code actual} descriptor's
     * {@link GroupConversionDescriptor#getFrom() from} group.
     *
     * @return a class assertion for the {@code actual} descriptor's source group.
     * @see GroupConversionDescriptor#getFrom()
     */
    public AbstractClassAssert<?> extractingFrom() {
        return isNotNull()
                .extracting(GroupConversionDescriptor::getFrom, InstanceOfAssertFactories.CLASS);
    }

    /**
     * Extracts an assertion for verifying the {@code actual} descriptor's {@link GroupConversionDescriptor#getTo() to}
     * group.
     *
     * @return a class assertion for the {@code actual} descriptor's target group.
     * @see GroupConversionDescriptor#getTo()
     */
    public AbstractClassAssert<?> extractingTo() {
        return isNotNull()
                .extracting(GroupConversionDescriptor::getTo, InstanceOfAssertFactories.CLASS);
    }

    /**
     * Verifies that the {@code actual} descriptor converts specified source group to specified target group.
     *
     * @param expectedFrom the expected value of {@link GroupConversionDescriptor#getFrom() actual.from}.
     * @param expectedTo   the expected value of {@link GroupConversionDescriptor#getTo() actual.to}.
     * @return this assertion object.
     * @see GroupConversionDescriptor#getFrom()
     * @see GroupConversionDescriptor#getTo()
     */
    public SELF convertsGroup(final Class<?> expectedFrom, final Class<?> expectedTo) {
        extractingFrom().isEqualTo(expectedFrom);
        extractingTo().isEqualTo(expectedTo);
        return myself;
    }
}
