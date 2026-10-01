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
import java.util.Set;
import java.util.function.Consumer;

/**
 * An interface for verifying a bean value.
 *
 * @param <SELF>   self type parameter
 * @param <ACTUAL> actual type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S119" // <SELF ...>
})
public interface BeanAssert<SELF extends BeanAssert<SELF, ACTUAL>, ACTUAL>
        extends PropertyAssert<SELF, ACTUAL> {

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
    SELF isValid(final Consumer<? super Set<ConstraintViolation<ACTUAL>>> consumer);

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
    default SELF isValid() {
        return isValid(
                i -> {
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
    SELF isNotValid();

    /**
     * Verifies that the {@code actual} value is <em>not valid</em>, while accepts a set of constraint violations, which
     * should be not empty, to specified consumer.
     *
     * @return this assertion object.
     * @throws AssertionError when the {@code actual} is {@code null} or <em>valid</em>.
     * @see #isNotValid()
     */
    SELF isNotValid(final Consumer<Set<ConstraintViolation<ACTUAL>>> consumer);

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
    SELF hasValidProperty(final String propertyName, final Consumer<? super Set<ConstraintViolation<ACTUAL>>> consumer);

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
    default SELF hasValidProperty(final String propertyName) {
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
    SELF doesNotHaveValidProperty(final String propertyName);
}
