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
import jakarta.validation.Validator;
import jakarta.validation.executable.ExecutableValidator;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * An abstract assertion class for verifying constructor parameters and constructor return values.
 * <p>
 * Constructor validation has no instance to assert on &mdash; the object does not exist yet &mdash; so, unlike
 * method validation which hangs off {@link AbstractBeanAssert}, it takes the {@link Constructor} itself as the
 * {@code actual} value.
 *
 * @param <SELF> self type parameter
 * @param <T>    the type declaring the constructor
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ExecutableValidator#validateConstructorParameters(Constructor, Object[], Class[])
 * @see ExecutableValidator#validateConstructorReturnValue(Constructor, Object, Class[])
 */
@SuppressWarnings({
        "java:S119", // <SELF ...>
        "java:S2160" // override equals/hashCode
})
public abstract class AbstractConstructorAssert<SELF extends AbstractConstructorAssert<SELF, T>, T>
        extends AbstractValidationAssert<SELF, Constructor<? extends T>> {

    /**
     * Creates a new instance with specified arguments.
     *
     * @param actual the actual value to verify.
     * @param selfType a class of {@code SELF}.
     */
    protected AbstractConstructorAssert(final Constructor<? extends T> actual, final Class<?> selfType) {
        super(actual, selfType);
    }

    private SELF validating(final String what,
                            final Function<? super ExecutableValidator, ? extends Set<ConstraintViolation<T>>> validation,
                            final Consumer<? super Set<ConstraintViolation<T>>> consumer,
                            final boolean expectedEmpty) {
        Objects.requireNonNull(consumer, "consumer is null");
        isNotNull();
        final Validator validator = delegate.getValidator();
        final Class<?>[] groups = delegate.getGroups();
        delegate.setViolations(validation.apply(validator.forExecutables()));
        final var assertion = assertThat(delegate.<T>getViolations())
                .as("%nThe set of constraint violations resulted while validating%n"
                    + "\t%s of%n"
                    + "\tconstructor: %s%n"
                    + "targeting%n"
                    + "\tgroups: %s%n",
                    what,
                    actual,
                    Arrays.asList(groups)
                );
        if (expectedEmpty) {
            assertion
                    .withFailMessage(() -> String.format(
                            "%nexpected to be empty but contains %1$d element(s)%n"
                            + "%2$s",
                            delegate.getViolations().size(),
                            ValidationAssertMessages.format(delegate.getViolations())
                    ))
                    .isEmpty();
        } else {
            assertion
                    .withFailMessage("%nexpected to be not empty but empty")
                    .isNotEmpty();
        }
        consumer.accept(delegate.getViolations());
        return myself;
    }

    /**
     * Verifies that specified parameter values are valid for the {@code actual} constructor, while accepting the
     * resulting set of constraint violations, which may be empty, to specified consumer.
     *
     * @param parameterValues the parameter values to validate.
     * @param consumer        the consumer accepts the set of constraint violations.
     * @return this assertion object.
     * @throws AssertionError when the {@code actual} is {@code null} or any parameter value is not valid.
     * @see ExecutableValidator#validateConstructorParameters(Constructor, Object[], Class[])
     */
    public final SELF hasValidParameters(final Object[] parameterValues,
                                         final Consumer<? super Set<ConstraintViolation<T>>> consumer) {
        return validating("parameters",
                          v -> v.validateConstructorParameters(actual, parameterValues, delegate.getGroups()),
                          consumer, true);
    }

    /**
     * Verifies that specified parameter values are valid for the {@code actual} constructor.
     *
     * @param parameterValues the parameter values to validate.
     * @return this assertion object.
     * @throws AssertionError when the {@code actual} is {@code null} or any parameter value is not valid.
     * @see ExecutableValidator#validateConstructorParameters(Constructor, Object[], Class[])
     */
    public final SELF hasValidParameters(final Object... parameterValues) {
        return hasValidParameters(parameterValues, s -> {
            // does nothing
        });
    }

    /**
     * Verifies that specified parameter values are <em>not</em> valid for the {@code actual} constructor.
     *
     * @param parameterValues the parameter values to validate.
     * @return this assertion object.
     * @throws AssertionError when the {@code actual} is {@code null} or every parameter value is valid.
     * @see ExecutableValidator#validateConstructorParameters(Constructor, Object[], Class[])
     */
    public final SELF doesNotHaveValidParameters(final Object... parameterValues) {
        return validating("parameters",
                          v -> v.validateConstructorParameters(actual, parameterValues, delegate.getGroups()),
                          s -> {
                          }, false);
    }

    /**
     * Verifies that specified created object is valid as the {@code actual} constructor's return value, while
     * accepting the resulting set of constraint violations, which may be empty, to specified consumer.
     *
     * @param createdObject the object created by the {@code actual} constructor.
     * @param consumer      the consumer accepts the set of constraint violations.
     * @return this assertion object.
     * @throws AssertionError when the {@code actual} is {@code null} or the created object is not valid.
     * @see ExecutableValidator#validateConstructorReturnValue(Constructor, Object, Class[])
     */
    public final SELF hasValidReturnValue(final T createdObject,
                                          final Consumer<? super Set<ConstraintViolation<T>>> consumer) {
        return validating("return value",
                          v -> v.validateConstructorReturnValue(actual, createdObject, delegate.getGroups()),
                          consumer, true);
    }

    /**
     * Verifies that specified created object is valid as the {@code actual} constructor's return value.
     *
     * @param createdObject the object created by the {@code actual} constructor.
     * @return this assertion object.
     * @throws AssertionError when the {@code actual} is {@code null} or the created object is not valid.
     * @see ExecutableValidator#validateConstructorReturnValue(Constructor, Object, Class[])
     */
    public final SELF hasValidReturnValue(final T createdObject) {
        return hasValidReturnValue(createdObject, s -> {
            // does nothing
        });
    }

    /**
     * Verifies that specified created object is <em>not</em> valid as the {@code actual} constructor's return value.
     *
     * @param createdObject the object created by the {@code actual} constructor.
     * @return this assertion object.
     * @throws AssertionError when the {@code actual} is {@code null} or the created object is valid.
     * @see ExecutableValidator#validateConstructorReturnValue(Constructor, Object, Class[])
     */
    public final SELF doesNotHaveValidReturnValue(final T createdObject) {
        return validating("return value",
                          v -> v.validateConstructorReturnValue(actual, createdObject, delegate.getGroups()),
                          s -> {
                          }, false);
    }
}
