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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatBean;
import static com.github.jinahya.assertj.validation.example.user.JuniorRegistration.juniorRegistrationOf;
import static com.github.jinahya.assertj.validation.example.user.User.newJunior;
import static com.github.jinahya.assertj.validation.example.user.User.newSenior;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("assertThatBean(JuniorRegistration).isValid()")
class JuniorRegistration_IsValid_Test {

    @DisplayName("user is null")
    @Test
    void __UserNull() {
        final var registration = JuniorRegistration.of(null);
        final var assertion = assertThatBean(registration);
        assertThatThrownBy(assertion::isValid).isInstanceOf(AssertionError.class);
    }

    @DisplayName("user is junior")
    @Test
    void __UserJunior() {
        final var registration = juniorRegistrationOf(newJunior());
        final var assertion = assertThatBean(registration);
        assertThatCode(assertion::isValid).doesNotThrowAnyException();
    }

    @DisplayName("user is senior")
    @Test
    void __UserSenior() {
        final var registration = juniorRegistrationOf(newSenior());
        final var assertion = assertThatBean(registration);
        assertThatThrownBy(assertion::isValid).isInstanceOf(AssertionError.class);
    }

    @DisplayName("user invalid")
    @Test
    void __UserInvalid() {
        {
            final var registration = juniorRegistrationOf(newJunior().invalidate());
            final var assertion = assertThatBean(registration);
            assertThatThrownBy(assertion::isValid).isInstanceOf(AssertionError.class);
        }
        {
            final var registration = juniorRegistrationOf(newSenior().invalidate());
            final var assertion = assertThatBean(registration);
            assertThatThrownBy(assertion::isValid).isInstanceOf(AssertionError.class);
        }
    }
}
