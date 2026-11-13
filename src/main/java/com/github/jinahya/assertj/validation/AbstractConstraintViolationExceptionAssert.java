package com.github.jinahya.assertj.validation;

/*-
 * #%L
 * assertj-validation
 * %%
 * Copyright (C) 2021 - 2026 Jinahya, Inc.
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
import jakarta.validation.ConstraintViolationException;
import org.assertj.core.api.AbstractThrowableAssert;
import org.assertj.core.api.CollectionAssert;

import java.util.Objects;
import java.util.Set;

/**
 * An abstract assertion class for verifying {@link ConstraintViolationException} values, which is what a framework
 * throws when it, rather than the test, triggers validation.
 * <p>
 * The violations of an exception may come from beans of different types, so they are verified as a collection of
 * {@code ConstraintViolation<?>} rather than through {@link AbstractIterableOfConstraintViolationsAssert}, whose root
 * bean type is fixed.
 *
 * @param <SELF> self type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S119" // <SELF ...>
})
public abstract class AbstractConstraintViolationExceptionAssert<
        SELF extends AbstractConstraintViolationExceptionAssert<SELF>>
        extends AbstractThrowableAssert<SELF, ConstraintViolationException> {

    /**
     * Creates a new instance with specified arguments.
     *
     * @param actual   the actual value to verify.
     * @param selfType the self type.
     */
    protected AbstractConstraintViolationExceptionAssert(final ConstraintViolationException actual,
                                                         final Class<?> selfType) {
        super(actual, selfType);
    }

    // ------------------------------------------------------------------------------------------ constraintViolations

    /**
     * Extracts an assertion for verifying the {@code actual} exception's
     * {@link ConstraintViolationException#getConstraintViolations() constraintViolations}.
     *
     * @return a collection assertion for the {@code actual} exception's constraint violations.
     * @see ConstraintViolationException#getConstraintViolations()
     */
    public CollectionAssert<ConstraintViolation<?>> extractingConstraintViolations() {
        return isNotNull()
                .extracting(ConstraintViolationException::getConstraintViolations, CollectionAssert::new);
    }

    /**
     * Verifies that the {@code actual} exception carries at least one constraint violation whose
     * {@link ConstraintViolation#getPropertyPath() propertyPath}, as a string, is equal to specified value.
     *
     * @param expectedPropertyPath the expected property path, e.g., {@code "name"} or {@code "greet.arg0"}.
     * @return this assertion object.
     * @throws AssertionError when the {@code actual} is {@code null} or carries no violation on
     *                        {@code expectedPropertyPath}.
     */
    public SELF hasViolationOn(final String expectedPropertyPath) {
        Objects.requireNonNull(expectedPropertyPath, "expectedPropertyPath is null");
        isNotNull();
        final Set<ConstraintViolation<?>> violations = actual.getConstraintViolations();
        if (violations == null
            || violations.stream().noneMatch(v -> expectedPropertyPath.equals(String.valueOf(v.getPropertyPath())))) {
            failWithMessage("%nExpecting%n  %s%nto have a constraint violation on%n  %s%nbut had violations on%n  %s",
                            actual,
                            expectedPropertyPath,
                            violations == null
                            ? "(none)"
                            : violations.stream().map(v -> String.valueOf(v.getPropertyPath())).sorted().toList());
        }
        return myself;
    }
}
