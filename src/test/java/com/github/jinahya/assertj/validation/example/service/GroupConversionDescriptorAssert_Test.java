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

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.groups.ConvertGroup;
import jakarta.validation.groups.Default;
import jakarta.validation.metadata.GroupConversionDescriptor;
import org.junit.jupiter.api.Test;

import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatBeanDescriptor;
import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatGroupConversionDescriptor;
import static com.github.jinahya.assertj.validation.ValidationAssertionsTestUtils.applyValidator;
import static com.github.jinahya.assertj.validation.ValidationInstanceOfAssertFactories.GROUP_CONVERSION_DESCRIPTOR;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests the assertion for {@link GroupConversionDescriptor}, reached through a cascaded property which converts the
 * {@link Default} group.
 */
class GroupConversionDescriptorAssert_Test {

    public interface Detailed {
    }

    public static class Child {

        @NotBlank(groups = Detailed.class)
        public String name;
    }

    public static class Parent {

        @Valid
        @ConvertGroup(from = Default.class, to = Detailed.class)
        public Child child;
    }

    private static GroupConversionDescriptor descriptor() {
        return applyValidator(v -> v.getConstraintsForClass(Parent.class)
                .getConstraintsForProperty("child")
                .getGroupConversions()
                .iterator().next());
    }

    @Test
    void __navigatedFromProperty() {
        assertThatBeanDescriptor(applyValidator(v -> v.getConstraintsForClass(Parent.class)))
                .extractingConstraintsForProperty("child")
                .isCascaded()
                .extractingGroupConversions()
                .singleElement(GROUP_CONVERSION_DESCRIPTOR)
                .convertsGroup(Default.class, Detailed.class);
    }

    @Test
    void __fromAndTo() {
        assertThatGroupConversionDescriptor(descriptor())
                .convertsGroup(Default.class, Detailed.class)
                .satisfies(d -> {
                    assertThatGroupConversionDescriptor(d).extractingFrom().isEqualTo(Default.class);
                    assertThatGroupConversionDescriptor(d).extractingTo().isEqualTo(Detailed.class);
                });
    }

    @Test
    void __convertsGroup_mismatch() {
        assertThatThrownBy(() -> assertThatGroupConversionDescriptor(descriptor())
                .convertsGroup(Detailed.class, Default.class))
                .isInstanceOf(AssertionError.class);
    }

    @Test
    void __null() {
        assertThatThrownBy(() -> assertThatGroupConversionDescriptor(null).extractingFrom())
                .isInstanceOf(AssertionError.class);
        assertThatThrownBy(() -> assertThatGroupConversionDescriptor(null).convertsGroup(Default.class, Detailed.class))
                .isInstanceOf(AssertionError.class);
    }

    @Test
    void __notConverting() {
        assertThatBeanDescriptor(applyValidator(v -> v.getConstraintsForClass(Child.class)))
                .extractingConstraintsForProperty("name")
                .doesNotHaveAnyGroupConversion();
    }
}
