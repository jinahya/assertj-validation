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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatBean;
import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatConstructor;
import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatPath;
import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatProperty;
import static com.github.jinahya.assertj.validation.ValidationAssertionsTestUtils.applyValidator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Verifies that the validation assertions report through the state the caller configured on them: a description set
 * with {@code as(...)} prefixes the failure, and a message set with {@code withFailMessage(...)}, eager or lazy,
 * replaces the default one.
 */
class User_DescriptionAndOverride_Test {

    private static Supplier<String> counting(final AtomicInteger count, final String message) {
        return () -> {
            count.incrementAndGet();
            return message;
        };
    }

    // ------------------------------------------------------------------------------------------------------ beans

    @DisplayName("as(..) prefixes the default failure")
    @Test
    void bean_description() {
        assertThatThrownBy(() -> assertThatBean(User.of(null, 0)).as("registration case 42").isValid())
                .isInstanceOf(AssertionError.class)
                .hasMessageStartingWith("[registration case 42]")
                .hasMessageContaining("expected to be empty but contains 1 element(s)");
    }

    @DisplayName("withFailMessage(..) replaces the default failure")
    @Test
    void bean_override() {
        assertThatThrownBy(() -> assertThatBean(User.of(null, 0)).withFailMessage("registration rejected").isValid())
                .hasMessage("registration rejected");
        assertThatThrownBy(() -> assertThatBean(User.newValidUser()).withFailMessage("should be invalid").isNotValid())
                .hasMessage("should be invalid");
    }

    @DisplayName("as(..) and withFailMessage(..) together")
    @Test
    void bean_descriptionAndOverride() {
        assertThatThrownBy(() -> assertThatBean(User.of(null, 0))
                .as("registration case 42")
                .withFailMessage("registration rejected")
                .isValid())
                .hasMessage("[registration case 42] registration rejected");
    }

    @DisplayName("a lazy message is evaluated on failure only")
    @Test
    void bean_lazyOverride() {
        final var count = new AtomicInteger();
        assertThatCode(() -> assertThatBean(User.newValidUser()).withFailMessage(counting(count, "lazy")).isValid())
                .doesNotThrowAnyException();
        assertThat(count).as("not evaluated on success").hasValue(0);
        assertThatThrownBy(() -> assertThatBean(User.of(null, 0)).withFailMessage(counting(count, "lazy")).isValid())
                .hasMessage("lazy");
        assertThat(count).as("evaluated on failure").hasPositiveValue();
    }

    @DisplayName("hasValidProperty(..) and doesNotHaveValidProperty(..) keep the description")
    @Test
    void beanProperty() {
        assertThatThrownBy(() -> assertThatBean(User.of(null, 0)).as("case 42").hasValidProperty(User.PROPERTY_NAME_NAME))
                .hasMessageStartingWith("[case 42]");
        assertThatThrownBy(() -> assertThatBean(User.newValidUser()).withFailMessage("valid name")
                .doesNotHaveValidProperty(User.PROPERTY_NAME_NAME))
                .hasMessage("valid name");
    }

    // ----------------------------------------------------------------------------------------------------- values

    @Test
    void value() {
        assertThatThrownBy(() -> assertThatProperty("").withFailMessage("invalid name")
                .isValidFor(User.class, User.PROPERTY_NAME_NAME))
                .hasMessage("invalid name");
        assertThatThrownBy(() -> assertThatProperty("Jane").as("case 42")
                .isNotValidFor(User.class, User.PROPERTY_NAME_NAME))
                .hasMessageStartingWith("[case 42]")
                .hasMessageContaining("expected to be not empty but empty");
    }

    // ------------------------------------------------------------------------------------------------ executables

    @Test
    void executables() throws Exception {
        final var method = Greeter.class.getMethod(Greeter.METHOD_NAME_GREET, String.class, int.class);
        final var greeter = new Greeter("Jane");
        assertThatThrownBy(() -> assertThatBean(greeter).as("case 42").hasValidParameters(method, " ", 1))
                .hasMessageStartingWith("[case 42]");
        assertThatThrownBy(() -> assertThatBean(greeter).withFailMessage("valid return")
                .doesNotHaveValidReturnValue(method, "Jane greets world"))
                .hasMessage("valid return");
        final var constructor = Greeter.class.getConstructor(String.class);
        assertThatThrownBy(() -> assertThatConstructor(constructor).as("case 42").hasValidParameters(" "))
                .hasMessageStartingWith("[case 42]");
        assertThatThrownBy(() -> assertThatConstructor(constructor).withFailMessage("valid greeter")
                .doesNotHaveValidReturnValue(greeter))
                .hasMessage("valid greeter");
    }

    // ------------------------------------------------------------------------------------------------------ paths

    @DisplayName("path navigation keeps the description and the overriding message")
    @Test
    void paths() throws Exception {
        final var method = Greeter.class.getMethod(Greeter.METHOD_NAME_GREET, String.class, int.class);
        final var path = applyValidator(v -> v.forExecutables()
                .validateParameters(new Greeter("Jane"), method, new Object[]{" ", 1})
                .iterator().next().getPropertyPath());
        assertThatThrownBy(() -> assertThatPath(path).as("case 42").nodes().hasSize(99))
                .hasMessageStartingWith("[case 42]");
        assertThatThrownBy(() -> assertThatPath(path).as("case 42").extractingPropertyNode(0))
                .hasMessageStartingWith("[case 42]")
                .hasMessageContaining("to be of kind");
        assertThatThrownBy(() -> assertThatPath(path).withFailMessage("no such node").extractingNode(5))
                .hasMessage("no such node");
        assertThatThrownBy(() -> assertThatPath(path).as("case 42").extractingMethodNode(0).hasName("nosuch"))
                .hasMessageStartingWith("[case 42]");
    }

    /**
     * The navigation this module owns, {@code nodes()} and {@code extracting*Node(index)}, hands the overriding message
     * over lazily. Navigating further, into a node's properties, goes through assertj's own {@code extracting(..)},
     * which copies the message eagerly, and so evaluates a supplier even on success.
     */
    @DisplayName("a lazy message is not evaluated by successful node navigation")
    @Test
    void paths_lazyOverride() throws Exception {
        final var method = Greeter.class.getMethod(Greeter.METHOD_NAME_GREET, String.class, int.class);
        final var path = applyValidator(v -> v.forExecutables()
                .validateParameters(new Greeter("Jane"), method, new Object[]{" ", 1})
                .iterator().next().getPropertyPath());
        final var count = new AtomicInteger();
        assertThatPath(path).withFailMessage(counting(count, "lazy")).extractingMethodNode(0).isNotNull();
        assertThatPath(path).withFailMessage(counting(count, "lazy")).nodes().hasSize(2);
        assertThat(count).hasValue(0);
    }
}
