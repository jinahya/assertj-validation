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
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.Objects;

import static com.github.jinahya.assertj.validation.example.user.UserConstants.MAX_AGE;
import static com.github.jinahya.assertj.validation.example.user.UserConstants.MAX_AGE_FOR_JUNIOR;
import static com.github.jinahya.assertj.validation.example.user.UserConstants.MIN_AGE;
import static com.github.jinahya.assertj.validation.example.user.UserConstants.MIN_AGE_FOR_SENIOR;
import static java.util.concurrent.ThreadLocalRandom.current;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class User {

    static final String PROPERTY_NAME_NAME = "name";

    static final String PROPERTY_NAME_AGE = "age";

    static String validName() {
        return "name";
    }

    static String invalidName() {
        return current().nextBoolean() ? null : (current().nextBoolean() ? "" : "   ");
    }

    static int validAge() {
        return current().nextInt(MAX_AGE + 1);
    }

    static int validAgeForJunior() {
        return current().nextInt(MIN_AGE, MAX_AGE_FOR_JUNIOR + 1);
    }

    static int validAgeForSenior() {
        return current().nextInt(MIN_AGE_FOR_SENIOR, MAX_AGE + 1);
    }

    static int invalidAge() {
        if (true) {
            return ~validAge();
        }
        return (current().nextBoolean()
                ? (current().nextInt() | Integer.MIN_VALUE) // not has been born yet
                : current().nextInt(MAX_AGE + 1, Integer.MAX_VALUE)) // too old
                ;
    }

    static User of(final String name, final int age) {
        return new User(name, age);
    }

    static User userOf(final String name, final int age) {
        return of(name, age);
    }

    static User newUser(final boolean validName, final boolean validAge) {
        final var name = validName ? validName() : invalidName();
        final var age = validAge ? validAge() : invalidAge();
        return of(name, age);
    }

    static User newValidUser() {
        return newUser(true, true);
    }

    static User newJunior() {
        return of(validName(), validAgeForJunior());
    }

    static User newSenior() {
        return of(validName(), validAgeForSenior());
    }

    @Override
    public String toString() {
        return super.toString() + '{' +
               "name=" + name +
               ",age=" + age +
               '}';
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof User)) return false;
        final User that = (User) obj;
        return age == that.age && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }

    @NotBlank
    @Getter(AccessLevel.PACKAGE)
    private final String name;

    @Max(MAX_AGE)
    @Min(value = MIN_AGE_FOR_SENIOR, groups = {Senior.class})
    @Max(value = MAX_AGE_FOR_JUNIOR, groups = {Junior.class})
    @PositiveOrZero
    @Getter(AccessLevel.PACKAGE)
    private final int age;

    User invalidate() {
        object = null;
        return this;
    }

    @NotNull
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private Object object = new Object();
}
