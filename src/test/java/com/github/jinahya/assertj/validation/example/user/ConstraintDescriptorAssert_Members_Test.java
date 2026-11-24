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
import jakarta.validation.metadata.ValidateUnwrappedValue;
import org.junit.jupiter.api.Test;

import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatConstraintDescriptor;
import static com.github.jinahya.assertj.validation.ValidationAssertionsTestUtils.applyValidator;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests the {@link jakarta.validation.metadata.ConstraintDescriptor} members that previously had only empty section
 * banners.
 */
class ConstraintDescriptorAssert_Members_Test {

    private static ConstraintViolation<User> blankNameViolation() {
        return applyValidator(v -> v
                .validateValue(User.class, User.PROPERTY_NAME_NAME, null)
                .iterator().next());
    }

    @Test
    void __messageTemplate() {
        assertThatConstraintDescriptor(blankNameViolation().getConstraintDescriptor())
                .hasMessageTemplate("{jakarta.validation.constraints.NotBlank.message}");
    }

    @Test
    void __attributes() {
        assertThatConstraintDescriptor(blankNameViolation().getConstraintDescriptor())
                .hasAttribute("message", "{jakarta.validation.constraints.NotBlank.message}");
    }

    @Test
    void __payload() {
        assertThatConstraintDescriptor(blankNameViolation().getConstraintDescriptor())
                .doesNotHaveAnyPayload();
    }

    @Test
    void __reportAsSingleViolation() {
        assertThatConstraintDescriptor(blankNameViolation().getConstraintDescriptor())
                .isNotReportedAsSingleViolation();
    }

    @Test
    void __valueUnwrapping() {
        assertThatConstraintDescriptor(blankNameViolation().getConstraintDescriptor())
                .hasValueUnwrapping(ValidateUnwrappedValue.DEFAULT);
    }

    @Test
    void __constraintValidatorClasses() {
        assertThatConstraintDescriptor(blankNameViolation().getConstraintDescriptor())
                .extractingConstraintValidatorClasses()
                .isNotEmpty();
    }

    @Test
    void should_verify_validationAppliesTo_through_the_alias_as_hostsConstraintTarget_does() {
        // a property constraint applies to no constraint target
        final var descriptor = blankNameViolation().getConstraintDescriptor();
        assertThatConstraintDescriptor(descriptor).hasValidationAppliesTo(null).hostsConstraintTarget(null);
        assertThatThrownBy(() -> assertThatConstraintDescriptor(descriptor)
                .hasValidationAppliesTo(jakarta.validation.ConstraintTarget.PARAMETERS))
                .hasMessageStartingWith("[validationAppliesTo]");
    }
}
