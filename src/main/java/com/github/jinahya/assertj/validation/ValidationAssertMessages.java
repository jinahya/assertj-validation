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
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assert;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

final class ValidationAssertMessages {

    static String format(final ConstraintViolation<?> violation) {
        Objects.requireNonNull(violation, "violation is null");
        return String.format(
                "message        : %1$s%n" +
                "propertyPath   : %2$s%n" +
                "rootBeanClass  : %3$s%n" +
                "messageTemplate: %4$s",
                violation.getMessage(),
                violation.getPropertyPath(),
                violation.getRootBeanClass(),
                violation.getMessageTemplate()
        );
    }

    static String format(final Set<? extends ConstraintViolation<?>> violations) {
        if (Objects.requireNonNull(violations, "violations is null").isEmpty()) {
            throw new IllegalArgumentException("empty violations");
        }
        // each entry is bulleted, and its following lines are indented to the width of the bullet, so they line up
        return violations.stream()
                .map(v -> format(v).lines().collect(Collectors.joining(System.lineSeparator() + "\t   ", "\t-> ", "")))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    /**
     * Describes specified assertion, navigated to an attribute, by the name of the attribute, so that its failure says
     * which attribute differed.
     * <p>
     * A description the assertion already carries, set by the caller on the assertion it navigated from, is kept and
     * followed by the attribute name: {@code [case 42: invalidValue]}.
     *
     * @param navigated the assertion navigated to the attribute.
     * @param attribute the name of the attribute.
     * @param <A>       assertion type parameter
     * @return {@code navigated}, described.
     */
    static <A extends Assert<?, ?>> A describedAsAttribute(final A navigated, final String attribute) {
        Objects.requireNonNull(navigated, "navigated is null");
        Objects.requireNonNull(attribute, "attribute is null");
        final String description = navigated instanceof AbstractAssert<?, ?>
                                   ? ((AbstractAssert<?, ?>) navigated).descriptionText()
                                   : null;
        // the text goes in as an argument, so that a '%' in the caller's description is not a format specifier
        navigated.as("%s", description == null || description.isEmpty() ? attribute : description + ": " + attribute);
        return navigated;
    }

    private ValidationAssertMessages() {
        throw new AssertionError("instantiation is not allowed");
    }
}
