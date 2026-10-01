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

import org.junit.jupiter.api.Test;

import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatBean;
import static com.github.jinahya.assertj.validation.ValidationAssertionsTestUtils.shouldFail;
import static com.github.jinahya.assertj.validation.ValidationAssertionsTestUtils.shouldPass;
import static com.github.jinahya.assertj.validation.example.user.User.PROPERTY_NAME_AGE;
import static com.github.jinahya.assertj.validation.example.user.User.PROPERTY_NAME_NAME;
import static com.github.jinahya.assertj.validation.example.user.User.invalidName;
import static com.github.jinahya.assertj.validation.example.user.User.newUser;
import static com.github.jinahya.assertj.validation.example.user.User.userOf;
import static com.github.jinahya.assertj.validation.example.user.User.validAge;
import static com.github.jinahya.assertj.validation.example.user.User.validAgeForJunior;
import static com.github.jinahya.assertj.validation.example.user.User.validAgeForSenior;
import static com.github.jinahya.assertj.validation.example.user.User.validName;
import static java.util.concurrent.ThreadLocalRandom.current;

class User_DoesNotHaveValidProperty_Test {

    @Test
    void __() {
        final var validName = current().nextBoolean();
        final var validAge = current().nextBoolean();
        final var user = newUser(validName, validAge);
        final var assertion = assertThatBean(user);
        if (validName) {
            shouldFail(() -> assertion.doesNotHaveValidProperty(PROPERTY_NAME_NAME));
        } else {
            shouldPass(() -> assertion.doesNotHaveValidProperty(PROPERTY_NAME_NAME));
        }
        if (validAge) {
            shouldFail(() -> assertion.doesNotHaveValidProperty(PROPERTY_NAME_AGE));
        } else {
            shouldPass(() -> assertion.doesNotHaveValidProperty(PROPERTY_NAME_AGE));
        }
    }

    @Test
    void __validName() {
        final var user = userOf(validName(), validAge());
        final var assertion = assertThatBean(user);
        {
            shouldFail(() -> assertion.doesNotHaveValidProperty(PROPERTY_NAME_NAME));
        }
        {
            shouldFail(() -> assertion.doesNotHaveValidProperty(PROPERTY_NAME_AGE));
        }
    }

    @Test
    void __invalidName() {
        final var user = userOf(invalidName(), validAge());
        final var assertion = assertThatBean(user);
        {
            shouldPass(() -> assertion.doesNotHaveValidProperty(PROPERTY_NAME_NAME));
        }
        {
            shouldFail(() -> assertion.doesNotHaveValidProperty(PROPERTY_NAME_AGE));
        }
    }

    @Test
    void __validAgeForJunior() {
        final var user = userOf(validName(), validAgeForJunior());
        final var assertion = assertThatBean(user);
        {
            shouldFail(() -> assertion.doesNotHaveValidProperty(PROPERTY_NAME_NAME));
        }
        {
            assertion.targetingGroups(Junior.class);
            shouldFail(() -> assertion.doesNotHaveValidProperty(PROPERTY_NAME_AGE));
        }
        {
            assertion.targetingGroups(Senior.class);
            shouldPass(() -> assertion.doesNotHaveValidProperty(PROPERTY_NAME_AGE));
        }
    }

    @Test
    void __validAgeForSenior() {
        final var user = userOf(validName(), validAgeForSenior());
        final var assertion = assertThatBean(user);
        {
            shouldFail(() -> assertion.doesNotHaveValidProperty(PROPERTY_NAME_NAME));
        }
        {
            assertion.targetingGroups(Junior.class);
            shouldPass(() -> assertion.doesNotHaveValidProperty(PROPERTY_NAME_AGE));
        }
        {
            assertion.targetingGroups(Senior.class);
            shouldFail(() -> assertion.doesNotHaveValidProperty(PROPERTY_NAME_AGE));
        }
    }
}
