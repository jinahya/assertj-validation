package com.github.jinahya.assertj.validation.example.user;

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
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatBean;
import static com.github.jinahya.assertj.validation.example.user.User.newValidUser;

class User_UsingValidator_Test {

    @DisplayName("usingValidator(null)")
    @Test
    void __Null() {
        final var user = newValidUser();
        final var assertion = assertThatBean(user);
        assertion.usingValidator(null).isValid();
    }

    @DisplayName("usingValidator(non-null)")
    @Test
    void __NonNull() {
        final var user = newValidUser();
        final var assertion = assertThatBean(user);
        final Validator validator;
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
        assertion.usingValidator(validator).isValid();
    }

    @DisplayName("usingValidator(non-null)")
    @Test
    void ___NonNull() {
        final var user = newValidUser();
        final var assertion = assertThatBean(user);
        final Validator validator;
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
        assertion.usingValidator(validator).isValid();
    }
}
