package com.github.jinahya.assertj.validation.example.user;

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

import com.github.jinahya.assertj.validation.ValidationAssertionsTestUtils;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatBean;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class User_ExtractingViolations_Test {

    @Test
    void should_extract_every_violation() {
        assertThatCode(() -> assertThatBean(User.of(null, -1))
                .extractingViolations()
                .extracting(v -> v.getPropertyPath().toString())
                .containsExactlyInAnyOrder(User.PROPERTY_NAME_NAME, User.PROPERTY_NAME_AGE))
                .doesNotThrowAnyException();
    }

    @Test
    void should_extract_an_empty_set_from_a_valid_bean() {
        assertThatCode(() -> assertThatBean(User.newValidUser()).extractingViolations().isEmpty())
                .doesNotThrowAnyException();
    }

    @Test
    void should_honour_the_targeted_groups() {
        final var senior = User.of(User.validName(), UserConstants.MIN_AGE_FOR_SENIOR);
        assertThatCode(() -> assertThatBean(senior).targetingGroups(Junior.class).extractingViolations().hasSize(1))
                .doesNotThrowAnyException();
        assertThatCode(() -> assertThatBean(senior).targetingGroups(Senior.class).extractingViolations().isEmpty())
                .doesNotThrowAnyException();
    }

    @Test
    void should_validate_once_with_the_configured_validator() {
        final Validator validator = spy(ValidationAssertionsTestUtils.<Validator>applyValidator(v -> v));
        assertThatBean(User.of(null, 0)).usingValidator(validator).extractingViolations().hasSize(1);
        verify(validator, times(1)).validate(any(), any(Class[].class));
    }

    @Test
    void should_fail_if_actual_is_null() {
        assertThatThrownBy(() -> assertThatBean(null).extractingViolations())
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Expecting actual not to be null");
    }

    @Test
    void should_carry_the_description() {
        assertThatThrownBy(() -> assertThatBean(User.of(null, 0)).as("case 42").extractingViolations().isEmpty())
                .hasMessageStartingWith("[case 42]");
    }
}
