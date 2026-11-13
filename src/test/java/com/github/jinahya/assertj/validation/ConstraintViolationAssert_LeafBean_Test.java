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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class ConstraintViolationAssert_LeafBean_Test
        extends ConstraintViolationAssert__Base {

    @Test
    void should_pass_if_leaf_bean_is_equal() {
        final var actual = violation();
        final var leaf = new Object();
        when(actual.getLeafBean()).thenReturn(leaf);
        assertThatCode(() -> assertion(actual).hasLeafBean(leaf)).doesNotThrowAnyException();
        returnsSelf(assertion(actual), a -> a.hasLeafBean(leaf));
    }

    @Test
    void should_fail_naming_the_attribute_if_leaf_bean_differs() {
        final var actual = violation();
        when(actual.getLeafBean()).thenReturn(new Object());
        assertThatThrownBy(() -> assertion(actual).hasLeafBean("other"))
                .hasMessageStartingWith("[leafBean]");
    }

    @Test
    void should_pass_if_there_is_no_leaf_bean() {
        assertThatCode(() -> assertion(violation()).doesNotHaveLeafBean()).doesNotThrowAnyException();
    }

    @Test
    void should_fail_if_there_is_a_leaf_bean() {
        final var actual = violation();
        when(actual.getLeafBean()).thenReturn(new Object());
        assertThatThrownBy(() -> assertion(actual).doesNotHaveLeafBean()).isInstanceOf(AssertionError.class);
    }

    @Test
    void should_fail_if_actual_is_null() {
        assertThatThrownBy(() -> assertion(null).doesNotHaveLeafBean())
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Expecting actual not to be null");
    }
}
