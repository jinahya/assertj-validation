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
import org.junit.jupiter.api.Test;

import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatProperty;
import static com.github.jinahya.assertj.validation.example.user.User.PROPERTY_NAME_AGE;
import static com.github.jinahya.assertj.validation.example.user.User.PROPERTY_NAME_NAME;
import static com.github.jinahya.assertj.validation.example.user.User.invalidAge;
import static com.github.jinahya.assertj.validation.example.user.User.invalidName;
import static com.github.jinahya.assertj.validation.example.user.User.validAge;
import static com.github.jinahya.assertj.validation.example.user.User.validName;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("isNotValidFor")
@Slf4j
class User_IsNotValidFor_Test {

    @DisplayName("valid name isNotValidFor User#name throws AssertionError")
    @Test
    void __ValidName() {
        {
            final var name = validName();
            final var assertion = assertThatProperty(name);
            assertThatThrownBy(() -> assertion.isNotValidFor(User.class, PROPERTY_NAME_NAME))
                    .isInstanceOf(AssertionError.class);
        }
    }

    @DisplayName("invalid name isNotValidFor User#name does not throw")
    @Test
    void __InvalidName() {
        {
            final var name = invalidName();
            final var assertion = assertThatProperty(name);
            assertThatCode(() -> assertion.isNotValidFor(User.class, PROPERTY_NAME_NAME))
                    .doesNotThrowAnyException();
        }
    }

    @Test
    void __ValidAge() {
        {
            final var age = validAge();
            final var assertion = assertThatProperty(age);
            assertThatThrownBy(() -> assertion.isNotValidFor(User.class, PROPERTY_NAME_AGE))
                    .isInstanceOf(AssertionError.class);
        }
    }

    @Test
    void __InvalidAge() {
        {
            final var age = invalidAge();
            final var assertion = assertThatProperty(age);
            assertThatCode(() -> assertion.isNotValidFor(User.class, PROPERTY_NAME_AGE))
                    .doesNotThrowAnyException();
        }
    }
}
