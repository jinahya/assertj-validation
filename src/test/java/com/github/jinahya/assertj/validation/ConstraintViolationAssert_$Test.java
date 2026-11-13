package com.github.jinahya.assertj.validation;

/*-
 * #%L
 * assertj-validation
 * %%
 * Copyright (C) 2021 - 2024 Jinahya, Inc.
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

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

abstract class ConstraintViolationAssert_$Test {

    /**
     * Returns a mock violation, whose getters answer {@code null} until stubbed.
     */
    @SuppressWarnings({"unchecked"})
    static <T> ConstraintViolation<T> violation() {
        return mock(ConstraintViolation.class);
    }

    /**
     * Verifies that specified call on specified assertion returns the assertion itself, so that it chains.
     */
    static <A> void returnsSelf(final A assertion, final Function<? super A, ?> call) {
        assertThat(call.apply(assertion)).isSameAs(assertion);
    }

    <T> AbstractConstraintViolationAssert<?, ConstraintViolation<T>, T> assertion(final ConstraintViolation<T> actual) {
        return new DefaultConstraintViolationAssert<>(actual);
    }
}
