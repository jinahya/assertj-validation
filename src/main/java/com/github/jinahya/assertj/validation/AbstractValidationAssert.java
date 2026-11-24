package com.github.jinahya.assertj.validation;

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
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.groups.Default;
import org.assertj.core.api.AbstractAssert;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.Collections.unmodifiableSet;

/**
 * An abstract base class for assertions which <em>perform</em> validation.
 * <p>
 * It holds only what such an assertion needs: the groups to target, and the validator to validate with &mdash; either
 * one the caller supplied, a factory the caller supplied, or neither, in which case each assertion builds and closes a
 * factory of its own. Assertions which merely inspect what validation produced &mdash; a {@link ConstraintViolation}, a
 * {@link jakarta.validation.Path}, a metadata descriptor &mdash; do not extend this class, because there is nothing
 * left for them to validate.
 * <p>
 * Every assertion method of a subclass which takes a {@link Consumer} of the resulting constraint violations calls it
 * <em>before</em> checking them, with an unmodifiable view of the set, which may be empty. So the consumer sees the
 * violations also when the assertion then fails, and cannot change what is checked. Anything it throws propagates
 * unchanged, and the assertion is not checked.
 * <p>
 * The assertion methods of subclasses are not {@code final}, so that AssertJ's soft assertions can intercept them. On
 * AssertJ 3.x and 4.0.0-M1, a chain navigating from a proxied assertion into a nested one, such as
 * {@code extractingPropertyPath()}, is not soft; wrap such a chain in {@code softly.check(..)}.
 *
 * @param <SELF>   self type parameter
 * @param <ACTUAL> actual type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S119" // <SELF ...>
})
public abstract class AbstractValidationAssert<SELF extends AbstractValidationAssert<SELF, ACTUAL>, ACTUAL>
        extends AbstractAssert<SELF, ACTUAL> {

    /**
     * Creates a new assertion object for verifying specified actual value.
     *
     * @param actual   the value of {@link ACTUAL} to verify.
     * @param selfType a class of {@link SELF}.
     */
    protected AbstractValidationAssert(final ACTUAL actual, final Class<?> selfType) {
        super(actual, selfType);
    }

    // ------------------------------------------------------------------------------------------- configuration

    /**
     * Configures this assertion object to use specified groups targeted for validation.
     *
     * @param groups the validation groups to use; {@code null} or empty for clearing the group.
     * @return this assertion object.
     */
    public final SELF targetingGroups(final Class<?>... groups) {
        this.groups.clear();
        if (groups != null) {
            for (final Class<?> group : groups) {
                if (group == null) {
                    throw new IllegalArgumentException("groups contains null");
                }
                this.groups.add(group);
            }
        }
        return myself;
    }

    /**
     * Configures this assertion object to use specified validator.
     * <p>
     * Supersedes any factory configured by {@link #usingValidatorFactory(ValidatorFactory)}; the two are alternatives
     * and the last call wins.
     *
     * @param validator the validator to use; {@code null} to fall back to a per-assertion default.
     * @return this assertion object.
     * @apiNote This accepts a {@link Validator} rather than only a {@link ValidatorFactory} because the
     *         conversion runs one way only. A factory yields a validator through
     *         {@link ValidatorFactory#getValidator()}, but a validator cannot yield a factory &mdash; and a validator
     *         customized through {@link ValidatorFactory#usingContext()}, one injected by a framework, or a test double
     *         has no factory to offer.
     * @see #usingValidatorFactory(ValidatorFactory)
     */
    public final SELF usingValidator(final Validator validator) {
        this.validator = validator;
        this.validatorFactory = null;
        return myself;
    }

    /**
     * Configures this assertion object to take validators from specified validator factory.
     * <p>
     * <strong>This assertion object never closes {@code factory}. Closing it is the caller's
     * responsibility.</strong> The caller supplied the instance, so the caller owns its lifecycle; each assertion only
     * takes a {@link Validator} from it.
     * {@snippet lang = "java" id = "usingValidatorFactory":
     * try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) { // @highlight substring="try"
     *     assertThatBean(user).usingValidatorFactory(factory).isValid();
     *     assertThatBean(other).usingValidatorFactory(factory).isNotValid();
     * } // the caller closes it, here
     *}
     * <p>
     * Supersedes any validator configured by {@link #usingValidator(Validator)}; the two are alternatives and the last
     * call wins.
     *
     * @param factory the factory to take validators from, <em>not</em> closed by this assertion object; {@code null} to
     *                fall back to a per-assertion default.
     * @return this assertion object.
     * @apiNote This assertion object does not take ownership of {@code factory} and will not call
     *         {@link ValidatorFactory#close()} on it. Closing it also ends the life of every {@link Validator} taken
     *         from it: the specification forbids using those afterwards.
     * @see #usingValidator(Validator)
     * @see ValidatorFactory#close()
     */
    public final SELF usingValidatorFactory(final ValidatorFactory factory) {
        this.validatorFactory = factory;
        this.validator = null;
        return myself;
    }

    // --------------------------------------------------------------------------------------- for subclasses

    /**
     * Returns the groups targeted for validation.
     *
     * @return the targeted groups; never {@code null}, and never empty &mdash; {@link Default} when none was
     *         configured.
     */
    protected final Class<?>[] groups() {
        if (groups.isEmpty()) {
            return new Class<?>[]{Default.class};
        }
        return groups.toArray(new Class<?>[0]);
    }

    /**
     * Applies the validator to assert with to specified function, and returns the result.
     * <p>
     * There are three cases, and in none of them is a resource retained past the call:
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
     *         which minds that configures a validator or a factory of its own and keeps the lifecycle.
     */
    protected final <R> R applyValidator(final Function<? super Validator, ? extends R> function) {
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
     * Accepts specified violations to specified consumer, as an unmodifiable set.
     *
     * @param consumer   the consumer; must be not {@code null}.
     * @param violations the violations to accept.
     * @param <T>        root bean type parameter
     * @implNote Anything the consumer throws propagates unchanged. {@link Consumer#accept(Object)} declares no
     *         checked exception, so wrapping could only ever re-wrap an unchecked one and lose its type &mdash; and the
     *         throwable that matters most here, an {@link AssertionError} from an assertion made inside the consumer,
     *         is an {@link Error} and was never caught anyway.
     */
    protected final <T> void acceptViolations(final Consumer<? super Set<ConstraintViolation<T>>> consumer,
                                              final Set<ConstraintViolation<T>> violations) {
        consumer.accept(unmodifiableSet(violations));
    }

    /**
     * Validates with specified function, accepts the resulting violations to specified consumer, and verifies that they
     * are empty, or not empty, failing through this assertion object.
     * <p>
     * The consumer is called <em>before</em> the check, with an unmodifiable view, as
     * {@link #acceptViolations(Consumer, Set)} does.
     * <p>
     * The failure is raised by {@link #failure(String, Object...)}, so it honours what the caller configured on this
     * assertion object: a description set with {@code as(...)} prefixes the message, and a message set with
     * {@code withFailMessage(...)} or {@code overridingErrorMessage(...)}, eager or lazy, replaces the default one. The
     * values of {@code subject}, and the groups, are formatted with this assertion object's representation.
     *
     * @param validation    the function validating with a validator and the targeted groups; must be not {@code null}.
     * @param consumer      the consumer to accept the violations; must be not {@code null}.
     * @param expectedEmpty {@code true} when the violations should be empty; {@code false} when they should not.
     * @param subject       what was validated, as pairs of a label and a value, e.g. {@code "actual", actual}.
     * @param <T>           root bean type parameter
     * @throws AssertionError when the violations are not as expected.
     */
    protected final <T> void validate(
            final BiFunction<? super Validator, ? super Class<?>[], ? extends Set<ConstraintViolation<T>>> validation,
            final Consumer<? super Set<ConstraintViolation<T>>> consumer, final boolean expectedEmpty,
            final Object... subject) {
        Objects.requireNonNull(validation, "validation is null");
        Objects.requireNonNull(consumer, "consumer is null");
        if (subject.length % 2 != 0) {
            throw new IllegalArgumentException("subject is not of label/value pairs: " + subject.length);
        }
        final Class<?>[] groups = groups();
        final Set<ConstraintViolation<T>> violations = applyValidator(v -> validation.apply(v, groups));
        acceptViolations(consumer, violations);
        if (violations.isEmpty() == expectedEmpty) {
            return;
        }
        final String message = ValidationAssertMessages.formatValidationFailure(
                info.representation(),
                groups,
                violations,
                expectedEmpty,
                subject
        );
        // the completed text goes in as an argument, so that a '%' in a value or a message is not a format specifier
        throw failure("%s", message);
    }

    // -----------------------------------------------------------------------------------------------------------
    private final Set<Class<?>> groups = new HashSet<>();

    private Validator validator;

    private ValidatorFactory validatorFactory;
}
