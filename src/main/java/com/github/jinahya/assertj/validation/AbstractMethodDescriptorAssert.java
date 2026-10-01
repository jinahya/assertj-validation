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

import jakarta.validation.metadata.MethodDescriptor;

/**
 * An abstract assertion class for verifying {@link MethodDescriptor} values.
 *
 * @param <SELF> self type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S119" // <SELF ...>
})
public abstract class AbstractMethodDescriptorAssert<SELF extends AbstractMethodDescriptorAssert<SELF>>
        extends AbstractExecutableDescriptorAssert<SELF, MethodDescriptor> {

    protected AbstractMethodDescriptorAssert(final MethodDescriptor actual, final Class<?> selfType) {
        super(actual, selfType);
    }
}
