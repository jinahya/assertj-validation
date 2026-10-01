package org.hibernate.validator.referenceguide.chapter02.containerelement.map;

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

import com.github.jinahya.assertj.validation.ValidationInstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatBean;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * .
 *
 * @see CarTest
 */
class CarTest_Test {

    /**
     * .
     *
     * @see CarTest#validateMapValueContainerElementConstraint()
     */
    @Test
    void validateMapValueContainerElementConstraint__() {
        final var car = new org.hibernate.validator.referenceguide.chapter02.containerelement.map.Car();
        car.setFuelConsumption(Car.FuelConsumption.HIGHWAY, 20);
        assertThatBean(car)
                .isNotValid(s -> {
                    assertThat(s)
                            .hasSize(1)
                            .first(ValidationInstanceOfAssertFactories.constraintViolation())
                            .hasMessage("20 is outside the max fuel consumption.")
                    ;
                });
    }

    /**
     * .
     *
     * @see CarTest#validateMapKeyContainerElementConstraint()
     */
    @Test
    void validateMapKeyContainerElementConstraint__() {
        final var car = new Car();
        car.setFuelConsumption(null, 5);
        assertThatBean(car)
                .isNotValid(s -> {
                    assertThat(s)
                            .hasSize(1)
                            .first(ValidationInstanceOfAssertFactories.constraintViolation())
                            .hasMessage("must not be null")
                    ;
                });
    }
}
