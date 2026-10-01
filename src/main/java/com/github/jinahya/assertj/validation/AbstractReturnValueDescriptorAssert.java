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

import jakarta.validation.metadata.ReturnValueDescriptor;

/**
 * An abstract assertion class for verifying {@link ReturnValueDescriptor} values.
 *
 * @param <SELF> self type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S119" // <SELF ...>
})
public abstract class AbstractReturnValueDescriptorAssert<SELF extends AbstractReturnValueDescriptorAssert<SELF>>
        extends AbstractCascadableContainerDescriptorAssert<SELF, ReturnValueDescriptor> {

    /**
     * Creates a new instance with specified arguments.
     *
     * @param actual the actual value to verify.
     * @param selfType a class of {@code SELF}.
     */
    protected AbstractReturnValueDescriptorAssert(final ReturnValueDescriptor actual, final Class<?> selfType) {
        super(actual, selfType);
    }
}
