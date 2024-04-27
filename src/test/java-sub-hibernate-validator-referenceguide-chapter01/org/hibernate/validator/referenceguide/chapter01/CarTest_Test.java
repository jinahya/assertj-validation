package org.hibernate.validator.referenceguide.chapter01;

import org.junit.jupiter.api.Test;

import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatBean;

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
                .isNotValid()
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
    void seatCountTooLow() {
        final var car = new Car("Morris", "DD-AB-123", 1);
        assertThatBean(car)
                .isNotValid()
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
                .isValid()
                .hasValidProperty(Car_Constants.PROPERTY_LICENSE_PLATE)
                .hasValidProperty(Car_Constants.PROPERTY_MANUFACTURER)
                .hasValidProperty(Car_Constants.PROPERTY_SEAT_COUNT)
        ;
    }
}
