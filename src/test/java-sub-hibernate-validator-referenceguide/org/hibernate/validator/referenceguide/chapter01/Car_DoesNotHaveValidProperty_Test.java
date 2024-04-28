package org.hibernate.validator.referenceguide.chapter01;

/*-
 * #%L
 * assertj-bean-validation
 * %%
 * Copyright (C) 2021 - 2023 Jinahya, Inc.
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

import org.junit.jupiter.api.RepeatedTest;

import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatBean;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hibernate.validator.referenceguide.chapter01.Car_Constants.PROPERTY_LICENSE_PLATE;
import static org.hibernate.validator.referenceguide.chapter01.Car_Constants.PROPERTY_MANUFACTURER;
import static org.hibernate.validator.referenceguide.chapter01.Car_Constants.PROPERTY_SEAT_COUNT;
import static org.hibernate.validator.referenceguide.chapter01.CarFactory.carOf;
import static org.hibernate.validator.referenceguide.chapter01.CarFactory.invalidLicensePlate;
import static org.hibernate.validator.referenceguide.chapter01.CarFactory.invalidManufacturer;
import static org.hibernate.validator.referenceguide.chapter01.CarFactory.invalidSeatCount;
import static org.hibernate.validator.referenceguide.chapter01.CarFactory.validLicensePlate;
import static org.hibernate.validator.referenceguide.chapter01.CarFactory.validManufacturer;
import static org.hibernate.validator.referenceguide.chapter01.CarFactory.validSeatCount;

class Car_DoesNotHaveValidProperty_Test {

    @RepeatedTest(128)
//    @Test
    void __invalidManufacturer() {
        final var car = carOf(invalidManufacturer(), validLicensePlate(), validSeatCount());
        final var assertion = assertThatBean(car);
        assertThatCode(() -> assertion.doesNotHaveValidProperty(PROPERTY_MANUFACTURER))
                .doesNotThrowAnyException();
        assertThatThrownBy(() -> assertion.doesNotHaveValidProperty(PROPERTY_LICENSE_PLATE))
                .isInstanceOf(AssertionError.class);
        assertThatThrownBy(() -> assertion.doesNotHaveValidProperty(PROPERTY_SEAT_COUNT))
                .isInstanceOf(AssertionError.class);
    }

    @RepeatedTest(128)
//    @Test
    void __invalidLicensePlate() {
        final var car = carOf(validManufacturer(), invalidLicensePlate(), validSeatCount());
        final var assertion = assertThatBean(car);
        assertThatThrownBy(() -> assertion.doesNotHaveValidProperty(PROPERTY_MANUFACTURER))
                .isInstanceOf(AssertionError.class);
        assertThatCode(() -> assertion.doesNotHaveValidProperty(PROPERTY_LICENSE_PLATE))
                .doesNotThrowAnyException();
        assertThatThrownBy(() -> assertion.doesNotHaveValidProperty(PROPERTY_SEAT_COUNT))
                .isInstanceOf(AssertionError.class);
    }

    @RepeatedTest(128)
//    @Test
    void __invalidSeatCount() {
        final var car = carOf(validManufacturer(), validLicensePlate(), invalidSeatCount());
        final var assertion = assertThatBean(car);
        assertThatThrownBy(() -> assertion.doesNotHaveValidProperty(PROPERTY_MANUFACTURER))
                .isInstanceOf(AssertionError.class);
        assertThatThrownBy(() -> assertion.doesNotHaveValidProperty(PROPERTY_LICENSE_PLATE))
                .isInstanceOf(AssertionError.class);
        assertThatCode(() -> assertion.doesNotHaveValidProperty(PROPERTY_SEAT_COUNT))
                .doesNotThrowAnyException();
    }
}
