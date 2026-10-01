package com.github.jinahya.assertj.validation.example.service;

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

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

/**
 * A constraint for verifying that a {@link Greeter} carries a non-blank name.
 * <p>
 * Applied to a constructor it becomes a <em>return value</em> constraint, which is what makes
 * {@link
 * jakarta.validation.executable.ExecutableValidator#validateConstructorReturnValue(java.lang.reflect.Constructor,
 * Object, Class[]) validateConstructorReturnValue} testable for failure &mdash; a {@code @NotNull} there cannot fail,
 * because the provider rejects a {@code null} created instance with an {@link IllegalArgumentException} before any
 * constraint runs.
 */
@Documented
@Constraint(validatedBy = {NamedValidator.class})
@Target({CONSTRUCTOR, METHOD, TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Named {

    String message() default "{com.github.jinahya.assertj.validation.example.service.Named.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
