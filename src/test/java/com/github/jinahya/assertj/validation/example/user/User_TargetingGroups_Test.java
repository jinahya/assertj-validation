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

import com.github.jinahya.assertj.validation.ValidationAssert;
import org.junit.jupiter.api.Test;

import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatBean;
import static com.github.jinahya.assertj.validation.example.user.User.newJunior;
import static com.github.jinahya.assertj.validation.example.user.User.newSenior;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests {@link ValidationAssert#targetingGroups(Class[])} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class User_TargetingGroups_Test {

    @Test
    void __Junior() {
        final var junior = newJunior();
        final var assertion = assertThatBean(junior);
        {
            assertion.targetingGroups(Junior.class);
            assertThatCode(assertion::isValid).doesNotThrowAnyException();
            assertThatThrownBy(assertion::isNotValid).isInstanceOf(AssertionError.class);
        }
        {
            assertion.targetingGroups(Senior.class);
            assertThatCode(assertion::isNotValid).doesNotThrowAnyException();
            assertThatThrownBy(assertion::isValid).isInstanceOf(AssertionError.class);
        }
    }

    @Test
    void __Senior() {
        final var senior = newSenior();
        final var assertion = assertThatBean(senior);
        {
            assertion.targetingGroups(Senior.class);
            assertThatCode(assertion::isValid).doesNotThrowAnyException();
            assertThatThrownBy(assertion::isNotValid).isInstanceOf(AssertionError.class);
        }
        {
            assertion.targetingGroups(Junior.class);
            assertThatCode(assertion::isNotValid).doesNotThrowAnyException();
            assertThatThrownBy(assertion::isValid).isInstanceOf(AssertionError.class);
        }
    }
}
