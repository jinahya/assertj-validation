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
import static com.github.jinahya.assertj.validation.example.user.Registration.PROPERTY_NAME_USER;
import static com.github.jinahya.assertj.validation.example.user.User.newJunior;
import static com.github.jinahya.assertj.validation.example.user.User.newSenior;
import static org.assertj.core.api.Assertions.assertThatCode;

@Slf4j
class User_IsValidFor_Registration_Test {

    @DisplayName("junior isValidFor JuniorRegistration#user")
    @Test
    void _JuniorRegistration_Junior() {
        final var user = newJunior();
        final var assertion = assertThatProperty(user);
        assertThatCode(() -> assertion.isValidFor(JuniorRegistration.class, PROPERTY_NAME_USER))
                .doesNotThrowAnyException();
    }

    @DisplayName("senior isValidFor SeniorRegistration#user")
    @Test
    void _SeniorRegistration_Senior() {
        final var user = newSenior();
        final var assertion = assertThatProperty(user);
        assertThatCode(() -> assertion.isValidFor(SeniorRegistration.class, PROPERTY_NAME_USER))
                .doesNotThrowAnyException();
    }
}
