package org.hibernate.validator.referenceguide.chapter02.containerelement.map;

import org.junit.jupiter.api.Test;

import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatBean;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * .
 *
 * @see org.hibernate.validator.referenceguide.chapter02.containerelement.map.CarTest
 */
class CarTest_Test {

    /**
     * .
     *
     * @see org.hibernate.validator.referenceguide.chapter02.containerelement.map.CarTest#validateMapValueContainerElementConstraint()
     */
    @Test
    void validateMapValueContainerElementConstraint() {
        final var car = new org.hibernate.validator.referenceguide.chapter02.containerelement.map.Car();
        car.setFuelConsumption(Car.FuelConsumption.HIGHWAY, 20);
        assertThatBean(car)
                .isNotValid(s -> {
                    assertThat(s)
                            .hasSize(1)
                            .allSatisfy(cv -> {
//                                assertThatConstraintViolation(cv)
//                                        .hasMessage("20 is outside the max fuel consumption.");
                                assertThat(cv.getPropertyPath().toString())
                                        .isEqualTo("fuelConsumption[HIGHWAY].<map value>");
                            })
                    ;
                });
    }

    @Test
    void validateMapKeyContainerElementConstraint() {
        final var car = new org.hibernate.validator.referenceguide.chapter02.containerelement.map.Car();
        car.setFuelConsumption(org.hibernate.validator.referenceguide.chapter02.containerelement.map.Car.FuelConsumption.HIGHWAY, 20);
        assertThatBean(car)
                .isNotValid(s -> {
                    assertThat(s)
                            .hasSize(1)
                            .allSatisfy(cv -> {
                            });
                });
    }
}
