package com.github.jinahya.assertj.validation;

/*-
 * #%L
 * assertj-bean-validation-javax
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

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
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
        extends AbstractPropertyAssert<SELF, ACTUAL>
        implements BeanAssert<SELF, ACTUAL> {

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
    @Override
    public final SELF isValid(final Consumer<? super Set<ConstraintViolation<ACTUAL>>> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        isNotNull();
        final Validator validator = delegate.getValidator();
        final Class<?>[] groups = delegate.getGroups();
        delegate.setViolations(validator.validate(actual, groups));
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

    @Override
    public final SELF isNotValid() {
        if (true) {
            return isNotValid(s -> {
            });
        }
        isNotNull();
        final Validator validator = delegate.getValidator();
        final Class<?>[] groups = delegate.getGroups();
        delegate.setViolations(validator.validate(actual, groups));
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
        return myself;
    }

    @Override
    public SELF isNotValid(final Consumer<Set<ConstraintViolation<ACTUAL>>> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        isNotNull();
        final Validator validator = delegate.getValidator();
        final Class<?>[] groups = delegate.getGroups();
        delegate.setViolations(validator.validate(actual, groups));
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

    @Override
    public final SELF hasValidProperty(final String propertyName,
                                       final Consumer<? super Set<ConstraintViolation<ACTUAL>>> consumer) {
        Objects.requireNonNull(propertyName, "propertyName is null");
        Objects.requireNonNull(consumer, "consumer is null");
        isNotNull();
        final Validator validator = delegate.getValidator();
        final Class<?>[] groups = delegate.getGroups();
        delegate.setViolations(validator.validateProperty(actual, propertyName, groups));
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

    @Override
    public final SELF doesNotHaveValidProperty(final String propertyName) {
        Objects.requireNonNull(propertyName, "propertyName is null");
        isNotNull();
        final Validator validator = delegate.getValidator();
        final Class<?>[] groups = delegate.getGroups();
        final Set<ConstraintViolation<ACTUAL>> violations = validator.validateProperty(actual, propertyName, groups);
        assertThat(violations)
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
}
