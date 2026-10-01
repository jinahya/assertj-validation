package com.github.jinahya.assertj.validation;

/*-
 * #%L
 * assertj-validation
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

import jakarta.validation.ConstraintViolation;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class ConstraintViolationAssert_RootBean_Test
        extends ConstraintViolationAssert_$Test {

    @Test
    void hasRootBean__() {
        final var actual = spy(ConstraintViolation.class);
        final var rootBeen = this;
        when(actual.getRootBean()).thenReturn(rootBeen);
        final var assertion = assertion(actual);
        assertThatCode(() -> assertion.hasRootBean(rootBeen)).doesNotThrowAnyException();
    }

    @Test
    void doesNotHaveRootBean__() {
        final var actual = spy(ConstraintViolation.class);
        when(actual.getRootBean()).thenReturn(null);
        final var assertion = assertion(actual);
        assertThatCode(() -> assertion.doesNotHaveRootBean()).doesNotThrowAnyException();
    }
}
