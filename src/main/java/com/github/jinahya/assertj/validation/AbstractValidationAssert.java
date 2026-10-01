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
import jakarta.validation.ValidatorFactory;
import org.assertj.core.api.AbstractAssert;


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

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Configures this assertion object to use validators obtained from specified validator factory.
     * <p>
     * <strong>This assertion object never closes {@code factory}. Closing it is the caller's
     * responsibility.</strong> The caller supplied the instance, so the caller owns its lifecycle; this assertion
     * object only takes a {@link Validator} from it.
     * {@snippet lang = "java" id = "usingValidatorFactory":
     * try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) { // @highlight substring="try"
     *     assertThatBean(user).usingValidatorFactory(factory).isValid();
     *     assertThatBean(other).usingValidatorFactory(factory).isNotValid();
     * } // the caller closes it, here
     *}
     *
     * @param factory the validator factory to take a validator from, <em>not</em> closed by this assertion
     *                object; {@code null} to reset to the default.
     * @return this assertion object.
     * @apiNote This assertion object does not take ownership of {@code factory} and will not call
     * {@link ValidatorFactory#close()} on it. A factory left unclosed holds whatever resources the provider
     * allocated for it, so a caller which built the factory should close it &mdash; ideally with
     * try-with-resources, since {@link ValidatorFactory} is {@link AutoCloseable}. Note that closing it also ends
     * the life of the {@link Validator} taken from it: the specification forbids using that afterwards.
     * @implNote {@link ValidatorFactory#getValidator()} is invoked once, here, and the resulting validator is
     * kept. A {@link ValidatorFactory} exposes no mutator, so it cannot be reconfigured after the fact and there
     * is nothing to re-read per assertion. To assert with a customized validator, build one from
     * {@link ValidatorFactory#usingContext()} and pass it to {@link #usingValidator(Validator)}.
     * @see #usingValidator(Validator)
     * @see ValidatorFactory#getValidator()
     * @see ValidatorFactory#close()
     */
    public final SELF usingValidatorFactory(final ValidatorFactory factory) {
        return usingValidator(factory == null ? null : factory.getValidator());
    }

    /**
     * Configures this assertion object to use specified validator.
     *
     * @param validator the validator to use; {@code null} to reset to the default.
     * @return this assertion object.
     * @see #usingValidatorFactory(ValidatorFactory)
     */
    public final SELF usingValidator(final Validator validator) {
        delegate.setValidator(validator);
        return myself;
    }

    final ValidationAssertDelegate delegate = new ValidationAssertDelegate();
}
