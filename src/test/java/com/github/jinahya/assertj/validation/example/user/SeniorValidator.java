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

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SeniorValidator
        implements ConstraintValidator<Senior, User> {

    private static final int MIN_AGE_INCLUSIVE = UserConstants.MIN_AGE_FOR_SENIOR;

    @Override
    public void initialize(final Senior junior) {
        // empty
    }

    @Override
    public boolean isValid(final User value, final ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return value.getAge() >= MIN_AGE_INCLUSIVE;
    }
}
