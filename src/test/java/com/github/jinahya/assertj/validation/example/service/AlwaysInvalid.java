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
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

/**
 * A class-level constraint which never holds, so that validating a bean yields a violation whose property path
 * is a single {@link jakarta.validation.Path.Node} of kind {@link jakarta.validation.ElementKind#BEAN} &mdash;
 * the only node whose name is {@code null}.
 */
@Documented
@Constraint(validatedBy = {AlwaysInvalid.Validator.class})
@Target({TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AlwaysInvalid {

    String message() default "always invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * The validator, which never passes.
     */
    class Validator implements ConstraintValidator<AlwaysInvalid, Object> {

        @Override
        public boolean isValid(final Object value, final ConstraintValidatorContext context) {
            return false;
        }
    }
}
