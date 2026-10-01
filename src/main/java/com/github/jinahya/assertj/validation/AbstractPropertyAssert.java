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

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * An abstract class for verifying a value against a specific property of a specific bean type.
 *
 * @param <SELF>   self type parameter
 * @param <ACTUAL> actual type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S119" // <SELF ...>
})
public abstract class AbstractPropertyAssert<SELF extends AbstractPropertyAssert<SELF, ACTUAL>, ACTUAL>
        extends AbstractValidationAssert<SELF, ACTUAL> {

    /**
     * Creates a new assertion object for verifying specified actual value.
     *
     * @param actual   the value of {@link ACTUAL} to verify.
     * @param selfType a class of {@link SELF}.
     */
    protected AbstractPropertyAssert(final ACTUAL actual, final Class<?> selfType) {
        super(actual, selfType);
    }

    /**
     * Verifies that the {@code actual} value is valid for the property of specified name of specified bean type, while
     * accepting a set of constraint violations, which may be empty, to specified consumer.
     *
     * @param beanType     the bean type; must be not {@code null}.
     * @param propertyName the name of the property; must be not {@code null}.
     * @param consumer     the consumer accepts the set of constraint violations.
     * @param <T>          type of the object to validate
     * @return this assertion object.
     * @throws AssertionError when the {@code actual} is not valid for {@code beanType#propertyName}.
     * @apiNote Note that the {@link jakarta.validation.Valid @Valid} is not honored by the
     * {@link jakarta.validation.Validator#validateValue(Class, String, Object, Class[])} method on which this method
     * relies.
     * @see #isValidFor(Class, String)
     */
    public final <T> SELF isValidFor(final Class<T> beanType, final String propertyName,
                                     final Consumer<? super Set<ConstraintViolation<T>>> consumer) {
        Objects.requireNonNull(beanType, "beanType is null");
        Objects.requireNonNull(propertyName, "propertyName is null");
        Objects.requireNonNull(consumer, "consumer is null");
        final Class<?>[] groups = delegate.getGroups();
        delegate.setViolations(
                delegate.applyValidator(v -> v.validateValue(beanType, propertyName, actual, groups)));
        delegate.acceptViolations(consumer);
        assertThat(delegate.getViolations())
                .as("%nThe set of constraint violations resulted while validating%n"
                    + "\tactual: %s%n"
                    + "\tagainst%n"
                    + "\t\tbeanType: %s%n"
                    + "\t\tproperty: '%s'%n"
                    + "\tfor%n"
                    + "\t\tgroups: %s%n",
                    actual,
                    beanType,
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
        return myself;
    }

    /**
     * Verifies that the {@code actual} value is valid for the property of specified name of specified bean type.
     * <p>
     * {@snippet lang = "java" id = "isValidFor":
     * class User {
     *     @NotBlank String name;
     *     @Max(0x7F) @PositiveOrZero int age;
     * }
     *
     * // @link region substring="assertThatProperty" target="ValidationAssertions#assertThatProperty(Object)"
     * // @highlight region substring="fail" type=highlighted
     * assertThatProperty("Jane").isValidFor(User.class, "name"); // should pass
     * assertThatProperty(  null).isValidFor(User.class, "name"); // should fail // @highlight regex="\-?(null|name)" type=highlighted
     * assertThatProperty(    "").isValidFor(User.class, "name"); // should fail // @highlight regex='(\"\"|name)' type=highlighted
     * assertThatProperty(   " ").isValidFor(User.class, "name"); // should fail // @highlight regex='(\"\s\"|name)' type=highlighted
     * assertThatProperty(     0).isValidFor(User.class,  "age"); // should pass
     * assertThatProperty(    28).isValidFor(User.class,  "age"); // should pass
     * assertThatProperty(    -1).isValidFor(User.class,  "age"); // should fail // @highlight regex="\-?(\d+|age)" type=highlighted
     * assertThatProperty(   300).isValidFor(User.class,  "age"); // should fail // @highlight regex="\-?(\d+|age)" type=highlighted
     * // @end
     * // @end
     *}
     *
     * @param beanType     the bean type; must be not {@code null}.
     * @param propertyName the name of the property; must be not {@code null}.
     * @param <T>          type of the object to validate
     * @return this assertion object.
     * @throws AssertionError when the {@code actual} is not valid for {@code beanType#propertyName}.
     * @apiNote Note that the {@link jakarta.validation.Valid @Valid} is not honored by the
     * {@link jakarta.validation.Validator#validateValue(Class, String, Object, Class[])} method on which this method
     * relies.
     * @see #isValidFor(Class, String, Consumer)
     */
    public final <T> SELF isValidFor(final Class<T> beanType, final String propertyName) {
        return isValidFor(
                beanType,
                propertyName,
                s -> {
                }
        );
    }

    /**
     * Verifies that the {@code actual} value is <em>not</em> valid for the property of specified name of specified bean
     * type.
     * <p>
     * {@snippet lang = "java" id = "isNotValidFor":
     * class User {
     *     @NotBlank String name;
     *     @Max(0x7F) @PositiveOrZero int age;
     * }
     *
     * // @link region substring="assertThatProperty" target="ValidationAssertions#assertThatProperty(Object)"
     * // @highlight region substring="pass" type=highlighted
     * assertThatProperty("Jane").isNotValidFor(User.class, "name"); // should fail
     * assertThatProperty(  null).isNotValidFor(User.class, "name"); // should pass // @highlight regex="\-?(null|name)" type=highlighted
     * assertThatProperty(    "").isNotValidFor(User.class, "name"); // should pass // @highlight regex='(\"\"|name)' type=highlighted
     * assertThatProperty(   " ").isNotValidFor(User.class, "name"); // should pass // @highlight regex='(\"\s\"|name)' type=highlighted
     * assertThatProperty(     0).isNotValidFor(User.class,  "age"); // should fail
     * assertThatProperty(    28).isNotValidFor(User.class,  "age"); // should fail
     * assertThatProperty(    -1).isNotValidFor(User.class,  "age"); // should pass // @highlight regex="\-?(\d+|age)" type=highlighted
     * assertThatProperty(   300).isNotValidFor(User.class,  "age"); // should pass // @highlight regex="\-?(\d+|age)" type=highlighted
     * // @end
     * // @end
     *}
     *
     * @param beanType     the bean type; must be not {@code null}.
     * @param propertyName the name of the property; must be not {@code null}.
     * @param <T>          type of the object to validate
     * @return this assertion object.
     * @throws AssertionError when the {@code actual} is not valid for {@code beanType#propertyName}.
     * @apiNote Note that the {@link jakarta.validation.Valid @Valid} is not honored by the
     * {@link jakarta.validation.Validator#validateValue(Class, String, Object, Class[])} method on which this method
     * relies.
     * @see #isValidFor(Class, String, Consumer)
     */
    public final <T> SELF isNotValidFor(final Class<T> beanType, final String propertyName) {
        Objects.requireNonNull(beanType, "beanType is null");
        Objects.requireNonNull(propertyName, "propertyName is null");
        final Class<?>[] groups = delegate.getGroups();
        delegate.setViolations(
                delegate.applyValidator(v -> v.validateValue(beanType, propertyName, actual, groups)));
        assertThat(delegate.getViolations())
                .as("%nThe set of constraint violations resulted while validating%n"
                    + "\tactual: %s%n"
                    + "\tagainst%n"
                    + "\t\tbeanType: %s%n"
                    + "\t\tproperty: '%s'%n"
                    + "\tfor%n"
                    + "\t\tgroups: %s%n",
                    actual,
                    beanType,
                    propertyName,
                    Arrays.asList(groups)
                )
                .withFailMessage("%nexpected to be not empty but empty")
                .isNotEmpty();
        return myself;
    }
}
