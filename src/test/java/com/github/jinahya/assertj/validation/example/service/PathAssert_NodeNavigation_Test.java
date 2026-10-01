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

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ElementKind;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Set;

import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatPath;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the typed node navigation on {@code AbstractPathAssert}, including the node kinds that previously had no entry
 * point at all.
 */
class PathAssert_NodeNavigation_Test {

    private static ConstraintViolation<Greeter> parameterViolation() throws NoSuchMethodException {
        final Method method = Greeter.class.getMethod(Greeter.METHOD_NAME_GREET, String.class, int.class);
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            final Set<ConstraintViolation<Greeter>> violations = factory.getValidator().forExecutables()
                    .validateParameters(new Greeter("Jane"), method, new Object[]{"  ", 1});
            return violations.iterator().next();
        }
    }

    @Test
    void __methodNode() throws Exception {
        assertThatPath(parameterViolation().getPropertyPath())
                .extractingMethodNode(0)
                .hasName(Greeter.METHOD_NAME_GREET)
                .hasKind(ElementKind.METHOD);
    }

    @Test
    void __methodNodeParameterTypes() throws Exception {
        assertThatPath(parameterViolation().getPropertyPath())
                .extractingMethodNode(0)
                .extractingParameterTypes()
                .containsExactly(String.class, int.class);
    }

    @Test
    void __parameterNode() throws Exception {
        assertThatPath(parameterViolation().getPropertyPath())
                .extractingParameterNode(1)
                .hasParameterIndex(0)
                .hasKind(ElementKind.PARAMETER);
    }

    @Test
    void __genericNode() throws Exception {
        assertThatPath(parameterViolation().getPropertyPath())
                .extractingNode(0)
                .hasName(Greeter.METHOD_NAME_GREET);
    }

    @Test
    void __hasNodeSatisfying() throws Exception {
        assertThatPath(parameterViolation().getPropertyPath())
                .hasMethodNodeSatisfying(0, a -> a.hasName(Greeter.METHOD_NAME_GREET))
                .hasNodeSatisfying(1, a -> a.hasKind(ElementKind.PARAMETER));
    }

    @Test
    void __pathIsIterable() throws Exception {
        final var path = parameterViolation().getPropertyPath();
        assertThat(path).hasSize(2);
        assertThatPath(path).hasSize(2);
    }
}
