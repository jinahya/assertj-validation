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

import jakarta.validation.metadata.CascadableDescriptor;
import jakarta.validation.metadata.ContainerDescriptor;
import jakarta.validation.metadata.ContainerElementTypeDescriptor;
import jakarta.validation.metadata.ElementDescriptor;
import jakarta.validation.metadata.GroupConversionDescriptor;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.CollectionAssert;
import org.assertj.core.api.InstanceOfAssertFactories;

/**
 * An abstract assertion class for the metadata descriptors that are at once an {@link ElementDescriptor}, a
 * {@link CascadableDescriptor} and a {@link ContainerDescriptor}.
 * <p>
 * In Jakarta Validation those three always occur together &mdash; on
 * {@link jakarta.validation.metadata.PropertyDescriptor PropertyDescriptor},
 * {@link jakarta.validation.metadata.ParameterDescriptor ParameterDescriptor},
 * {@link jakarta.validation.metadata.ReturnValueDescriptor ReturnValueDescriptor} and
 * {@link ContainerElementTypeDescriptor} &mdash; and never apart, so this single intersection-bounded class covers
 * the combination without needing a mixin interface.
 *
 * @param <SELF>   self type parameter
 * @param <ACTUAL> actual type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S119" // <SELF ...>
})
public abstract class AbstractCascadableContainerDescriptorAssert<
        SELF extends AbstractCascadableContainerDescriptorAssert<SELF, ACTUAL>,
        ACTUAL extends ElementDescriptor & CascadableDescriptor & ContainerDescriptor>
        extends AbstractElementDescriptorAssert<SELF, ACTUAL> {

    /**
     * Creates a new instance with specified arguments.
     *
     * @param actual the actual value to verify.
     * @param selfType a class of {@code SELF}.
     */
    protected AbstractCascadableContainerDescriptorAssert(final ACTUAL actual, final Class<?> selfType) {
        super(actual, selfType);
    }

    // -------------------------------------------------------------------------------------------------------- cascaded

    /**
     * Extracts an assertion for verifying whether the {@code actual} descriptor
     * {@link CascadableDescriptor#isCascaded() is cascaded}.
     *
     * @return a boolean assertion for the {@code actual} descriptor's cascaded flag.
     * @see CascadableDescriptor#isCascaded()
     */
    public AbstractBooleanAssert<?> extractingCascaded() {
        return isNotNull()
                .extracting(CascadableDescriptor::isCascaded, InstanceOfAssertFactories.BOOLEAN);
    }

    /**
     * Verifies that the {@code actual} descriptor is {@link CascadableDescriptor#isCascaded() cascaded}, that is, it
     * is annotated with {@link jakarta.validation.Valid @Valid}.
     *
     * @return this assertion object.
     * @see CascadableDescriptor#isCascaded()
     */
    public SELF isCascaded() {
        extractingCascaded().isTrue();
        return myself;
    }

    /**
     * Verifies that the {@code actual} descriptor is <em>not</em>
     * {@link CascadableDescriptor#isCascaded() cascaded}.
     *
     * @return this assertion object.
     * @see CascadableDescriptor#isCascaded()
     */
    public SELF isNotCascaded() {
        extractingCascaded().isFalse();
        return myself;
    }

    // ------------------------------------------------------------------------------------------------ groupConversions

    /**
     * Extracts an assertion for verifying the {@code actual} descriptor's
     * {@link CascadableDescriptor#getGroupConversions() groupConversions}.
     *
     * @return a collection assertion for the {@code actual} descriptor's group conversions.
     * @see CascadableDescriptor#getGroupConversions()
     */
    public CollectionAssert<GroupConversionDescriptor> extractingGroupConversions() {
        return isNotNull()
                .extracting(CascadableDescriptor::getGroupConversions, CollectionAssert::new);
    }

    /**
     * Verifies that the {@code actual} descriptor declares no
     * {@link CascadableDescriptor#getGroupConversions() group conversion}.
     *
     * @return this assertion object.
     * @see CascadableDescriptor#getGroupConversions()
     */
    public SELF doesNotHaveAnyGroupConversion() {
        extractingGroupConversions().isEmpty();
        return myself;
    }

    // ------------------------------------------------------------------------------- constrainedContainerElementTypes

    /**
     * Extracts an assertion for verifying the {@code actual} descriptor's
     * {@link ContainerDescriptor#getConstrainedContainerElementTypes() constrainedContainerElementTypes}.
     *
     * @return a collection assertion for the {@code actual} descriptor's constrained container element types.
     * @see ContainerDescriptor#getConstrainedContainerElementTypes()
     */
    public CollectionAssert<ContainerElementTypeDescriptor> extractingConstrainedContainerElementTypes() {
        return isNotNull()
                .extracting(ContainerDescriptor::getConstrainedContainerElementTypes, CollectionAssert::new);
    }

    /**
     * Verifies that the {@code actual} descriptor has no
     * {@link ContainerDescriptor#getConstrainedContainerElementTypes() constrained container element type}.
     *
     * @return this assertion object.
     * @see ContainerDescriptor#getConstrainedContainerElementTypes()
     */
    public SELF doesNotHaveAnyConstrainedContainerElementType() {
        extractingConstrainedContainerElementTypes().isEmpty();
        return myself;
    }
}
