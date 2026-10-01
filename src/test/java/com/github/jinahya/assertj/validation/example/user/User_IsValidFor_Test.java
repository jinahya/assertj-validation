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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatProperty;
import static com.github.jinahya.assertj.validation.ValidationAssertionsTestUtils.shouldFail;
import static com.github.jinahya.assertj.validation.ValidationAssertionsTestUtils.shouldPass;
import static com.github.jinahya.assertj.validation.example.user.User.PROPERTY_NAME_AGE;
import static com.github.jinahya.assertj.validation.example.user.User.PROPERTY_NAME_NAME;
import static com.github.jinahya.assertj.validation.example.user.User.invalidAge;
import static com.github.jinahya.assertj.validation.example.user.User.invalidName;
import static com.github.jinahya.assertj.validation.example.user.User.validAge;
import static com.github.jinahya.assertj.validation.example.user.User.validAgeForJunior;
import static com.github.jinahya.assertj.validation.example.user.User.validAgeForSenior;
import static com.github.jinahya.assertj.validation.example.user.User.validName;

@DisplayName("isValidFor")
@Slf4j
class User_IsValidFor_Test {

    @DisplayName("name")
    @Nested
    class NameTest {

        @DisplayName("valid name isValidFor User#name")
        @Test
        void __Valid() {
            final var name = validName();
            final var assertion = assertThatProperty(name);
            shouldPass(() -> assertion.isValidFor(User.class, PROPERTY_NAME_NAME));
        }

        @DisplayName("invalid name isValidFor User#name")
        @Test
        void __Invalid() {
            final var name = invalidName();
            final var assertion = assertThatProperty(name);
            shouldFail(() -> assertion.isValidFor(User.class, PROPERTY_NAME_NAME));
        }
    }

    @DisplayName("age")
    @Nested
    class AgeTest {

        @DisplayName("validAge() should be valid for User#age")
        @Test
        void __ValidAge() {
            final var age = validAge();
            final var assertion = assertThatProperty(age);
            shouldPass(() -> assertion.isValidFor(User.class, PROPERTY_NAME_AGE));
        }

        @DisplayName("validAgeForJunior() should be valid for User#age targeting Junior.class")
        @Test
        void __ValidAgeForJunior() {
            final var age = validAgeForJunior();
            final var assertion = assertThatProperty(age);
            shouldPass(() -> assertion.isValidFor(User.class, PROPERTY_NAME_AGE));
            assertion.targetingGroups(Junior.class);
            shouldPass(() -> assertion.isValidFor(User.class, PROPERTY_NAME_AGE));
            assertion.targetingGroups(Senior.class);
            shouldFail(() -> assertion.isValidFor(User.class, PROPERTY_NAME_AGE));
        }

        @Test
        void __ValidAgeForSenior() {
            final var age = validAgeForSenior();
            final var assertion = assertThatProperty(age);
            shouldPass(() -> assertion.isValidFor(User.class, PROPERTY_NAME_AGE));
            assertion.targetingGroups(Senior.class);
            shouldPass(() -> assertion.isValidFor(User.class, PROPERTY_NAME_AGE));
            assertion.targetingGroups(Junior.class);
            shouldFail(() -> assertion.isValidFor(User.class, PROPERTY_NAME_AGE));
        }

        @Test
        void __InvalidAge() {
            final var age = invalidAge();
            final var assertion = assertThatProperty(age);
            shouldFail(() -> assertion.isValidFor(User.class, PROPERTY_NAME_AGE));
        }
    }
}
