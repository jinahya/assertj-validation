package org.hibernate.validator.referenceguide.chapter02.containerelement.custom;

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

import com.github.jinahya.assertj.validation.AbstractPathAssert;
import com.github.jinahya.assertj.validation.ValidationAssertions;
import com.github.jinahya.assertj.validation.ValidationInstanceOfAssertFactories;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.AssertFactory;
import org.hibernate.validator.HibernateValidator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.Path;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatBean;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * .
 *
 * @see CarTest
 */
@Slf4j
class CarTest_Test {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        try (var factory = Validation.byProvider(HibernateValidator.class)
                .configure()
                .addValueExtractor(new GearBoxValueExtractor())
                .buildValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    /**
     * .
     *
     * @see CarTest#validateCustomContainerElementConstraint
     */
    @Test
    void validateCustomContainerElementConstraint__() {
        final var car = new Car();
        car.setGearBox(new GearBox<>(new Gear.AcmeGear()));
        assertThatBean(car)
                .usingValidator(validator)
                .isNotValid(s -> {
                    assertThat(s).hasSize(1);
                    log.debug("class: {}", s.iterator().next().getPropertyPath().getClass());
                    assertThat(s)
                            .first(ValidationInstanceOfAssertFactories.constraintViolation())
                            .hasMessage("Gear is not providing enough torque.")
                            .extractingPropertyPath(ValidationAssertions::assertThatPath)
                            .hasToString("gearBox")
                            .hasSize(1)
                            .first()
                            .doesNotHaveIndex()
                            .hasName("gearBox")
                            .isNotInIterable()
                            .doesNotHaveKey()
                    ;
                });
    }
}
