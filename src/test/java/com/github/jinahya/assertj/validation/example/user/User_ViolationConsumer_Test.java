package com.github.jinahya.assertj.validation.example.user;

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

import com.github.jinahya.assertj.validation.example.service.Greeter;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatBean;
import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatConstructor;
import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatProperty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Every overload taking a consumer of violations calls it before the check, once, with an unmodifiable view.
 */
class User_ViolationConsumer_Test {

    /**
     * Runs specified assertion with a recording consumer, checks that the consumer was called once with an
     * unmodifiable set, and returns that set.
     */
    private static Set<?> recorded(final Function<Consumer<Object>, ThrowingCallable> assertion,
                                   final boolean expectFailure) {
        final List<Object> seen = new ArrayList<>();
        final ThrowingCallable callable = assertion.apply(seen::add);
        if (expectFailure) {
            assertThatThrownBy(callable).isInstanceOf(AssertionError.class);
        } else {
            assertThatCode(callable).doesNotThrowAnyException();
        }
        assertThat(seen).singleElement().isInstanceOf(Set.class);
        final Set<?> violations = (Set<?>) seen.get(0);
        assertThatThrownBy(violations::clear).isInstanceOf(UnsupportedOperationException.class);
        return violations;
    }

    @DisplayName("isValid(consumer) and isNotValid(consumer) see the violations, also when they fail")
    @Test
    void should_see_the_violations_of_isValid_and_isNotValid_also_on_failure() {
        final var invalid = User.of(null, 0);
        final var valid = User.newValidUser();
        assertThat(recorded(c -> () -> assertThatBean(invalid).isValid(c), true)).hasSize(1);
        assertThat(recorded(c -> () -> assertThatBean(valid).isValid(c), false)).isEmpty();
        assertThat(recorded(c -> () -> assertThatBean(invalid).isNotValid(c), false)).hasSize(1);
        assertThat(recorded(c -> () -> assertThatBean(valid).isNotValid(c), true)).isEmpty();
    }

    @DisplayName("hasValidProperty(name, consumer) sees the violations, also when it fails")
    @Test
    void should_see_the_violations_of_hasValidProperty_also_on_failure() {
        final var invalid = User.of(null, 0);
        assertThat(recorded(c -> () -> assertThatBean(invalid).hasValidProperty(User.PROPERTY_NAME_NAME, c), true)
                           ).hasSize(1);
        assertThat(recorded(c -> () -> assertThatBean(invalid).hasValidProperty(User.PROPERTY_NAME_AGE, c), false)
                           ).isEmpty();
    }

    @DisplayName("isValidFor(type, name, consumer) sees the violations, also when it fails")
    @Test
    void should_see_the_violations_of_isValidFor_also_on_failure() {
        assertThat(recorded(c -> () -> assertThatProperty((String) null)
                .isValidFor(User.class, User.PROPERTY_NAME_NAME, c), true)).hasSize(1);
    }

    @DisplayName("method and constructor consumers see the violations, also when they fail")
    @Test
    void should_see_the_violations_of_executables_also_on_failure() throws Exception {
        final Method method = Greeter.class.getMethod(Greeter.METHOD_NAME_GREET, String.class, int.class);
        final var greeter = new Greeter("Jane");
        assertThat(recorded(c -> () -> assertThatBean(greeter)
                .hasValidParameters(method, new Object[]{" ", 1}, c), true)).hasSize(1);
        assertThat(recorded(c -> () -> assertThatBean(greeter)
                .hasValidReturnValue(method, " ", c), true)).hasSize(1);
        final var constructor = Greeter.class.getConstructor(String.class);
        assertThat(recorded(c -> () -> assertThatConstructor(constructor)
                .hasValidParameters(new Object[]{" "}, c), true)).hasSize(1);
        assertThat(recorded(c -> () -> assertThatConstructor(constructor)
                .hasValidReturnValue(greeter, c), false)).isEmpty();
    }

    @DisplayName("what the consumer throws propagates unchanged, before the check")
    @Test
    void should_propagate_what_the_consumer_throws() {
        final var thrown = new IllegalStateException("from the consumer");
        assertThatThrownBy(() -> assertThatBean(User.newValidUser()).isNotValid(s -> {
            throw thrown;
        })).isSameAs(thrown);
    }
}
