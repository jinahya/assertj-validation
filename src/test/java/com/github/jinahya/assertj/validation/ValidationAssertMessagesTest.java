package com.github.jinahya.assertj.validation;

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
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests {@link ValidationAssertMessages}, the class which renders every failure message this library produces.
 */
class ValidationAssertMessagesTest {

    static class Bean {

        Bean(final String name, final int age) {
            this.name = name;
            this.age = age;
        }

        @NotBlank
        final String name;

        @Max(10)
        final int age;
    }

    private static Set<ConstraintViolation<Bean>> validate(final Bean bean) {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            return factory.getValidator().validate(bean);
        }
    }

    // ------------------------------------------------------------------------------ format(ConstraintViolation)
    @DisplayName("format(violation) renders all four fields")
    @Test
    void formatOne__() {
        final var violation = validate(new Bean("  ", 1)).iterator().next();
        final var formatted = ValidationAssertMessages.format(violation);
        assertThat(formatted)
                .contains("message        : " + violation.getMessage())
                .contains("propertyPath   : " + violation.getPropertyPath())
                .contains("rootBeanClass  : " + violation.getRootBeanClass())
                .contains("messageTemplate: " + violation.getMessageTemplate());
    }

    @DisplayName("format(violation) uses real newlines, not a literal %n")
    @Test
    void formatOne__NoLiteralPercentN() {
        final var violation = validate(new Bean("  ", 1)).iterator().next();
        final var formatted = ValidationAssertMessages.format(violation);
        assertThat(formatted)
                .as("a literal %n would mean the message was assembled by concatenation, not formatting")
                .doesNotContain("%n")
                .hasLineCount(4);
    }

    @DisplayName("format(null violation) throws")
    @Test
    void formatOne__Null() {
        assertThatThrownBy(() -> ValidationAssertMessages.format((ConstraintViolation<?>) null))
                .isInstanceOf(NullPointerException.class);
    }

    // ---------------------------------------------------------------------------------------------- format(Set)
    @DisplayName("format(set) renders one entry per violation, each bulleted")
    @Test
    void formatSet__Many() {
        final var violations = validate(new Bean("  ", 100));
        assertThat(violations).as("the fixture must produce two violations").hasSize(2);
        final var formatted = ValidationAssertMessages.format(violations);
        assertThat(formatted.lines().filter(l -> l.startsWith("-> ")).count())
                .as("one bullet per violation")
                .isEqualTo(2);
    }

    @DisplayName("format(set) separates entries with real line separators")
    @Test
    void formatSet__Separator() {
        final var violations = validate(new Bean("  ", 100));
        final var formatted = ValidationAssertMessages.format(violations);
        assertThat(formatted)
                .as("the regression guarded here: entries were once joined on a literal %n")
                .doesNotContain("%n")
                .contains(System.lineSeparator());
    }

    @DisplayName("format(empty set) throws")
    @Test
    void formatSet__Empty() {
        assertThatThrownBy(() -> ValidationAssertMessages.format(Set.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("empty");
    }

    @DisplayName("format(null set) throws")
    @Test
    void formatSet__Null() {
        assertThatThrownBy(() -> ValidationAssertMessages.format((Set<ConstraintViolation<?>>) null))
                .isInstanceOf(NullPointerException.class);
    }

    @DisplayName("the class cannot be instantiated")
    @Test
    void __NotInstantiable() throws Exception {
        final var constructor = ValidationAssertMessages.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        assertThatThrownBy(constructor::newInstance).hasCauseInstanceOf(AssertionError.class);
    }
}
