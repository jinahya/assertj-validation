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

import jakarta.validation.ConstraintViolation;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.presentation.StandardRepresentation;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatConstraintViolation;
import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatPath;
import static com.github.jinahya.assertj.validation.ValidationAssertionsTestUtils.applyValidator;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * The contract every navigation method keeps, as AssertJ's {@code NavigationMethodBaseTest} does for its own: the
 * navigated assertion carries the description, the overriding message and the representation of the assertion it
 * navigated from, both into a failure after a successful navigation and into a failure of the navigation's own guard.
 */
class Navigation_Contract_Test {

    private static final String DESCRIPTION = "case-42-marker";

    private static final String OVERRIDE = "override-42-marker";

    private static final StandardRepresentation REPRESENTATION = new StandardRepresentation() {
        @Override
        public String toStringOf(final Object object) {
            return "repr-42-marker";
        }
    };

    private static ConstraintViolation<User> violation() {
        return applyValidator(v -> v.validate(User.of(null, 0)).iterator().next());
    }

    /**
     * Each case: a name, a navigation from a fresh origin assertion configured by the given operator, and a failing
     * assertion on what it navigated to.
     */
    static Stream<Arguments> navigations() {
        final ConstraintViolation<User> violation = violation();
        return Stream.of(
                Arguments.of(
                        "violation -> propertyPath",
                        (Function<UnaryOperator<AbstractAssert<?, ?>>, Runnable>) c -> () ->
                                ((com.github.jinahya.assertj.validation.AbstractConstraintViolationAssert<?, ?, ?>)
                                        c.apply(assertThatConstraintViolation(violation)))
                                        .extractingPropertyPath().hasSize(9)
                ),
                Arguments.of(
                        "violation -> constraintDescriptor",
                        (Function<UnaryOperator<AbstractAssert<?, ?>>, Runnable>) c -> () ->
                                ((com.github.jinahya.assertj.validation.AbstractConstraintViolationAssert<?, ?, ?>)
                                        c.apply(assertThatConstraintViolation(violation)))
                                        .extractingConstraintDescriptor().isNull()
                ),
                Arguments.of(
                        "path -> node",
                        (Function<UnaryOperator<AbstractAssert<?, ?>>, Runnable>) c -> () ->
                                ((com.github.jinahya.assertj.validation.AbstractPathAssert<?>)
                                        c.apply(assertThatPath(violation.getPropertyPath())))
                                        .extractingNode(0).isNull()
                )
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("navigations")
    void should_carry_the_description(final String name,
                                      final Function<UnaryOperator<AbstractAssert<?, ?>>, Runnable> navigation) {
        assertThatThrownBy(() -> navigation.apply(a -> a.as(DESCRIPTION)).run())
                .hasMessageStartingWith("[" + DESCRIPTION + "]");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("navigations")
    void should_carry_the_overriding_message(final String name,
                                             final Function<UnaryOperator<AbstractAssert<?, ?>>, Runnable> navigation) {
        assertThatThrownBy(() -> navigation.apply(a -> a.withFailMessage(OVERRIDE)).run())
                .hasMessageContaining(OVERRIDE);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("navigations")
    void should_carry_the_representation(final String name,
                                         final Function<UnaryOperator<AbstractAssert<?, ?>>, Runnable> navigation) {
        assertThatThrownBy(() -> navigation.apply(a -> a.withRepresentation(REPRESENTATION)).run())
                .hasMessageContaining("repr-42-marker");
    }

    @org.junit.jupiter.api.Test
    void should_fail_the_navigation_guard_honoring_the_description() {
        assertThatThrownBy(() -> assertThatConstraintViolation((ConstraintViolation<User>) null)
                .as(DESCRIPTION)
                .extractingPropertyPath())
                .hasMessageStartingWith("[" + DESCRIPTION + "]")
                .hasMessageContaining("Expecting actual not to be null");
        assertThatThrownBy(() -> assertThatPath(violation().getPropertyPath())
                .as(DESCRIPTION)
                .extractingNode(5))
                .hasMessageStartingWith("[" + DESCRIPTION + "]");
    }
}
