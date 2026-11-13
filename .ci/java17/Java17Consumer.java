/*
 * A consumer of the packaged assertj-validation jar, compiled and run on Java 17, the runtime baseline; the main test
 * suite is built at release 25 and so cannot check it. It touches each entry point once, with default validation, a
 * caller-owned validator, executables and paths.
 */

import jakarta.validation.Validation;
import jakarta.validation.constraints.NotBlank;

import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatBean;
import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatConstructor;
import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatPath;
import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatProperty;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class Java17Consumer {

    public static class Bean {

        @NotBlank(message = "invalid ${validatedValue}")
        public String name;

        public Bean(final String name) {
            this.name = name;
        }

        public void accept(@NotBlank final String value) {
        }
    }

    public static void main(final String... args) throws Exception {
        if (Runtime.version().feature() != 17) {
            throw new AssertionError("expected to run on Java 17 but runs on " + Runtime.version());
        }
        final Bean good = new Bean("ok");
        final Bean bad = new Bean("");

        // default validation: a provider discovered, a factory built and closed per assertion
        assertThatBean(good).isValid();
        assertThatBean(bad).isNotValid();
        assertThatThrownBy(() -> assertThatBean(bad).as("case 42").isValid())
                .isInstanceOf(AssertionError.class)
                .hasMessageStartingWith("[case 42]")
                .hasMessageContaining("messageTemplate:");

        assertThatProperty("ok").isValidFor(Bean.class, "name");

        final var method = Bean.class.getMethod("accept", String.class);
        assertThatBean(good).hasValidParameters(method, "ok");
        final var constructor = Bean.class.getConstructor(String.class);
        assertThatConstructor(constructor).hasValidParameters("ok");

        // a caller-owned validator, used while its factory is open
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            final var validator = factory.getValidator();
            assertThatBean(good).usingValidator(validator).isValid();
            final var violation = validator.validate(bad).iterator().next();
            assertThatPath(violation.getPropertyPath()).nodes().hasSize(1);
            assertThatPath(violation.getPropertyPath()).extractingPropertyNode(0).hasName("name");
        }
        System.out.println("Java 17 consumer passed on " + Runtime.version());
    }
}
