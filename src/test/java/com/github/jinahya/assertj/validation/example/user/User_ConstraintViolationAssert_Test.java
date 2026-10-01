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

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ElementKind;
import jakarta.validation.Path;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatConstraintDescriptor;
import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatConstraintViolation;
import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatProperty;
import static com.github.jinahya.assertj.validation.ValidationAssertionsTestUtils.shouldFail;
import static com.github.jinahya.assertj.validation.ValidationAssertionsTestUtils.shouldPass;
import static com.github.jinahya.assertj.validation.example.user.User.invalidAge;
import static com.github.jinahya.assertj.validation.example.user.User.invalidName;
import static com.github.jinahya.assertj.validation.example.user.User.validAge;
import static com.github.jinahya.assertj.validation.example.user.User.validName;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class User_ConstraintViolationAssert_Test {

    @Nested
    class NameTest {

        @Test
        void __ValidName() {
            final var name = validName();
            final var assertion = assertThatProperty(name);
            shouldPass(
                    () -> assertion.isValidFor(User.class, "name", s -> {
                        assertThat(s).isEmpty();
                    })
            );
        }

        @Test
        void __InvalidName() {
            final var name = invalidName();
            final var assertion = assertThatProperty(name);
            final var violations = new HashSet<ConstraintViolation<User>>();
            shouldFail(
                    () -> assertion.isValidFor(User.class, "name", violations::addAll)
            );
            assertThat(violations).hasSize(1).allSatisfy(cv -> {
                assertThatConstraintViolation(cv)
                        .hasNoExecutableParameters()
                        .hasNoExecutableReturnValue()
                        .hasInvalidValue(name)
                        .doesNotHaveLeafBean()
                        .doesNotHaveRootBean()
                        .hasRootBeanClass(User.class);
                assertThatConstraintViolation(cv).extractingExecutableParameters().isNull();
                assertThatConstraintViolation(cv).extractingExecutableReturnValue().isNull();
                assertThatConstraintViolation(cv).extractingInvalidValue().isEqualTo(name);
                assertThatConstraintViolation(cv).extractingLeafBean().isNull();
                assertThatConstraintViolation(cv).extractingRootBean().isNull();
                assertThatConstraintViolation(cv).extractingRootBeanClass().isEqualTo(User.class);
                {
                    assertThatConstraintViolation(cv)
                            .extractingConstraintDescriptor()
                            .extractingAnnotation()
                            .isInstanceOf(NotBlank.class);
                    assertThatConstraintDescriptor(cv.getConstraintDescriptor())
                            .extractingAnnotation()
                            .isInstanceOf(NotBlank.class);
                }
                {
                    assertThatConstraintViolation(cv)
                            .extractingPropertyPath()
                            .hasSize(1)
                            .hasNodeSatisfying(0, a -> {
                                a.doesNotHaveIndex()
                                        .hasKey(null)
                                        .hasKind(ElementKind.PROPERTY)
                                        .hasName(User.PROPERTY_NAME_NAME)
                                        .isNotInIterable();
                            })
                            .hasPropertyNodeSatisfying(0, a -> {
                                a.hasContainerClass(null);
                                a.doesNotHaveTypeArgumentIndex();
                            })
                            .element(0)
                            .isInstanceOf(Path.PropertyNode.class)
                            .doesNotHaveIndex()
                            .hasKey(null)
                            .hasKind(ElementKind.PROPERTY)
                            .hasName(User.PROPERTY_NAME_NAME)
                            .isNotInIterable()
                    ;
                }
            });
        }
    }

    @Nested
    class AgeTest {

        @Test
        void __ValidAge() {
            final var age = validAge();
            final var assertion = assertThatProperty(age);
            shouldPass(
                    () -> assertion.isValidFor(User.class, "age", s -> {
                        assertThat(s).isEmpty();
                    })
            );
        }

        @Test
        void __InvalidAge() {
            final var age = invalidAge();
            final var assertion = assertThatProperty(age);
            final var violations = new HashSet<ConstraintViolation<User>>();
            shouldFail(
                    () -> assertion.isValidFor(User.class, "age", violations::addAll)
            );
            assertThat(violations).hasSize(1).allSatisfy(cv -> {
                assertThatConstraintViolation(cv)
                        .hasNoExecutableParameters()
                        .hasNoExecutableReturnValue()
                        .hasInvalidValue(age)
                        .doesNotHaveLeafBean()
                        .doesNotHaveRootBean()
                        .hasRootBeanClass(User.class);
                assertThatConstraintViolation(cv).extractingExecutableParameters().isNull();
                assertThatConstraintViolation(cv).extractingExecutableReturnValue().isNull();
                assertThatConstraintViolation(cv).extractingInvalidValue().isEqualTo(age);
                assertThatConstraintViolation(cv).extractingLeafBean().isNull();
                assertThatConstraintViolation(cv).extractingRootBean().isNull();
                assertThatConstraintViolation(cv).extractingRootBeanClass().isEqualTo(User.class);
                {
                    assertThatConstraintViolation(cv)
                            .extractingConstraintDescriptor()
                            .extractingAnnotation()
                            .isInstanceOfAny(PositiveOrZero.class, Min.class, Max.class);
                    assertThatConstraintDescriptor(cv.getConstraintDescriptor())
                            .extractingAnnotation()
                            .isInstanceOfAny(PositiveOrZero.class, Min.class, Max.class);
                }
                {
                    assertThatConstraintViolation(cv)
                            .extractingPropertyPath()
                            .hasSize(1)
                            .element(0)
                            .hasName(User.PROPERTY_NAME_AGE);
                }
                {
                    assertThatConstraintViolation(cv)
                            .extractingConstraintDescriptor()
                            .extractingAnnotation()
                            .isInstanceOfAny(PositiveOrZero.class, Min.class, Max.class);
                    log.debug("{}", cv.getConstraintDescriptor().getValidationAppliesTo());
                    assertThatConstraintViolation(cv)
                            .extractingConstraintDescriptor()
                            .doesNotHostAnyConstraintTarget();
                }
                {
                    assertThatConstraintViolation(cv)
                            .extractingPropertyPath()
                            .hasSize(1)
                            .hasNodeSatisfying(0, a -> {
                                a.doesNotHaveIndex()
                                        .hasKey(null)
                                        .hasKind(ElementKind.PROPERTY)
                                        .hasName(User.PROPERTY_NAME_AGE)
                                        .isNotInIterable();
                            })
                            .hasPropertyNodeSatisfying(0, a -> {
                                a.hasContainerClass(null);
                                a.doesNotHaveTypeArgumentIndex();
                            })
                            .element(0)
                            .isInstanceOf(Path.PropertyNode.class)
                            .doesNotHaveIndex()
                            .hasKey(null)
                            .hasKind(ElementKind.PROPERTY)
                            .hasName(User.PROPERTY_NAME_AGE)
                            .isNotInIterable();
                }
            });
        }
    }
}
