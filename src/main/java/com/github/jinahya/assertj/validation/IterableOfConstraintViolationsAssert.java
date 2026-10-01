package com.github.jinahya.assertj.validation;

/*-
 * #%L
 * assertj-validation
 * %%
 * Copyright (C) 2021 - 2023 Jinahya, Inc.
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

import jakarta.validation.ConstraintViolation;

class IterableOfConstraintViolationsAssert<T>
        extends AbstractIterableOfConstraintViolationsAssert<IterableOfConstraintViolationsAssert<T>, T> {

    IterableOfConstraintViolationsAssert(final Iterable<? extends ConstraintViolation<T>> actual) {
        super(actual, IterableOfConstraintViolationsAssert.class);
    }

    @Override
    protected DefaultConstraintViolationAssert<T> toAssert(final ConstraintViolation<T> value, final String description) {
        return new DefaultConstraintViolationAssert<>(value)
                .describedAs(description);
    }

    @Override
    protected IterableOfConstraintViolationsAssert<T> newAbstractIterableAssert(
            final Iterable<? extends ConstraintViolation<T>> iterable) {
        return new IterableOfConstraintViolationsAssert<>(iterable);
    }
}
