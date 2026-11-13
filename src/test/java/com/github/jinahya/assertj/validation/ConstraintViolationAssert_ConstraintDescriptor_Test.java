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

class ConstraintViolationAssert_ConstraintDescriptor_Test
        extends ConstraintViolationAssert_$Test {

    @Test
    void should_fail_if_there_is_no_constraint_descriptor() {
        assertThatThrownBy(() -> assertion(violation()).extractingConstraintDescriptor().isNotNull())
                .isInstanceOf(AssertionError.class);
    }

    @Test
    void should_carry_the_description_to_the_constraint_descriptor() {
        assertThatThrownBy(() -> assertion(violation()).as("case 42").extractingConstraintDescriptor().isNotNull())
                .hasMessageStartingWith("[case 42]");
    }

    @Test
    void should_fail_if_actual_is_null() {
        assertThatThrownBy(() -> assertion(null).extractingConstraintDescriptor())
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Expecting actual not to be null");
    }
}
