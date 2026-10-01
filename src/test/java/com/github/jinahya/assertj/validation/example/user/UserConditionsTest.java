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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@Slf4j
class UserConditionsTest {

    @Nested
    class JuniorTest {

        @Test
        void _Pass_Junior() {
//            final User user = User.newUser(true, true);
//            user.setAge(UserConstants.MAX_AGE_FOR_JUNIOR);
//            assertThat(user).is(JUNIOR);
        }

        @Test
        void _Fail_Junior() {
//            final User user = User.newUser(true, true);
//            user.setAge(UserConstants.MAX_AGE_FOR_JUNIOR + 1);
//            final var assertion = assertThat(user);
//            assertThatThrownBy(() -> assertion.is(JUNIOR))
//                    .isInstanceOf(AssertionError.class)
//                    .satisfies(ar -> {
//                        log.debug("message: {}", ar.getMessage());
//                    });
        }
    }

    @Nested
    class SeniorTest {

        @Test
        void _Pass_Senior() {
//            final User user = User.newUser(true, true);
//            user.setAge(UserConstants.MIN_AGE_FOR_SENIOR);
//            assertThat(user).is(SENIOR);
        }

        @Test
        void _Fail_Senior() {
//            final User user = User.newUser(true, true);
//            user.setAge(UserConstants.MIN_AGE_FOR_SENIOR - 1);
//            final var assertion = assertThat(user);
//            assertThatThrownBy(() -> assertion.is(SENIOR))
//                    .isInstanceOf(AssertionError.class)
//                    .satisfies(ar -> {
//                        log.debug("message: {}", ar.getMessage());
//                    });
        }
    }
}
