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
     * With neither a validator nor a factory configured, each assertion builds a factory of its own and closes
     * it before returning, so nothing is ever left unowned.
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
        delegate.setValidatorFactory(factory);
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
        delegate.setValidator(validator);
        return myself;
    }

    final ValidationAssertDelegate delegate = new ValidationAssertDelegate();
}
