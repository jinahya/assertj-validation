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
import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatProperty;
import static com.github.jinahya.assertj.validation.ValidationAssertionsTestUtils.shouldFail;
import static com.github.jinahya.assertj.validation.ValidationAssertionsTestUtils.shouldPass;
import static com.github.jinahya.assertj.validation.example.user.User.invalidAge;
import static com.github.jinahya.assertj.validation.example.user.User.invalidName;
import static com.github.jinahya.assertj.validation.example.user.User.userOf;
import static com.github.jinahya.assertj.validation.example.user.User.validAge;
import static com.github.jinahya.assertj.validation.example.user.User.validAgeForJunior;
import static com.github.jinahya.assertj.validation.example.user.User.validAgeForSenior;
import static com.github.jinahya.assertj.validation.example.user.User.validName;

class User_README_Test {

    @Test
    void isValidFor__() {
        // @formatter:off
        // @start region=assertThatPropertyName
        shouldPass(() -> assertThatProperty("Jane").isValidFor(User.class, "name"));
        shouldFail(() -> assertThatProperty(  null).isValidFor(User.class, "name"));
        shouldFail(() -> assertThatProperty(    "").isValidFor(User.class, "name"));
        shouldFail(() -> assertThatProperty(   " ").isValidFor(User.class, "name"));
        // @end region=assertThatPropertyName
        // @start region=assertThatPropertyAge
        shouldPass(() -> assertThatProperty(  0).isValidFor(User.class, "age"));
        shouldPass(() -> assertThatProperty( 28).isValidFor(User.class, "age"));
        shouldFail(() -> assertThatProperty( -1).isValidFor(User.class, "age"));
        shouldFail(() -> assertThatProperty(300).isValidFor(User.class, "age"));
        // @end region=assertThatPropertyAge
        assertThatProperty(59)                .isValidFor   (User.class, "age")
                .targetingGroups(Junior.class).isValidFor   (User.class, "age")
                .targetingGroups(Senior.class).isNotValidFor(User.class, "age");

        assertThatProperty(60)                .isValidFor   (User.class, "age")
                .targetingGroups(Junior.class).isNotValidFor(User.class, "age")
                .targetingGroups(Senior.class).isValidFor   (User.class, "age");
        // @formatter:on
    }

    @Test
    void isValidFor__User() {
        shouldPass(() -> {
            assertThatProperty(userOf(validName(), validAge()))
                    .isValidFor(Registration.class, "user");
        });
        shouldPass(() -> {
            assertThatProperty(userOf(validName(), validAgeForJunior()))
                    .isValidFor(JuniorRegistration.class, "user");
        });
        shouldPass(() -> {
            assertThatProperty(userOf(validName(), validAgeForSenior()))
                    .isValidFor(SeniorRegistration.class, "user");
        });
    }

    @Test
    void isValid__() {
        // @formatter:off
        shouldPass(() -> assertThatBean(userOf(  validName(),   validAge())).isValid());
        shouldFail(() -> assertThatBean(userOf(invalidName(),   validAge())).isValid());
        shouldFail(() -> assertThatBean(userOf(  validName(), invalidAge())).isValid());
        shouldFail(() -> assertThatBean(userOf(invalidName(), invalidAge())).isValid());
        // @formatter:on
    }

    @Test
    void isNotValid__() {
        // @formatter:off
        shouldFail(() -> assertThatBean(userOf(  validName(),   validAge())).isNotValid());
        shouldPass(() -> assertThatBean(userOf(invalidName(),   validAge())).isNotValid());
        shouldPass(() -> assertThatBean(userOf(  validName(), invalidAge())).isNotValid());
        shouldPass(() -> assertThatBean(userOf(invalidName(), invalidAge())).isNotValid());
        // @formatter:on
    }
}
