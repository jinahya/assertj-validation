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

import org.assertj.core.api.Condition;

final class UserConditions {

    public static final Condition<User> JUNIOR = new Condition<>(
            v -> v.getAge() <= UserConstants.MAX_AGE_FOR_JUNIOR,
            "a junior user whose `age` is less than or equal to [%d]",
            UserConstants.MAX_AGE_FOR_JUNIOR
    );

    public static final Condition<User> SENIOR = new Condition<>(
            v -> v.getAge() >= UserConstants.MIN_AGE_FOR_SENIOR,
            "a senior user whose 'age' is greater than or equal to [%d]",
            UserConstants.MIN_AGE_FOR_SENIOR
    );

    private UserConditions() {
        throw new AssertionError("instantiation is not allowed");
    }
}
