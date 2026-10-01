package org.hibernate.validator.referenceguide.chapter01;

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
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * .
 *
 * @see org.hibernate.validator.referenceguide.chapter01.CarTest
 */
class CarTest_Test {

    /**
     * .
     *
     * @see CarTest#manufacturerIsNull()
     */
    @Test
    void manufacturerIsNull__() {
        final var car = new Car(null, "DD-AB-123", 4);
        assertThatBean(car)
                .isNotValid()
                .doesNotHaveValidProperty(Car_Constants.PROPERTY_MANUFACTURER)
                .hasValidProperty(Car_Constants.PROPERTY_LICENSE_PLATE)
                .hasValidProperty(Car_Constants.PROPERTY_SEAT_COUNT)
        ;
    }

    /**
     * .
     *
     * @see CarTest#licensePlateTooShort()
     */
    @Test
    void licensePlateTooShort__() {
        final var car = new Car("Morris", "D", 4);
        assertThatBean(car)
                .isNotValid(s -> {
                    assertThat(s)
                            .isNotEmpty()
                            .first(ValidationInstanceOfAssertFactories.constraintViolation())
                            .hasMessage("size must be between 2 and 14")
                    ;
                })
                .doesNotHaveValidProperty(Car_Constants.PROPERTY_LICENSE_PLATE)
                .hasValidProperty(Car_Constants.PROPERTY_MANUFACTURER)
                .hasValidProperty(Car_Constants.PROPERTY_MANUFACTURER)
        ;
    }

    /**
     * .
     *
     * @see CarTest#seatCountTooLow()
     */
    @Test
    void seatCountTooLow__() {
        final var car = new Car("Morris", "DD-AB-123", 1);
        assertThatBean(car)
                .isNotValid(s -> {
                    assertThat(s)
                            .isNotEmpty()
                            .first(ValidationInstanceOfAssertFactories.constraintViolation())
                            .hasMessage("must be greater than or equal to 2")
                    ;
                })
                .doesNotHaveValidProperty(Car_Constants.PROPERTY_SEAT_COUNT)
                .hasValidProperty(Car_Constants.PROPERTY_MANUFACTURER)
                .hasValidProperty(Car_Constants.PROPERTY_LICENSE_PLATE)
        ;
    }

    /**
     * .
     *
     * @see CarTest#carIsValid()
     */
    @Test
    void carIsValid() {
        final var car = new Car("Morris", "DD-AB-123", 2);
        assertThatBean(car)
                .isValid(s -> {
                    assertThat(s).isEmpty();
                })
                .hasValidProperty(Car_Constants.PROPERTY_LICENSE_PLATE)
                .hasValidProperty(Car_Constants.PROPERTY_MANUFACTURER)
                .hasValidProperty(Car_Constants.PROPERTY_SEAT_COUNT)
        ;
    }
}
