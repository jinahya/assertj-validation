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

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatConstraintViolation;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests the {@code messageTemplate} assertions, which verify the <em>non-interpolated</em> message.
 */
class ConstraintViolationAssert_MessageTemplate_Test {

    private static ConstraintViolation<User> blankNameViolation() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            return factory.getValidator()
                    .validateValue(User.class, User.PROPERTY_NAME_NAME, null)
                    .iterator().next();
        }
    }

    @Test
    void __hasMessageTemplate() {
        assertThatConstraintViolation(blankNameViolation())
                .hasMessageTemplate("{jakarta.validation.constraints.NotBlank.message}");
    }

    @Test
    void __extractingMessageTemplate() {
        assertThatConstraintViolation(blankNameViolation())
                .extractingMessageTemplate()
                .startsWith("{")
                .endsWith("}");
    }

    @Test
    void __templateIsNotTheInterpolatedMessage() {
        final var violation = blankNameViolation();
        assertThat(violation.getMessageTemplate()).isNotEqualTo(violation.getMessage());
        assertThatThrownBy(() -> assertThatConstraintViolation(violation).hasMessageTemplate(violation.getMessage()))
                .isInstanceOf(AssertionError.class);
    }
}
