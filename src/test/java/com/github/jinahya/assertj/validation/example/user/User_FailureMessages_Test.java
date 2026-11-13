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
import jakarta.validation.groups.Default;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatBean;
import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatConstructor;
import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatProperty;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Pins the text of the failures, which is what this module is for. Only stable fragments are asserted: the
 * description of what was validated, the violation count, and the per-violation fields which do not depend on the
 * locale or on the order of the violations.
 */
class User_FailureMessages_Test {

    private static final String NOT_BLANK = "{jakarta.validation.constraints.NotBlank.message}";

    // no group given means the default group, which the description names explicitly
    private static final String DEFAULT_GROUPS = "groups: [" + Default.class + "]";

    @DisplayName("isValid() describes the bean, the groups, and each violation")
    @Test
    void isValid__() {
        final var user = User.of(null, 0);
        assertThatThrownBy(() -> assertThatBean(user).isValid())
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("The set of constraint violations resulted while validating")
                .hasMessageContaining("actual: " + user)
                .hasMessageContaining(DEFAULT_GROUPS)
                .hasMessageContaining("expected to be empty but contains 1 element(s)")
                .hasMessageContaining("propertyPath   : " + User.PROPERTY_NAME_NAME)
                .hasMessageContaining("rootBeanClass  : " + User.class)
                .hasMessageContaining("messageTemplate: " + NOT_BLANK);
    }

    @DisplayName("isValid() reports every violation")
    @Test
    void isValid__multiple() {
        assertThatThrownBy(() -> assertThatBean(User.of(null, -1)).isValid())
                .hasMessageContaining("expected to be empty but contains 2 element(s)")
                .hasMessageContaining("propertyPath   : " + User.PROPERTY_NAME_NAME)
                .hasMessageContaining("propertyPath   : " + User.PROPERTY_NAME_AGE);
    }

    @DisplayName("isValid() names the groups it targeted")
    @Test
    void isValid__groups() {
        final var senior = User.of(User.validName(), UserConstants.MIN_AGE_FOR_SENIOR);
        assertThatThrownBy(() -> assertThatBean(senior).targetingGroups(Junior.class).isValid())
                .hasMessageContaining("groups: [" + Junior.class + "]")
                .hasMessageContaining("propertyPath   : " + User.PROPERTY_NAME_AGE);
    }

    @DisplayName("isNotValid() says the set was empty")
    @Test
    void isNotValid__() {
        assertThatThrownBy(() -> assertThatBean(User.newValidUser()).isNotValid())
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining(DEFAULT_GROUPS)
                .hasMessageContaining("expected to be not empty but empty");
    }

    @DisplayName("hasValidProperty(..) names the property")
    @Test
    void hasValidProperty__() {
        assertThatThrownBy(() -> assertThatBean(User.of(null, 0)).hasValidProperty(User.PROPERTY_NAME_NAME))
                .hasMessageContaining("property: '" + User.PROPERTY_NAME_NAME + "'")
                .hasMessageContaining("expected to be empty but contains 1 element(s)")
                .hasMessageContaining("messageTemplate: " + NOT_BLANK);
    }

    @DisplayName("isValidFor(..) names the bean type and the property")
    @Test
    void isValidFor__() {
        assertThatThrownBy(() -> assertThatProperty((String) null).isValidFor(User.class, User.PROPERTY_NAME_NAME))
                .hasMessageContaining(User.class.getName())
                .hasMessageContaining(User.PROPERTY_NAME_NAME)
                .hasMessageContaining("expected to be empty but contains 1 element(s)")
                .hasMessageContaining("messageTemplate: " + NOT_BLANK);
    }

    @DisplayName("an executable assertion names what of the executable it validated")
    @Test
    void executable__() throws Exception {
        final var method = Greeter.class.getMethod(Greeter.METHOD_NAME_GREET, String.class, int.class);
        final var greeter = new Greeter("Jane");
        assertThatThrownBy(() -> assertThatBean(greeter).hasValidParameters(method, " ", 1))
                .hasMessageContaining("method: " + method)
                .hasMessageContaining("expected to be empty but contains 1 element(s)")
                .hasMessageContaining("messageTemplate: " + NOT_BLANK);
        final var constructor = Greeter.class.getConstructor(String.class);
        assertThatThrownBy(() -> assertThatConstructor(constructor).doesNotHaveValidReturnValue(greeter))
                .hasMessageContaining("return value of")
                .hasMessageContaining("constructor: " + constructor)
                .hasMessageContaining("expected to be not empty but empty");
    }
}
