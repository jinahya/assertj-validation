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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatConstraintViolation;
import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatPropertyDescriptor;
import static com.github.jinahya.assertj.validation.ValidationAssertionsTestUtils.applyValidator;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * A {@code has…(expected)} failure names the attribute it compared, and keeps the caller's description.
 */
class User_AttributeFailureMessages_Test {

    private static ConstraintViolation<User> violation() {
        return applyValidator(v -> v.validate(User.of(null, 0)).iterator().next());
    }

    @DisplayName("each has…(expected) of a violation names its attribute")
    @Test
    void violation__() {
        final var violation = violation();
        assertThatThrownBy(() -> assertThatConstraintViolation(violation).hasInvalidValue("x"))
                .hasMessageStartingWith("[invalidValue]");
        assertThatThrownBy(() -> assertThatConstraintViolation(violation).hasMessageTemplate("{x}"))
                .hasMessageStartingWith("[messageTemplate]");
        assertThatThrownBy(() -> assertThatConstraintViolation(violation).hasMessage("x"))
                .hasMessageStartingWith("[message]");
        assertThatThrownBy(() -> assertThatConstraintViolation(violation).hasRootBeanClass((Class) String.class))
                .hasMessageStartingWith("[rootBeanClass]");
        assertThatThrownBy(() -> assertThatConstraintViolation(violation).hasLeafBean("x"))
                .hasMessageStartingWith("[leafBean]");
    }

    @DisplayName("a description set by the caller is kept, followed by the attribute")
    @Test
    void description__() {
        assertThatThrownBy(() -> assertThatConstraintViolation(violation()).as("case 42").hasInvalidValue("x"))
                .hasMessageStartingWith("[case 42: invalidValue]");
    }

    @DisplayName("a message overridden by the caller replaces the default one, prefixed by the description as AssertJ does")
    @Test
    void override__() {
        assertThatThrownBy(() -> assertThatConstraintViolation(violation()).withFailMessage("OVERRIDE")
                .hasInvalidValue("x"))
                .hasMessage("[invalidValue] OVERRIDE");
    }

    @DisplayName("a descriptor's has…(expected) names its attribute too")
    @Test
    void descriptor__() {
        final var descriptor = applyValidator(
                v -> v.getConstraintsForClass(User.class).getConstraintsForProperty(User.PROPERTY_NAME_NAME));
        assertThatThrownBy(() -> assertThatPropertyDescriptor(descriptor).hasPropertyName("x"))
                .hasMessageStartingWith("[propertyName]");
    }
}
