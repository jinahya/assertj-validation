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

import lombok.AccessLevel;
import lombok.Getter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.function.Function;

abstract class Registration {

    static final String PROPERTY_NAME_USER = "user";

    static <T extends Registration> T of(final Function<? super User, ? extends T> initializer, final User user) {
        return initializer.apply(user);
    }

    Registration(final User user) {
        super();
        this.user = user;
    }

    @Override
    public String toString() {
        return super.toString() + '{' +
               "user=" + user +
               '}';
    }

    @Valid
    @NotNull
    @Getter(AccessLevel.PACKAGE)
    final User user;
}
