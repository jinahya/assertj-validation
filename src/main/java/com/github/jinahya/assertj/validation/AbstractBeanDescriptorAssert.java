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

import jakarta.validation.metadata.BeanDescriptor;
import jakarta.validation.metadata.ConstructorDescriptor;
import jakarta.validation.metadata.MethodDescriptor;
import jakarta.validation.metadata.MethodType;
import jakarta.validation.metadata.PropertyDescriptor;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AssertFactory;
import org.assertj.core.api.CollectionAssert;
import org.assertj.core.api.InstanceOfAssertFactories;

import java.util.Objects;

/**
 * An abstract assertion class for verifying {@link BeanDescriptor} values, the root of the Jakarta Validation
 * metadata API.
 *
 * @param <SELF> self type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see jakarta.validation.Validator#getConstraintsForClass(Class)
 */
@SuppressWarnings({
        "java:S119" // <SELF ...>
})
public abstract class AbstractBeanDescriptorAssert<SELF extends AbstractBeanDescriptorAssert<SELF>>
        extends AbstractElementDescriptorAssert<SELF, BeanDescriptor> {

    protected AbstractBeanDescriptorAssert(final BeanDescriptor actual, final Class<?> selfType) {
        super(actual, selfType);
    }

    // ---------------------------------------------------------------------------------------------- beanConstrained

    /**
     * Extracts an assertion for verifying whether the {@code actual} descriptor's bean
     * {@link BeanDescriptor#isBeanConstrained() is constrained}.
     *
     * @return a boolean assertion for the {@code actual} descriptor's bean-constrained flag.
     * @see BeanDescriptor#isBeanConstrained()
     */
    public AbstractBooleanAssert<?> extractingBeanConstrained() {
        return isNotNull()
                .extracting(BeanDescriptor::isBeanConstrained, InstanceOfAssertFactories.BOOLEAN);
    }

    /**
     * Verifies that the {@code actual} descriptor's bean {@link BeanDescriptor#isBeanConstrained() is constrained}.
     *
     * @return this assertion object.
     * @see BeanDescriptor#isBeanConstrained()
     */
    public SELF isBeanConstrained() {
        extractingBeanConstrained().isTrue();
        return myself;
    }

    /**
     * Verifies that the {@code actual} descriptor's bean is <em>not</em>
     * {@link BeanDescriptor#isBeanConstrained() constrained}.
     *
     * @return this assertion object.
     * @see BeanDescriptor#isBeanConstrained()
     */
    public SELF isNotBeanConstrained() {
        extractingBeanConstrained().isFalse();
        return myself;
    }

    // ------------------------------------------------------------------------------------------------------ property

    /**
     * Extracts an assertion for verifying the constraints declared on the property of specified name, using specified
     * assertion factory.
     *
     * @param propertyName the name of the property; must be not {@code null}.
     * @param factory      the assertion factory.
     * @param <A>          assertion type parameter
     * @return an instance of {@link A}.
     * @see BeanDescriptor#getConstraintsForProperty(String)
     */
    public <A extends AbstractPropertyDescriptorAssert<? extends A>> A extractingConstraintsForProperty(
            final String propertyName, final AssertFactory<? super PropertyDescriptor, ? extends A> factory) {
        Objects.requireNonNull(propertyName, "propertyName is null");
        return isNotNull()
                .extracting(a -> a.getConstraintsForProperty(propertyName), factory);
    }

    /**
     * Extracts an assertion for verifying the constraints declared on the property of specified name.
     *
     * @param propertyName the name of the property; must be not {@code null}.
     * @return a property-descriptor assertion; its {@code actual} is {@code null} when the property has no
     * constraint and is not cascaded.
     * @see BeanDescriptor#getConstraintsForProperty(String)
     */
    public AbstractPropertyDescriptorAssert<?> extractingConstraintsForProperty(final String propertyName) {
        return extractingConstraintsForProperty(propertyName, DefaultPropertyDescriptorAssert::new);
    }

    /**
     * Verifies that the {@code actual} descriptor declares constraints for the property of specified name.
     *
     * @param propertyName the name of the property; must be not {@code null}.
     * @return this assertion object.
     * @see BeanDescriptor#getConstraintsForProperty(String)
     */
    public SELF hasConstraintsForProperty(final String propertyName) {
        extractingConstraintsForProperty(propertyName).isNotNull();
        return myself;
    }

    /**
     * Extracts an assertion for verifying the {@code actual} descriptor's
     * {@link BeanDescriptor#getConstrainedProperties() constrainedProperties}.
     *
     * @return a collection assertion for the {@code actual} descriptor's constrained properties.
     * @see BeanDescriptor#getConstrainedProperties()
     */
    public CollectionAssert<PropertyDescriptor> extractingConstrainedProperties() {
        return isNotNull()
                .extracting(BeanDescriptor::getConstrainedProperties, CollectionAssert::new);
    }

    // -------------------------------------------------------------------------------------------------------- method

    /**
     * Extracts an assertion for verifying the constraints declared on the method of specified name and parameter
     * types, using specified assertion factory.
     *
     * @param factory        the assertion factory.
     * @param name           the name of the method; must be not {@code null}.
     * @param parameterTypes the parameter types of the method.
     * @param <A>            assertion type parameter
     * @return an instance of {@link A}.
     * @see BeanDescriptor#getConstraintsForMethod(String, Class[])
     */
    public <A extends AbstractMethodDescriptorAssert<? extends A>> A extractingConstraintsForMethod(
            final AssertFactory<? super MethodDescriptor, ? extends A> factory, final String name,
            final Class<?>... parameterTypes) {
        Objects.requireNonNull(name, "name is null");
        return isNotNull()
                .extracting(a -> a.getConstraintsForMethod(name, parameterTypes), factory);
    }

    /**
     * Extracts an assertion for verifying the constraints declared on the method of specified name and parameter
     * types.
     *
     * @param name           the name of the method; must be not {@code null}.
     * @param parameterTypes the parameter types of the method.
     * @return a method-descriptor assertion; its {@code actual} is {@code null} when the method is unconstrained.
     * @see BeanDescriptor#getConstraintsForMethod(String, Class[])
     */
    public AbstractMethodDescriptorAssert<?> extractingConstraintsForMethod(final String name,
                                                                            final Class<?>... parameterTypes) {
        return extractingConstraintsForMethod(DefaultMethodDescriptorAssert::new, name, parameterTypes);
    }

    /**
     * Extracts an assertion for verifying the {@code actual} descriptor's
     * {@link BeanDescriptor#getConstrainedMethods(MethodType, MethodType...) constrainedMethods} of specified types.
     *
     * @param methodType  the method type to look for; must be not {@code null}.
     * @param methodTypes further method types to look for.
     * @return a collection assertion for the {@code actual} descriptor's constrained methods.
     * @see BeanDescriptor#getConstrainedMethods(MethodType, MethodType...)
     */
    public CollectionAssert<MethodDescriptor> extractingConstrainedMethods(final MethodType methodType,
                                                                           final MethodType... methodTypes) {
        Objects.requireNonNull(methodType, "methodType is null");
        return isNotNull()
                .extracting(a -> a.getConstrainedMethods(methodType, methodTypes), CollectionAssert::new);
    }

    // --------------------------------------------------------------------------------------------------- constructor

    /**
     * Extracts an assertion for verifying the constraints declared on the constructor of specified parameter types,
     * using specified assertion factory.
     *
     * @param factory        the assertion factory.
     * @param parameterTypes the parameter types of the constructor.
     * @param <A>            assertion type parameter
     * @return an instance of {@link A}.
     * @see BeanDescriptor#getConstraintsForConstructor(Class[])
     */
    public <A extends AbstractConstructorDescriptorAssert<? extends A>> A extractingConstraintsForConstructor(
            final AssertFactory<? super ConstructorDescriptor, ? extends A> factory,
            final Class<?>... parameterTypes) {
        return isNotNull()
                .extracting(a -> a.getConstraintsForConstructor(parameterTypes), factory);
    }

    /**
     * Extracts an assertion for verifying the constraints declared on the constructor of specified parameter types.
     *
     * @param parameterTypes the parameter types of the constructor.
     * @return a constructor-descriptor assertion; its {@code actual} is {@code null} when the constructor is
     * unconstrained.
     * @see BeanDescriptor#getConstraintsForConstructor(Class[])
     */
    public AbstractConstructorDescriptorAssert<?> extractingConstraintsForConstructor(
            final Class<?>... parameterTypes) {
        return extractingConstraintsForConstructor(DefaultConstructorDescriptorAssert::new, parameterTypes);
    }

    /**
     * Extracts an assertion for verifying the {@code actual} descriptor's
     * {@link BeanDescriptor#getConstrainedConstructors() constrainedConstructors}.
     *
     * @return a collection assertion for the {@code actual} descriptor's constrained constructors.
     * @see BeanDescriptor#getConstrainedConstructors()
     */
    public CollectionAssert<ConstructorDescriptor> extractingConstrainedConstructors() {
        return isNotNull()
                .extracting(BeanDescriptor::getConstrainedConstructors, CollectionAssert::new);
    }
}
