package com.github.jinahya.assertj.validation;

/*-
 * #%L
 * assertj-validation
 * %%
 * Copyright (C) 2021 Jinahya, Inc.
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
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.groups.Default;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.Collections.unmodifiableSet;

final class ValidationAssertDelegate {

    private static final Supplier<? extends Validator> DEFAULT_VALIDATOR_SUPPLIER = () -> {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            return factory.getValidator();
        }
    };

    /**
     * Returns groups targeted.
     *
     * @return the targeting group; never {@code null}.
     */
    Class<?>[] getGroups() {
        if (groups.isEmpty()) {
            return new Class<?>[]{Default.class};
        }
        return groups.toArray(new Class<?>[0]);
    }

    void setGroups(final Class<?>... groups) {
        this.groups.clear();
        if (groups != null) {
            for (final Class<?> group : groups) {
                if (group == null) {
                    throw new IllegalArgumentException("groups contains null");
                }
                this.groups.add(group);
            }
        }
    }

    @SuppressWarnings({"unchecked"})
    <T> Set<ConstraintViolation<T>> getViolations() {
        final Set<ConstraintViolation<T>> set = new HashSet<>();
        violations.forEach(v -> set.add((ConstraintViolation<T>) v));
        return set;
    }

    <T> void setViolations(final Set<ConstraintViolation<T>> violations) {
        Objects.requireNonNull(violations, "violations is null");
        this.violations.clear();
        this.violations.addAll(violations);
    }

    <T> void acceptViolations(final Consumer<? super Set<ConstraintViolation<T>>> consumer) {
        try {
            consumer.accept(unmodifiableSet(getViolations()));
        } catch (final Exception e) {
            throw new RuntimeException("failed to accept violations to [" + consumer + "]", e);
        }
    }

    Validator getValidator() {
        return Objects.requireNonNull(validatorSupplier.get(), "null supplied by " + validatorSupplier);
    }

    void setValidator(final Validator validator) {
        if (validator != null) {
            validatorSupplier = () -> validator;
            return;
        }
        validatorSupplier = DEFAULT_VALIDATOR_SUPPLIER;
    }

    void setValidatorSupplier(final Supplier<? extends Validator> validatorSupplier) {
        if (validatorSupplier != null) {
            this.validatorSupplier = validatorSupplier;
            return;
        }
        this.validatorSupplier = DEFAULT_VALIDATOR_SUPPLIER;
    }

    final Set<Class<?>> groups = new HashSet<>();

    final Set<ConstraintViolation<?>> violations = new HashSet<>();

    private Supplier<? extends Validator> validatorSupplier = DEFAULT_VALIDATOR_SUPPLIER;
}
