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

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

final class ValidationAssertMessages {

    static String format(final ConstraintViolation<?> violation) {
        Objects.requireNonNull(violation, "violation is null");
        return String.format(
                "\tmessage        : %1$s%n" +
                "\tpropertyPath   : %2$s%n" +
                "\trootBeanClass  : %3$s%n" +
                "\tmessageTemplate: %4$s",
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
        final Iterator<? extends ConstraintViolation<?>> iterator = violations.iterator();
        return IntStream.range(0, violations.size())
                .mapToObj(i -> String.format("-> %1$s", format(iterator.next())))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private ValidationAssertMessages() {
        throw new AssertionError("instantiation is not allowed");
    }
}
