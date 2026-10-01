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
import org.junit.jupiter.api.Test;

import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatBean;
import static com.github.jinahya.assertj.validation.example.user.User.newUser;
import static com.github.jinahya.assertj.validation.example.user.User.newValidUser;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
class User_IsNotValid_Test {

    @Test
    void __() {
        {
            final var user = newValidUser();
            final var assertion = assertThatBean(user);
            assertThatThrownBy(assertion::isNotValid).isInstanceOf(AssertionError.class);
        }
        {
            final var user = newUser(false, true);
            final var assertion = assertThatBean(user);
            assertThatCode(assertion::isNotValid).doesNotThrowAnyException();
        }
        {
            final var user = newUser(true, false);
            final var assertion = assertThatBean(user);
            assertThatCode(assertion::isNotValid).doesNotThrowAnyException();
        }
        {
            final var user = newUser(false, false);
            final var assertion = assertThatBean(user);
            assertThatCode(assertion::isNotValid).doesNotThrowAnyException();
        }
    }
}
