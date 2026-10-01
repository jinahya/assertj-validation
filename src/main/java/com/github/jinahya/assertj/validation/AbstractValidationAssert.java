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

import jakarta.validation.Validator;
import org.assertj.core.api.AbstractAssert;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * An abstract base class for verifying values and beans.
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

    /**
     * Configures this assertion object to use specified groups targeted for validation.
     *
     * @param groups the validation groups to use; {@code null} or empty for clearing the group.
     * @return this assertion object.
     */
    public final SELF targetingGroups(final Class<?>... groups) {
        delegate.setGroups(groups);
        return myself;
    }

    /**
     * Configures this assertion object to use specified validator.
     *
     * @param validator the validator to use; {@code null} to reset.
     * @return this assertion object.
     */
    public final SELF usingValidator(final Validator validator) {
        return usingValidatorSuppliedBy(
                Optional.ofNullable(validator)
                        .<Supplier<Validator>>map(v -> () -> v)
                        .orElse(null)
        );
    }

    /**
     * Configures this assertion object to use validators supplied by specified supplier.
     *
     * @param validatorSupplier the supplier supplying validators; {@code null} to reset.
     * @return this assertion object.
     */
    public final SELF usingValidatorSuppliedBy(final Supplier<? extends Validator> validatorSupplier) {
        delegate.setValidatorSupplier(validatorSupplier);
        return myself;
    }

    final ValidationAssertDelegate delegate = new ValidationAssertDelegate();
}
