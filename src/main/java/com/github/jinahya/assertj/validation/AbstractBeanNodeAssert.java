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

import jakarta.validation.Path;
import org.assertj.core.api.AbstractClassAssert;
import org.assertj.core.api.AbstractIntegerAssert;
import org.assertj.core.api.AssertFactory;

/**
 * An abstract assertion class for verifying {@link Path.BeanNode} values.
 *
 * @param <SELF> self type parameter
 */
public abstract class AbstractBeanNodeAssert<SELF extends AbstractBeanNodeAssert<SELF>>
        extends AbstractNodeAssert<SELF, Path.BeanNode>
        implements HasContainerClass<SELF>, HasTypeArgumentIndex<SELF> {

    /**
     * Creates a new instance with specified arguments.
     *
     * @param actual   the actual value to verify.
     * @param selfType a class of {@code SELF}.
     */
    protected AbstractBeanNodeAssert(final Path.BeanNode actual, final Class<?> selfType) {
        super(actual, selfType);
    }

    @Override
    @CheckReturnValue
    public <ASSERT extends AbstractClassAssert<? extends ASSERT>> ASSERT extractingContainerClass(
            final AssertFactory<? super Class<?>, ? extends ASSERT> factory) {
        return isNotNull()
                .extracting(Path.BeanNode::getContainerClass, factory);
    }

    @Override
    @CheckReturnValue
    public <ASSERT extends AbstractIntegerAssert<? extends ASSERT>> ASSERT extractingTypeArgumentIndex(
            final AssertFactory<? super Integer, ? extends ASSERT> factory) {
        return isNotNull()
                .extracting(Path.BeanNode::getTypeArgumentIndex, factory);
    }
}
