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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.groups.Default;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("ValidationAssertDelegate")
class ValidationAssertDelegateTest {

    @Nested
    class GroupsTest {

        @DisplayName("getGroups()")
        @Test
        void groups_NotNullEmpty_() {
            final var delegate = new ValidationAssertDelegate();
            assertThat(delegate.groups).isEmpty();
            assertThat(delegate.getGroups()).hasSize(1).containsOnly(Default.class);
        }

        @DisplayName("setGroups(null)")
        @Test
        void groups__Null() {
            final var delegate = new ValidationAssertDelegate();
            delegate.setGroups((Class<?>[]) null);
            assertThat(delegate.groups).isEmpty();
            assertThat(delegate.getGroups()).hasSize(1).containsOnly(Default.class);
        }

        @DisplayName("setGroups(empty)")
        @Test
        void setGroups__Empty() {
            final var delegate = new ValidationAssertDelegate();
            delegate.setGroups();
            assertThat(delegate.groups).isEmpty();
        }

        @DisplayName("setGroups(not-empty)")
        @Test
        void setGroups__() {
            final var delegate = new ValidationAssertDelegate();
            delegate.setGroups(Object.class);
            assertThat(delegate.groups).isNotEmpty().hasSize(1);
        }
    }

    @Nested
    class ValidatorTest {

        @DisplayName("getValidator()")
        @Test
        void getValidator_NotNull_() {
            final var delegate = new ValidationAssertDelegate();
            final var validator = delegate.getValidator();
            assertThat(validator).isNotNull();
        }

        @DisplayName("validator(null)")
        @Test
        void setValidator__Null() {
            final var delegate = new ValidationAssertDelegate();
            assertThatCode(() -> delegate.setValidator(null)).doesNotThrowAnyException();
        }

        @DisplayName("validator(null)")
        @Test
        void setValidator__Null_() {
            final var delegate = new ValidationAssertDelegate();
            final Validator validator;
            try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
                validator = factory.getValidator();
            }
            delegate.setValidator(validator);
            assertThat(delegate.getValidator()).isSameAs(validator);
        }
    }
}
