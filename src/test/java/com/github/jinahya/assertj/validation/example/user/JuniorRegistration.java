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

class JuniorRegistration
        extends Registration {

    static JuniorRegistration of(final User user) {
        return Registration.of(JuniorRegistration::new, user);
    }

    static JuniorRegistration juniorRegistrationOf(final User user) {
        return of(user);
    }

    JuniorRegistration(@Junior final User user) {
        super(user);
    }

    private JuniorRegistration() {
        super(null);
    }

    @Junior
    @Override
    User getUser() {
        return super.getUser();
    }
}
