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

import org.assertj.core.api.ThrowableAssert;
import org.mockito.ArgumentCaptor;

import jakarta.validation.ConstraintViolation;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.spy;

public final class ValidationAssertionsTestUtils {

    public static <T> Consumer<? super Set<ConstraintViolation<T>>> violationsConsumerEmpty() {
        return s -> {
        };
    }

    public static <T> Consumer<Set<ConstraintViolation<T>>> violationsConsumerSpy(
            final Consumer<? super Set<? extends ConstraintViolation<T>>> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        final Consumer<Set<ConstraintViolation<T>>> wrapper = new Consumer<>() { // DO NOT REPLACE WITH LAMBDA!!!
            @Override
            public void accept(final Set<ConstraintViolation<T>> violations) {
                consumer.accept(violations);
            }
        };
        return spy(wrapper);
    }

    public static <T> Consumer<Set<ConstraintViolation<T>>> violationsConsumerSpy() {
        return violationsConsumerSpy(cv -> {
        });
    }

    public static <T> ArgumentCaptor<Set<ConstraintViolation<T>>> constraintViolationsCaptor() {
        @SuppressWarnings({"unchecked"})
        final ArgumentCaptor<Set<ConstraintViolation<T>>> captor = ArgumentCaptor.forClass(Set.class);
        return captor;
    }

    public static void shouldPass(final ThrowableAssert.ThrowingCallable callable) {
        Objects.requireNonNull(callable, "callable is null");
        assertThatCode(callable).doesNotThrowAnyException();
    }

    public static void shouldFail(final ThrowableAssert.ThrowingCallable callable) {
        Objects.requireNonNull(callable, "callable is null");
        assertThatThrownBy(callable).isInstanceOf(AssertionError.class);
    }

    private ValidationAssertionsTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
