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

@SuppressWarnings({
        "java:S119" // <SELF ...>
})
public abstract class AbstractPropertyAssert<SELF extends AbstractPropertyAssert<SELF, ACTUAL>, ACTUAL>
        extends AbstractValidationAssert<SELF, ACTUAL>
        implements PropertyAssert<SELF, ACTUAL> {

    /**
     * Creates a new assertion object for verifying specified actual value.
     *
     * @param actual   the value of {@link ACTUAL} to verify.
     * @param selfType a class of {@link SELF}.
     */
    protected AbstractPropertyAssert(final ACTUAL actual, final Class<?> selfType) {
        super(actual, selfType);
    }

    @Override
    public final <T> SELF isValidFor(final Class<T> beanType, final String propertyName,
                                     final Consumer<? super Set<ConstraintViolation<T>>> consumer) {
        Objects.requireNonNull(beanType, "beanType is null");
        Objects.requireNonNull(propertyName, "propertyName is null");
        Objects.requireNonNull(consumer, "consumer is null");
        final Validator validator = delegate.getValidator();
        final Class<?>[] groups = delegate.getGroups();
        delegate.setViolations(validator.validateValue(beanType, propertyName, actual, groups));
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

    @Override
    public final <T> SELF isNotValidFor(final Class<T> beanType, final String propertyName) {
        Objects.requireNonNull(beanType, "beanType is null");
        Objects.requireNonNull(propertyName, "propertyName is null");
        final Validator validator = delegate.getValidator();
        final Class<?>[] groups = delegate.getGroups();
        delegate.setViolations(validator.validateValue(beanType, propertyName, actual, groups));
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
