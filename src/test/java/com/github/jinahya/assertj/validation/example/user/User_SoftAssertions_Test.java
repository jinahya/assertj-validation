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

import com.github.jinahya.assertj.validation.BeanAssert;
import com.github.jinahya.assertj.validation.ConstraintViolationAssert;
import com.github.jinahya.assertj.validation.ConstructorAssert;
import com.github.jinahya.assertj.validation.PathAssert;
import com.github.jinahya.assertj.validation.PropertyAssert;
import com.github.jinahya.assertj.validation.example.service.Greeter;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import org.assertj.core.api.BDDSoftAssertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static com.github.jinahya.assertj.validation.ValidationAssertionsTestUtils.applyValidator;
import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatConstraintViolation;
import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatPath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Validation assertions made through {@link SoftAssertions#proxy(Class, Class, Object)} are collected, not thrown.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
class User_SoftAssertions_Test {

    private static ConstraintViolation<User> violationOf(final User user) {
        return applyValidator(v -> v.validate(user).iterator().next());
    }

    @DisplayName("bean, property and constructor assertions are collected, and reported by assertAll()")
    @Test
    void should_collect_bean_property_and_constructor_failures() throws Exception {
        final var softly = new SoftAssertions();
        final var invalid = User.of(null, -1);
        softly.proxy(BeanAssert.class, Object.class, invalid).isValid();
        softly.proxy(BeanAssert.class, Object.class, invalid).hasValidProperty(User.PROPERTY_NAME_AGE);
        softly.proxy(BeanAssert.class, Object.class, User.newValidUser()).isNotValid();
        softly.proxy(PropertyAssert.class, Object.class, null).isValidFor(User.class, User.PROPERTY_NAME_NAME);
        softly.proxy(BeanAssert.class, Object.class, new Greeter("Jane"))
                .hasValidParameters(Greeter.class.getMethod(Greeter.METHOD_NAME_GREET, String.class, int.class),
                                    " ", 1);
        softly.proxy(ConstructorAssert.class, Constructor.class, Greeter.class.getConstructor(String.class))
                .hasValidParameters((Object) " ");
        assertThat(softly.errorsCollected()).hasSize(6);
        assertThatThrownBy(softly::assertAll).isInstanceOf(AssertionError.class);
    }

    @DisplayName("a null bean is one collected failure, not a NullPointerException")
    @Test
    void should_collect_a_null_bean_as_one_failure() {
        final var softly = new SoftAssertions();
        softly.proxy(BeanAssert.class, Object.class, null).isValid();
        assertThat(softly.errorsCollected()).hasSize(1);
    }

    @DisplayName("BDDSoftAssertions collects them alike, and a passing chain collects nothing")
    @Test
    void should_collect_through_BDDSoftAssertions() {
        final var softly = new BDDSoftAssertions();
        softly.proxy(BeanAssert.class, Object.class, User.newValidUser()).isValid().hasValidProperty(
                User.PROPERTY_NAME_NAME);
        assertThat(softly.errorsCollected()).isEmpty();
        softly.proxy(BeanAssert.class, Object.class, User.of(null, 0)).isValid().isNotValid();
        assertThat(softly.errorsCollected()).hasSize(1);
    }

    @DisplayName("assertions on a violation are collected")
    @Test
    void should_collect_violation_failures() {
        final ConstraintViolation<User> violation = violationOf(User.of(null, 0));
        final var softly = new SoftAssertions();
        softly.proxy(ConstraintViolationAssert.class, ConstraintViolation.class, violation)
                .hasMessageTemplate("{wrong}")
                .hasInvalidValue("wrong");
        assertThat(softly.errorsCollected()).hasSize(2);
    }

    @DisplayName("assertions on a path are collected")
    @Test
    void should_collect_path_failures() {
        final Path path = violationOf(User.of(null, 0)).getPropertyPath();
        final var softly = new SoftAssertions();
        softly.proxy(PathAssert.class, Path.class, path).hasSize(2);
        assertThat(softly.errorsCollected()).hasSize(1);
    }

    /**
     * Released AssertJ re-proxies only the results of its own navigation methods ({@code extracting}, {@code asInstanceOf},
     * ...), so a chain navigating into a nested assertion of this module is collected through {@code check(...)}.
     */
    @DisplayName("a navigating chain is collected through check(...)")
    @Test
    void should_collect_a_navigating_chain_through_check() {
        final ConstraintViolation<User> violation = violationOf(User.of(null, 0));
        final var softly = new SoftAssertions();
        softly.check(() -> assertThatConstraintViolation(violation).extractingPropertyPath().hasSize(2));
        assertThat(softly.errorsCollected()).hasSize(1);
        softly.check(() -> assertThatPath(violation.getPropertyPath()).extractingNode(0).hasName("wrong"));
        assertThat(softly.errorsCollected()).hasSize(2);
    }
}
