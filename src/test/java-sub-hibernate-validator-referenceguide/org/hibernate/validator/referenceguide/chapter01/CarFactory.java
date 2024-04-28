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

import static java.util.concurrent.ThreadLocalRandom.current;

final class CarFactory {

    static String validManufacturer() {
        return current().nextBoolean() ? "" : "manufacturer";
    }

    static String invalidManufacturer() {
        return null;
    }

    static String validLicensePlate() {
        return current().nextBoolean() ? "  " : "              ";
    }

    static String invalidLicensePlate() {
        return current().nextBoolean()
                ? null
                : current().nextBoolean() ? ""
                : current().nextBoolean() ? " " : "               ";
    }

    static int validSeatCount() {
        return (current().nextInt() >>> 1) | 2;
    }

    static int invalidSeatCount() {
        return ~validSeatCount();
    }

    static org.hibernate.validator.referenceguide.chapter01.Car of(final String manufacturer, final String licencePlate,
                                                                   final int seatCount) {
        return new org.hibernate.validator.referenceguide.chapter01.Car(manufacturer, licencePlate, seatCount);
    }

    static org.hibernate.validator.referenceguide.chapter01.Car carOf(final String manufacturer, final String licencePlate,
                                                                      final int seatCount) {
        return of(manufacturer, licencePlate, seatCount);
    }

    private CarFactory() {
        throw new AssertionError("instantiation is not allowed");
    }
}
