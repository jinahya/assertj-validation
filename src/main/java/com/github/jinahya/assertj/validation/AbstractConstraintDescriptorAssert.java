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

import jakarta.validation.ConstraintTarget;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.Payload;
import jakarta.validation.metadata.ConstraintDescriptor;
import jakarta.validation.metadata.ValidateUnwrappedValue;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractComparableAssert;
import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.Assert;
import org.assertj.core.api.AssertFactory;
import org.assertj.core.api.CollectionAssert;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.assertj.core.api.ListAssert;
import org.assertj.core.api.MapAssert;
import org.assertj.core.api.ObjectAssertFactory;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.function.Function;

/**
 * An abstract assertion class for verifying {@link ConstraintDescriptor} values.
 *
 * @param <SELF>   self type parameter
 * @param <ACTUAL> actual type parameter
 * @param <T>      constraint's annotation type
 */
@SuppressWarnings({
        "java:S119" // <SELF ...>
})
public abstract class AbstractConstraintDescriptorAssert<
        SELF extends AbstractConstraintDescriptorAssert<SELF, ACTUAL, T>,
        ACTUAL extends ConstraintDescriptor<T>,
        T extends Annotation>
        extends AbstractAssert<SELF, ACTUAL> {

    /**
     * Creates a new instance with specified arguments.
     *
     * @param actual   an actual value to verify.
     * @param selfType self type.
     */
    protected AbstractConstraintDescriptorAssert(final ACTUAL actual, final Class<?> selfType) {
        super(actual, selfType);
    }

    // ------------------------------------------------------------------------------------------------------ annotation

    /**
     * Extracts an assertion for verifying the {@code actual} constraint descriptor's
     * {@link ConstraintDescriptor#getAnnotation() annotation}, using specified extractor and assertion factory.
     *
     * @param extractor the extractor for the annotation.
     * @param factory   the assertion factory.
     * @param <A>       assertion type parameter
     * @return an instance of {@link A}.
     * @see ConstraintDescriptor#getAnnotation()
     */
    public <A extends AbstractAssert<?, T>> A extractingAnnotation(
            final Function<? super ACTUAL, ? extends T> extractor,
            final AssertFactory<? super T, ? extends A> factory) {
        return isNotNull()
                .extracting(extractor, factory);
    }

    /**
     * Extracts an assertion for verifying the {@code actual} constraint descriptor's
     * {@link ConstraintDescriptor#getAnnotation() annotation}.
     *
     * @return an assertion for the {@code actual} constraint descriptor's annotation.
     * @see ConstraintDescriptor#getAnnotation()
     */
    public Assert<?, T> extractingAnnotation() {
        return extractingAnnotation(ConstraintDescriptor::getAnnotation, new ObjectAssertFactory<>());
    }

    // ------------------------------------------------------------------------------------------------------ attributes

    /**
     * Extracts an assertion for verifying the {@code actual} constraint descriptor's
     * {@link ConstraintDescriptor#getAttributes() attributes}.
     *
     * @return a map assertion for the {@code actual} constraint descriptor's attributes.
     * @see ConstraintDescriptor#getAttributes()
     */
    public MapAssert<String, Object> extractingAttributes() {
        return isNotNull()
                .extracting(ConstraintDescriptor::getAttributes, MapAssert<String, Object>::new);
    }

    /**
     * Verifies that the {@code actual} constraint descriptor's {@link ConstraintDescriptor#getAttributes() attributes}
     * contain an entry with specified key and value.
     *
     * @param name  the name of the attribute; must be not {@code null}.
     * @param value the expected value of the attribute.
     * @return this assertion object.
     * @see ConstraintDescriptor#getAttributes()
     */
    public SELF hasAttribute(final String name, final Object value) {
        Objects.requireNonNull(name, "name is null");
        extractingAttributes().containsEntry(name, value);
        return myself;
    }

    // -------------------------------------------------------------------------------------------- composingConstraints

    /**
     * Extracts an assertion for verifying the {@code actual} constraint descriptor's
     * {@link ConstraintDescriptor#getComposingConstraints() composingConstraints}.
     *
     * @return a collection assertion for the {@code actual} constraint descriptor's composing constraints.
     * @see ConstraintDescriptor#getComposingConstraints()
     */
    public CollectionAssert<ConstraintDescriptor<?>> extractingComposingConstraints() {
        return isNotNull()
                .extracting(ConstraintDescriptor::getComposingConstraints, CollectionAssert::new);
    }

    /**
     * Verifies that the {@code actual} constraint descriptor has no
     * {@link ConstraintDescriptor#getComposingConstraints() composingConstraints}.
     *
     * @return this assertion object.
     * @see ConstraintDescriptor#getComposingConstraints()
     */
    public SELF doesNotHaveComposingConstraints() {
        extractingComposingConstraints().isEmpty();
        return myself;
    }

    // -------------------------------------------------------------------------------------- constraintValidatorClasses

    /**
     * Extracts an assertion for verifying the {@code actual} constraint descriptor's
     * {@link ConstraintDescriptor#getConstraintValidatorClasses() constraintValidatorClasses}.
     *
     * @return a list assertion for the {@code actual} constraint descriptor's constraint validator classes.
     * @see ConstraintDescriptor#getConstraintValidatorClasses()
     */
    public ListAssert<Class<? extends ConstraintValidator<T, ?>>> extractingConstraintValidatorClasses() {
        return isNotNull()
                .extracting(ConstraintDescriptor::getConstraintValidatorClasses, ListAssert::new);
    }

    /**
     * Verifies that the {@code actual} constraint descriptor's
     * {@link ConstraintDescriptor#getConstraintValidatorClasses() constraintValidatorClasses} contain specified class.
     *
     * @param expectedConstraintValidatorClass the constraint validator class expected to be present.
     * @return this assertion object.
     * @see ConstraintDescriptor#getConstraintValidatorClasses()
     */
    public SELF hasConstraintValidatorClass(
            final Class<? extends ConstraintValidator<T, ?>> expectedConstraintValidatorClass) {
        extractingConstraintValidatorClasses().contains(expectedConstraintValidatorClass);
        return myself;
    }

    // ---------------------------------------------------------------------------------------------------------- groups

    /**
     * Extracts an assertion for verifying the {@code actual} constraint descriptor's
     * {@link ConstraintDescriptor#getGroups() groups}.
     *
     * @return a collection assertion for the {@code actual} constraint descriptor's groups.
     * @see ConstraintDescriptor#getGroups()
     */
    public CollectionAssert<Class<?>> extractingGroups() {
        return isNotNull()
                .extracting(ConstraintDescriptor::getGroups, CollectionAssert::new);
    }

    // ------------------------------------------------------------------------------------------------- messageTemplate

    /**
     * Extracts an assertion for verifying the {@code actual} constraint descriptor's
     * {@link ConstraintDescriptor#getMessageTemplate() messageTemplate}.
     *
     * @return a string assertion for the {@code actual} constraint descriptor's message template.
     * @see ConstraintDescriptor#getMessageTemplate()
     */
    public AbstractStringAssert<?> extractingMessageTemplate() {
        return isNotNull()
                .extracting(ConstraintDescriptor::getMessageTemplate, InstanceOfAssertFactories.STRING);
    }

    /**
     * Verifies that the {@link ConstraintDescriptor#getMessageTemplate() actual.messageTemplate} is equal to specified
     * value.
     *
     * @param expectedMessageTemplate the expected value of {@code actual.messageTemplate}.
     * @return this assertion object.
     * @see ConstraintDescriptor#getMessageTemplate()
     */
    public SELF hasMessageTemplate(final String expectedMessageTemplate) {
        extractingMessageTemplate().isEqualTo(expectedMessageTemplate);
        return myself;
    }

    // --------------------------------------------------------------------------------------------------------- payload

    /**
     * Extracts an assertion for verifying the {@code actual} constraint descriptor's
     * {@link ConstraintDescriptor#getPayload() payload}.
     *
     * @return a collection assertion for the {@code actual} constraint descriptor's payload.
     * @see ConstraintDescriptor#getPayload()
     */
    public CollectionAssert<Class<? extends Payload>> extractingPayload() {
        return isNotNull()
                .extracting(ConstraintDescriptor::getPayload, CollectionAssert::new);
    }

    /**
     * Verifies that the {@code actual} constraint descriptor's {@link ConstraintDescriptor#getPayload() payload}
     * contains specified class.
     *
     * @param expectedPayloadClass the payload class expected to be present.
     * @return this assertion object.
     * @see ConstraintDescriptor#getPayload()
     */
    public SELF hasPayload(final Class<? extends Payload> expectedPayloadClass) {
        extractingPayload().contains(expectedPayloadClass);
        return myself;
    }

    /**
     * Verifies that the {@code actual} constraint descriptor carries no
     * {@link ConstraintDescriptor#getPayload() payload}.
     *
     * @return this assertion object.
     * @see ConstraintDescriptor#getPayload()
     */
    public SELF doesNotHaveAnyPayload() {
        extractingPayload().isEmpty();
        return myself;
    }

    // --------------------------------------------------------------------------------------------- validationAppliesTo

    /**
     * Verifies that the {@code actual} constraint descriptor's
     * {@link ConstraintDescriptor#getValidationAppliesTo() validationAppliesTo} is same as specified value.
     *
     * @param constraintTarget the expected value of {@code actual.validationAppliesTo}.
     * @return this assertion object.
     * @see ConstraintDescriptor#getValidationAppliesTo()
     */
    public SELF hostsConstraintTarget(final ConstraintTarget constraintTarget) {
        isNotNull()
                .extracting(ConstraintDescriptor::getValidationAppliesTo, new ObjectAssertFactory<>())
                .isSameAs(constraintTarget);
        return myself;
    }

    /**
     * Verifies that the {@code actual} constraint descriptor hosts no
     * {@link ConstraintDescriptor#getValidationAppliesTo() constraint target}.
     *
     * @return this assertion object.
     * @apiNote This method invokes {@link #hostsConstraintTarget(ConstraintTarget)} method with {@code null}, and
     * returns the result.
     * @see ConstraintDescriptor#getValidationAppliesTo()
     */
    public SELF doesNotHostAnyConstraintTarget() {
        return hostsConstraintTarget(null);
    }

    // ------------------------------------------------------------------------------------------------- valueUnwrapping

    /**
     * Extracts an assertion for verifying the {@code actual} constraint descriptor's
     * {@link ConstraintDescriptor#getValueUnwrapping() valueUnwrapping}.
     *
     * @return a comparable assertion for the {@code actual} constraint descriptor's value-unwrapping mode.
     * @see ConstraintDescriptor#getValueUnwrapping()
     */
    public AbstractComparableAssert<?, ValidateUnwrappedValue> extractingValueUnwrapping() {
        return isNotNull()
                .extracting(ConstraintDescriptor::getValueUnwrapping,
                            InstanceOfAssertFactories.comparable(ValidateUnwrappedValue.class));
    }

    /**
     * Verifies that the {@link ConstraintDescriptor#getValueUnwrapping() actual.valueUnwrapping} is same as specified
     * value.
     *
     * @param expectedValueUnwrapping the expected value of {@code actual.valueUnwrapping}.
     * @return this assertion object.
     * @see ConstraintDescriptor#getValueUnwrapping()
     */
    public SELF hasValueUnwrapping(final ValidateUnwrappedValue expectedValueUnwrapping) {
        extractingValueUnwrapping().isSameAs(expectedValueUnwrapping);
        return myself;
    }

    // ----------------------------------------------------------------------------------------- reportAsSingleViolation

    /**
     * Extracts an assertion for verifying the {@code actual} constraint descriptor's
     * {@link ConstraintDescriptor#isReportAsSingleViolation() reportAsSingleViolation} flag.
     *
     * @return a boolean assertion for the {@code actual} constraint descriptor's report-as-single-violation flag.
     * @see ConstraintDescriptor#isReportAsSingleViolation()
     */
    public AbstractBooleanAssert<?> extractingReportAsSingleViolation() {
        return isNotNull()
                .extracting(ConstraintDescriptor::isReportAsSingleViolation, InstanceOfAssertFactories.BOOLEAN);
    }

    /**
     * Verifies that the {@code actual} constraint descriptor is
     * {@link ConstraintDescriptor#isReportAsSingleViolation() reported as a single violation}.
     *
     * @return this assertion object.
     * @see ConstraintDescriptor#isReportAsSingleViolation()
     */
    public SELF isReportedAsSingleViolation() {
        extractingReportAsSingleViolation().isTrue();
        return myself;
    }

    /**
     * Verifies that the {@code actual} constraint descriptor is <em>not</em>
     * {@link ConstraintDescriptor#isReportAsSingleViolation() reported as a single violation}.
     *
     * @return this assertion object.
     * @see ConstraintDescriptor#isReportAsSingleViolation()
     */
    public SELF isNotReportedAsSingleViolation() {
        extractingReportAsSingleViolation().isFalse();
        return myself;
    }

    // ---------------------------------------------------------------------------------------------------------- unwrap

    /**
     * Extracts an assertion for verifying the {@code actual} constraint descriptor
     * {@link ConstraintDescriptor#unwrap(Class) unwrapped} as specified type, using specified assertion factory.
     *
     * @param type          the type to be unwrapped as.
     * @param assertFactory the assertion factory.
     * @param <U>           the type of the unwrapped object.
     * @param <A>           assertion type parameter
     * @return an instance of {@link A}.
     * @see ConstraintDescriptor#unwrap(Class)
     */
    public <U, A extends AbstractAssert<?, ? extends U>> A extractingAsUnwrapped(
            final Class<U> type,
            final AssertFactory<? super U, ? extends A> assertFactory) {
        return isNotNull()
                .extracting(a -> a.unwrap(type), assertFactory)
                ;
    }

    /**
     * Verifies that the {@code actual} constraint descriptor value is equal to specified value when
     * {@link ConstraintDescriptor#unwrap(Class) unwrapped} as specified type.
     *
     * @param type                   the to be unwrapped as.
     * @param expectedUnwrappedValue expected unwrapped value.
     * @param <U>                    the type of the unwrapped object.
     * @return this assertion object.
     * @see ConstraintDescriptor#unwrap(Class)
     */
    public <U> SELF isEqualToWhenUnwrappedAs(final Class<U> type, final Object expectedUnwrappedValue) {
        extractingAsUnwrapped(type, new ObjectAssertFactory<>())
                .isEqualTo(expectedUnwrappedValue);
        return myself;
    }
}
