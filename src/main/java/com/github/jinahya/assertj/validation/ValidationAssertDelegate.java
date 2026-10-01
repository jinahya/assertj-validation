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
import java.util.function.Function;

import static java.util.Collections.unmodifiableSet;

final class ValidationAssertDelegate {

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
     * Applies the validator to assert with to specified function, and returns the result.
     * <p>
     * There are three cases, and in none of them does this class retain a resource past the call:
     * <ul>
     *   <li>a validator was configured &mdash; it is applied as-is; the caller owns it;</li>
     *   <li>a factory was configured &mdash; a validator is taken from it and applied; the caller owns the
     *       factory, so it is <em>never</em> closed here;</li>
     *   <li>neither &mdash; a {@link ValidatorFactory} is built for this one call and closed before returning,
     *       so the validator never outlives the factory that produced it, which
     *       {@link ValidatorFactory#close()} forbids.</li>
     * </ul>
     *
     * @param function the function to apply the validator to; must be not {@code null}.
     * @param <R>      result type parameter
     * @return the result of applying {@code function}.
     * @apiNote Building a factory costs a classpath scan plus a {@code META-INF/validation.xml} parse. A suite
     * which minds that configures a validator or a factory of its own and keeps the lifecycle.
     */
    <R> R applyValidator(final Function<? super Validator, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        if (validator != null) {
            return function.apply(validator);
        }
        if (validatorFactory != null) {
            return function.apply(validatorFactory.getValidator());
        }
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            return function.apply(factory.getValidator());
        }
    }

    /**
     * Configures the validator to assert with, clearing any configured factory.
     *
     * @param validator the validator; {@code null} to fall back to a per-assertion default.
     */
    void setValidator(final Validator validator) {
        this.validator = validator;
        this.validatorFactory = null;
    }

    /**
     * Configures the factory to take validators from, clearing any configured validator.
     * <p>
     * The factory is never closed here; the caller supplied it, so the caller owns it.
     *
     * @param validatorFactory the factory; {@code null} to fall back to a per-assertion default.
     */
    void setValidatorFactory(final ValidatorFactory validatorFactory) {
        this.validatorFactory = validatorFactory;
        this.validator = null;
    }

    final Set<Class<?>> groups = new HashSet<>();

    final Set<ConstraintViolation<?>> violations = new HashSet<>();

    private Validator validator;

    private ValidatorFactory validatorFactory;
}
