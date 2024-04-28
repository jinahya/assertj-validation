package com.github.jinahya.assertj.validation;

/*-
 * #%L
 * assertj-bean-validation
 * %%
 * Copyright (C) 2021 - 2024 Jinahya, Inc.
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

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class ConstraintViolationAssert_RootBeanClass_Test
        extends ConstraintViolationAssert_$Test {

    @Test
    void hasRootBeanClass__() {
        @SuppressWarnings({"unchecked"})
        final ConstraintViolation<Object> actual = (ConstraintViolation<Object>) spy(ConstraintViolation.class);
        final Class<Object> rootBeenClass = Object.class;
        when(actual.getRootBeanClass()).thenReturn(rootBeenClass);
        final var assertion = assertion(actual);
        assertThatCode(
                () -> assertion.hasRootBeanClass(rootBeenClass)
        ).doesNotThrowAnyException();
    }
}
