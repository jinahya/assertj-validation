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

import static java.util.Collections.unmodifiableSet;

final class ValidationAssertDelegate {

    /**
     * Holds the default {@link Validator}, and the factory it came from.
     * <p>
     * The factory is deliberately <em>never</em> closed. {@link ValidatorFactory#close()} forbids any further use
     * of the validators it produced, so a factory whose validator outlives it must not be closed; and rebuilding
     * one per assertion costs a classpath scan plus a {@code META-INF/validation.xml} parse. For a test-scoped
     * library, holding one for the life of the JVM is the right trade &mdash; process exit reclaims it.
     * <p>
     * Initialization is deferred to first use of the default: a suite which always supplies its own validator
     * never builds one. Class initialization makes that lazy and thread-safe without locking.
     */
    private static final class DefaultValidatorHolder {

        private static final ValidatorFactory FACTORY = Validation.buildDefaultValidatorFactory();

        private static final Validator INSTANCE = FACTORY.getValidator();

        private DefaultValidatorHolder() {
            throw new AssertionError("instantiation is not allowed");
        }
    }

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

    /**
     * Returns the validator to assert with: the one configured on this delegate, or the shared default.
     *
     * @return a validator; never {@code null}.
     */
    Validator getValidator() {
        return validator != null ? validator : DefaultValidatorHolder.INSTANCE;
    }

    /**
     * Configures the validator to assert with.
     *
     * @param validator the validator; {@code null} to fall back to the shared default.
     */
    void setValidator(final Validator validator) {
        this.validator = validator;
    }

    final Set<Class<?>> groups = new HashSet<>();

    final Set<ConstraintViolation<?>> violations = new HashSet<>();

    private Validator validator;
}
