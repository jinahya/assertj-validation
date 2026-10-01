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
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.Collections.unmodifiableSet;

/**
 * An abstract base class for assertions which <em>perform</em> validation.
 * <p>
 * It holds only what such an assertion needs: the groups to target, and the validator to validate with &mdash;
 * either one the caller supplied, a factory the caller supplied, or neither, in which case each assertion builds
 * and closes a factory of its own. Assertions which merely inspect what validation produced &mdash; a
 * {@link ConstraintViolation}, a {@link jakarta.validation.Path}, a metadata descriptor &mdash; do not extend
 * this class, because there is nothing left for them to validate.
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
     * Supersedes any factory configured by {@link #usingValidatorFactory(ValidatorFactory)}; the two are
     * alternatives and the last call wins.
     *
     * @param validator the validator to use; {@code null} to fall back to a per-assertion default.
     * @return this assertion object.
     * @apiNote This accepts a {@link Validator} rather than only a {@link ValidatorFactory} because the
     * conversion runs one way only. A factory yields a validator through
     * {@link ValidatorFactory#getValidator()}, but a validator cannot yield a factory &mdash; and a validator
     * customized through {@link ValidatorFactory#usingContext()}, one injected by a framework, or a test double
     * has no factory to offer.
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
     * responsibility.</strong> The caller supplied the instance, so the caller owns its lifecycle; each
     * assertion only takes a {@link Validator} from it.
     * {@snippet lang = "java" id = "usingValidatorFactory":
     * try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) { // @highlight substring="try"
     *     assertThatBean(user).usingValidatorFactory(factory).isValid();
     *     assertThatBean(other).usingValidatorFactory(factory).isNotValid();
     * } // the caller closes it, here
     *}
     * <p>
     * Supersedes any validator configured by {@link #usingValidator(Validator)}; the two are alternatives and
     * the last call wins.
     *
     * @param factory the factory to take validators from, <em>not</em> closed by this assertion object;
     *                {@code null} to fall back to a per-assertion default.
     * @return this assertion object.
     * @apiNote This assertion object does not take ownership of {@code factory} and will not call
     * {@link ValidatorFactory#close()} on it. Closing it also ends the life of every {@link Validator} taken
     * from it: the specification forbids using those afterwards.
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
     * configured.
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
     * which minds that configures a validator or a factory of its own and keeps the lifecycle.
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
     */
    protected final <T> void acceptViolations(final Consumer<? super Set<ConstraintViolation<T>>> consumer,
                                              final Set<ConstraintViolation<T>> violations) {
        try {
            consumer.accept(unmodifiableSet(violations));
        } catch (final Exception e) {
            throw new RuntimeException("failed to accept violations to [" + consumer + "]", e);
        }
    }

    // -----------------------------------------------------------------------------------------------------------
    private final Set<Class<?>> groups = new HashSet<>();

    private Validator validator;

    private ValidatorFactory validatorFactory;
}
