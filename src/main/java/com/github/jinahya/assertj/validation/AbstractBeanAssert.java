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

import java.lang.reflect.Method;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * An abstract assert class for verifying a bean value.
 *
 * @param <SELF>   self type parameter
 * @param <ACTUAL> actual type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see Validator#validate(Object, Class[])
 * @see Validator#validateProperty(Object, String, Class[])
 * @see Validator#validateValue(Class, String, Object, Class[])
 */
@SuppressWarnings({
        "java:S119", // <SELF ...>
        "java:S2160" // override equals/hashCode
})
public abstract class AbstractBeanAssert<SELF extends AbstractBeanAssert<SELF, ACTUAL>, ACTUAL>
        extends AbstractPropertyAssert<SELF, ACTUAL> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance for verifying specified actual value.
     *
     * @param actual   the value of {@link ACTUAL} to verify.
     * @param selfType a class of {@link SELF}.
     */
    protected AbstractBeanAssert(final ACTUAL actual, final Class<?> selfType) {
        super(actual, selfType);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Verifies that the {@code actual} value is valid, while accepting the set of constraint violations, which may be
     * empty, to specified consumer.
     *
     * @param consumer the consumer accepts the set of constraint violations which may be empty.
     * @return this assertion object.
     * @throws AssertionError when the {@code actual} is {@code null} or invalid.
     * @see #isValid()
     * @see #isNotValid()
     */
    public final SELF isValid(final Consumer<? super Set<ConstraintViolation<ACTUAL>>> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        isNotNull();
        final Class<?>[] groups = delegate.getGroups();
        delegate.setViolations(delegate.applyValidator(v -> v.validate(actual, groups)));
        delegate.acceptViolations(consumer);
        assertThat(delegate.getViolations())
                .as("%nThe set of constraint violations resulted while validating%n"
                    + "\tactual: %s%n"
                    + "targeting%n"
                    + "\tgroups: %s%n",
                    actual,
                    Arrays.asList(groups)
                )
                .withFailMessage(() -> String.format(
                        "%nexpected to be empty but contains %1$d element(s)%n"
                        + "%2$s",
                        delegate.getViolations().size(),
                        ValidationAssertMessages.format(delegate.getViolations())
                ))
                .isEmpty();
        return myself;
    }

    /**
     * Verifies that the {@code actual} value is valid.
     * <p>
     * {@snippet lang = "java" id = "example":
     * class User {
     *     @NotBlank String name;
     *     @Max(0x7F) @PositiveOrZero int age;
     * }
     *
     * // @highlight region substring="fail" type=highlighted
     * // @link region substring="assertThatBean" target="com.github.jinahya.assertj.validation.ValidationAssertions#assertThatBean(Object)"
     * assertThatBean(new User("Jane", 28)).isValid(); // should pass
     * assertThatBean(new User(  null,  0)).isValid(); // should fail // @highlight regex="\-?null" type=highlighted
     * assertThatBean(new User("John", -1)).isValid(); // should fail // @highlight regex="\-?\d+" type=highlighted
     * // @end
     * // @end
     *}
     *
     * @return this assertion object.
     * @throws AssertionError when the {@code actual} is {@code null} or invalid.
     * @see #isValid(Consumer)
     * @see #isNotValid()
     */
    public final SELF isValid() {
        return isValid(
                i -> {
                    // does nothing
                }
        );
    }

    /**
     * Verifies that the {@code actual} value is <em>not</em> valid.
     * <p>
     * {@snippet lang = "java" id = "example":
     * class User {
     *     @NotBlank String name;
     *     @Max(0x7F) @PositiveOrZero int age;
     * }
     *
     * // @highlight region substring="pass" type=highlighted
     * // @link region substring="assertThatBean" target="com.github.jinahya.assertj.validation.ValidationAssertions#assertThatBean(Object)"
     * assertThatBean(new User("Jane", 28)).isNotValid(); // should fail
     * assertThatBean(new User(  null,  0)).isNotValid(); // should pass // @highlight regex="\-?null" type=highlighted
     * assertThatBean(new User("John", -1)).isNotValid(); // should pass // @highlight regex="\-?\d+" type=highlighted
     * // @end
     * // @end
     *}
     *
     * @return this assertion object.
     * @throws AssertionError when the {@code actual} is {@code null} or <em>valid</em>.
     * @see #isValid(Consumer)
     * @see #isValid()
     */
    public final SELF isNotValid() {
        return isNotValid(s -> {
            // does nothing
        });
    }

    /**
     * Verifies that the {@code actual} value is <em>not valid</em>, while accepts a set of constraint violations, which
     * should be not empty, to specified consumer.
     *
     * @param consumer the consumer accepts the set of constraint violations.
     * @return this assertion object.
     * @throws AssertionError when the {@code actual} is {@code null} or <em>valid</em>.
     * @see #isNotValid()
     */
    public final SELF isNotValid(final Consumer<? super Set<ConstraintViolation<ACTUAL>>> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        isNotNull();
        final Class<?>[] groups = delegate.getGroups();
        delegate.setViolations(delegate.applyValidator(v -> v.validate(actual, groups)));
        assertThat(delegate.getViolations())
                .as("%nThe set of constraint violations resulted while validating%n"
                    + "\tactual: %s%n"
                    + "targeting%n"
                    + "\tgroups: %s%n",
                    actual,
                    Arrays.asList(groups)
                )
                .withFailMessage("%nexpected to be not empty but empty")
                .isNotEmpty();
        consumer.accept(delegate.getViolations());
        return myself;
    }

    /**
     * Verifies that no constraint violations populated while validating all constraints placed on the property of
     * specified name of the {@code actual} value, while accepts the set of constraint violations which may be empty to
     * specified consumer.
     *
     * @param propertyName the name of the property to be verified as valid; not {@code null}.
     * @param consumer     the consumer accepts the set of constraint violations.
     * @return this assertion object.
     * @throws AssertionError when the {@code actual} is {@code null} or its current value of {@code propertyName} is
     *                        not valid.
     * @apiNote Note that the {@link jakarta.validation.Valid @Valid} is not honored by the
     * {@link Validator#validateProperty(Object, String, Class[])} method on which this method relies. See <a
     * href="https://jakarta.ee/specifications/bean-validation/3.0/jakarta-bean-validation-spec-3.0.html#validationapi-validatorapi-validationmethods">6.1.1.
     * Validation methods (Jakarta Bean Validation 3.0)</a>.
     * @see #hasValidProperty(String)
     * @see #doesNotHaveValidProperty(String)
     */
    public final SELF hasValidProperty(final String propertyName,
                                       final Consumer<? super Set<ConstraintViolation<ACTUAL>>> consumer) {
        Objects.requireNonNull(propertyName, "propertyName is null");
        Objects.requireNonNull(consumer, "consumer is null");
        isNotNull();
        final Class<?>[] groups = delegate.getGroups();
        delegate.setViolations(delegate.applyValidator(v -> v.validateProperty(actual, propertyName, groups)));
        assertThat(delegate.getViolations())
                .as("%nThe set of constraint violations resulted while validating%n"
                    + "\tactual: %s%n"
                    + "for its%n"
                    + "\tproperty: '%s'%n"
                    + "targeting %n"
                    + "\tgroups: %s%n",
                    actual,
                    propertyName,
                    Arrays.asList(groups)
                )
                .withFailMessage(() -> String.format(
                        "%nexpected to be empty but contains %1$d element(s)%n"
                        + "%2$s",
                        delegate.getViolations().size(),
                        ValidationAssertMessages.format(delegate.getViolations())
                ))
                .isEmpty();
        consumer.accept(delegate.getViolations());
        return myself;
    }

    /**
     * Verifies that no constraint violations populated while validating all constraints placed on the property of
     * specified name of the {@code actual} value.
     * <p>
     * {@snippet lang = "java" id = "example":
     * class User {
     *     @NotBlank String name;
     *     @Max(0x7F) @PositiveOrZero int age;
     * }
     * // @highlight region substring="fail" type=highlighted
     * // @link region substring="assertThatBean" target="com.github.jinahya.assertj.validation.ValidationAssertions#assertThatBean(Object)"
     * assertThatBean(new User("Jane", 28)).hasValidProperty("name"); // should pass
     * assertThatBean(new User("John", 28)).hasValidProperty( "age"); // should pass
     * assertThatBean(new User(  null,  0)).hasValidProperty("name"); // should fail // @highlight regex="\-?(null|name)" type=highlighted
     * assertThatBean(new User(  null,  0)).hasValidProperty( "age"); // should pass
     * assertThatBean(new User("John", -1)).hasValidProperty("name"); // should pass
     * assertThatBean(new User("John", -1)).hasValidProperty( "age"); // should fail // @highlight regex="\-?(\d+|age)" type=highlighted
     * // @end
     * // @end
     *}
     *
     * @param propertyName the name of the property to be verified as valid; not {@code null}.
     * @return this assertion object.
     * @throws AssertionError when the {@code actual} is {@code null} or its current value of the {@code propertyName}
     *                        is not valid.
     * @apiNote Note that the {@link jakarta.validation.Valid @Valid} is not honored by the
     * {@link Validator#validateProperty(Object, String, Class[])} method on which this method relies. See <a
     * href="https://jakarta.ee/specifications/bean-validation/3.0/jakarta-bean-validation-spec-3.0.html#validationapi-validatorapi-validationmethods">6.1.1.
     * Validation methods (Jakarta Bean Validation 3.0)</a>.
     * @see #hasValidProperty(String, Consumer)
     * @see #doesNotHaveValidProperty(String)
     */
    public final SELF hasValidProperty(final String propertyName) {
        return hasValidProperty(
                propertyName,
                s -> {
                    // does nothing
                }
        );
    }

    /**
     * Verifies that any constraint violation populated while validating all constraints placed on the property of
     * specified name of the {@code actual} value.
     * <p>
     * {@snippet lang = "java" id = "example":
     * class User {
     *     @NotBlank String name;
     *     @Max(0x7F) @PositiveOrZero int age;
     * }
     * // @highlight region substring="pass" type=highlighted
     * // @link region substring="assertThatBean" target="com.github.jinahya.assertj.validation.ValidationAssertions#assertThatBean(Object)"
     * assertThatBean(new User("Jane", 28)).doesNotHaveValidProperty("name"); // should fail
     * assertThatBean(new User("John", 28)).doesNotHaveValidProperty( "age"); // should fail
     * assertThatBean(new User(  null,  0)).doesNotHaveValidProperty("name"); // should pass // @highlight regex="\-?(null|name)" type=highlighted
     * assertThatBean(new User(  null,  0)).doesNotHaveValidProperty( "age"); // should fail
     * assertThatBean(new User("John", -1)).doesNotHaveValidProperty("name"); // should fail
     * assertThatBean(new User("John", -1)).doesNotHaveValidProperty( "age"); // should pass // @highlight regex="\-?(\d+|age)" type=highlighted
     * // @end
     * // @end
     *}
     *
     * @param propertyName the name of the property to be verified as valid; not {@code null}.
     * @return this assertion object.
     * @throws AssertionError when the {@code actual} is {@code null} or its current value of the {@code propertyName}
     *                        is not valid.
     * @apiNote Note that the {@link jakarta.validation.Valid @Valid} is not honored by the
     * {@link Validator#validateProperty(Object, String, Class[])} method on which this method relies. See <a
     * href="https://jakarta.ee/specifications/bean-validation/3.0/jakarta-bean-validation-spec-3.0.html#validationapi-validatorapi-validationmethods">6.1.1.
     * Validation methods (Jakarta Bean Validation 3.0)</a>.
     * @see #hasValidProperty(String, Consumer)
     * @see #hasValidProperty(String)
     */
    public final SELF doesNotHaveValidProperty(final String propertyName) {
        Objects.requireNonNull(propertyName, "propertyName is null");
        isNotNull();
        final Class<?>[] groups = delegate.getGroups();
        delegate.setViolations(delegate.applyValidator(v -> v.validateProperty(actual, propertyName, groups)));
        assertThat(delegate.getViolations())
                .as("%nThe set of constraint violations resulted while validating%n"
                    + "\tactual: %s%n"
                    + "for its%n"
                    + "\tproperty: '%s'%n"
                    + "targeting %n"
                    + "\tgroups: %s%n",
                    actual,
                    propertyName,
                    Arrays.asList(groups)
                )
                .withFailMessage("%nexpected to be not empty but empty")
                .isNotEmpty();
        return myself;
    }

    // ----------------------------------------------------------------------------------------- executable validation

    private SELF executable(final String what, final String detail,
                            final java.util.function.Function<? super ExecutableValidator,
                                    ? extends Set<ConstraintViolation<ACTUAL>>> validation,
                            final Consumer<? super Set<ConstraintViolation<ACTUAL>>> consumer,
                            final boolean expectedEmpty) {
        Objects.requireNonNull(consumer, "consumer is null");
        isNotNull();
        final Class<?>[] groups = delegate.getGroups();
        delegate.setViolations(delegate.applyValidator(v -> validation.apply(v.forExecutables())));
        final var assertion = assertThat(delegate.<ACTUAL>getViolations())
                .as("%nThe set of constraint violations resulted while validating%n"
                    + "\tactual: %s%n"
                    + "for its%n"
                    + "\t%s: %s%n"
                    + "targeting%n"
                    + "\tgroups: %s%n",
                    actual,
                    what,
                    detail,
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
     * Verifies that specified parameter values are valid for specified method of the {@code actual} value, while
     * accepting the resulting set of constraint violations, which may be empty, to specified consumer.
     *
     * @param method          the method whose parameters are validated; must be not {@code null}.
     * @param parameterValues the parameter values to validate.
     * @param consumer        the consumer accepts the set of constraint violations.
     * @return this assertion object.
     * @throws AssertionError when the {@code actual} is {@code null} or any parameter value is not valid.
     * @see ExecutableValidator#validateParameters(Object, Method, Object[], Class[])
     */
    public final SELF hasValidParameters(final Method method, final Object[] parameterValues,
                                         final Consumer<? super Set<ConstraintViolation<ACTUAL>>> consumer) {
        Objects.requireNonNull(method, "method is null");
        return executable("method", method.toString(),
                          v -> v.validateParameters(actual, method, parameterValues, delegate.getGroups()),
                          consumer, true);
    }

    /**
     * Verifies that specified parameter values are valid for specified method of the {@code actual} value.
     *
     * @param method          the method whose parameters are validated; must be not {@code null}.
     * @param parameterValues the parameter values to validate.
     * @return this assertion object.
     * @throws AssertionError when the {@code actual} is {@code null} or any parameter value is not valid.
     * @see ExecutableValidator#validateParameters(Object, Method, Object[], Class[])
     */
    public final SELF hasValidParameters(final Method method, final Object... parameterValues) {
        return hasValidParameters(method, parameterValues, s -> {
            // does nothing
        });
    }

    /**
     * Verifies that specified parameter values are <em>not</em> valid for specified method of the {@code actual}
     * value.
     *
     * @param method          the method whose parameters are validated; must be not {@code null}.
     * @param parameterValues the parameter values to validate.
     * @return this assertion object.
     * @throws AssertionError when the {@code actual} is {@code null} or every parameter value is valid.
     * @see ExecutableValidator#validateParameters(Object, Method, Object[], Class[])
     */
    public final SELF doesNotHaveValidParameters(final Method method, final Object... parameterValues) {
        Objects.requireNonNull(method, "method is null");
        return executable("method", method.toString(),
                          v -> v.validateParameters(actual, method, parameterValues, delegate.getGroups()),
                          s -> {
                          }, false);
    }

    /**
     * Verifies that specified return value is valid for specified method of the {@code actual} value, while accepting
     * the resulting set of constraint violations, which may be empty, to specified consumer.
     *
     * @param method      the method whose return value is validated; must be not {@code null}.
     * @param returnValue the return value to validate.
     * @param consumer    the consumer accepts the set of constraint violations.
     * @return this assertion object.
     * @throws AssertionError when the {@code actual} is {@code null} or the return value is not valid.
     * @see ExecutableValidator#validateReturnValue(Object, Method, Object, Class[])
     */
    public final SELF hasValidReturnValue(final Method method, final Object returnValue,
                                          final Consumer<? super Set<ConstraintViolation<ACTUAL>>> consumer) {
        Objects.requireNonNull(method, "method is null");
        return executable("method return value", method.toString(),
                          v -> v.validateReturnValue(actual, method, returnValue, delegate.getGroups()),
                          consumer, true);
    }

    /**
     * Verifies that specified return value is valid for specified method of the {@code actual} value.
     *
     * @param method      the method whose return value is validated; must be not {@code null}.
     * @param returnValue the return value to validate.
     * @return this assertion object.
     * @throws AssertionError when the {@code actual} is {@code null} or the return value is not valid.
     * @see ExecutableValidator#validateReturnValue(Object, Method, Object, Class[])
     */
    public final SELF hasValidReturnValue(final Method method, final Object returnValue) {
        return hasValidReturnValue(method, returnValue, s -> {
            // does nothing
        });
    }

    /**
     * Verifies that specified return value is <em>not</em> valid for specified method of the {@code actual} value.
     *
     * @param method      the method whose return value is validated; must be not {@code null}.
     * @param returnValue the return value to validate.
     * @return this assertion object.
     * @throws AssertionError when the {@code actual} is {@code null} or the return value is valid.
     * @see ExecutableValidator#validateReturnValue(Object, Method, Object, Class[])
     */
    public final SELF doesNotHaveValidReturnValue(final Method method, final Object returnValue) {
        Objects.requireNonNull(method, "method is null");
        return executable("method return value", method.toString(),
                          v -> v.validateReturnValue(actual, method, returnValue, delegate.getGroups()),
                          s -> {
                          }, false);
    }
}
