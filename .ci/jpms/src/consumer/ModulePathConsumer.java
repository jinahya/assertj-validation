package consumer;

import com.github.jinahya.assertj.validation.BeanAssert;
import com.github.jinahya.assertj.validation.ValidationInstanceOfAssertFactories;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.constraints.NotBlank;
import org.assertj.core.api.SoftAssertions;

import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatBean;
import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatConstraintViolation;
import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatPath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Exercises assertj-validation from a named module: default validation, navigation, the instance-of factories, and
 * soft-assertion proxies, which AssertJ generates into this module's assertion package.
 */
public class ModulePathConsumer {

    public static class Bean {

        @NotBlank
        public String name;

        public Bean(final String name) {
            this.name = name;
        }
    }

    @SuppressWarnings({"unchecked"}) // SoftAssertions.proxy(..) with a raw assertion class
    public static void main(final String... args) {
        final Module module = BeanAssert.class.getModule();
        if (!module.isNamed() || module.getDescriptor().isAutomatic()) {
            throw new AssertionError("expected an explicit module, but was " + module.getDescriptor());
        }

        // default validation: the provider is discovered through jakarta.validation's service loader
        assertThatBean(new Bean("ok")).isValid();
        assertThatThrownBy(() -> assertThatBean(new Bean("")).as("case 42").isValid())
                .hasMessageStartingWith("[case 42]");

        // navigation, and narrowing through the instance-of factories
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            final ConstraintViolation<Bean> violation = factory.getValidator().validate(new Bean("")).iterator().next();
            assertThatConstraintViolation(violation).extractingPropertyPath().extractingPropertyNode(0).hasName("name");
            assertThat((Object) violation.getPropertyPath())
                    .asInstanceOf(ValidationInstanceOfAssertFactories.PATH)
                    .hasSize(1);
        }

        // soft assertions: AssertJ subclasses the assertion class, in its own package
        final var softly = new SoftAssertions();
        softly.proxy(BeanAssert.class, Object.class, new Bean("")).isValid();
        softly.proxy(BeanAssert.class, Object.class, new Bean(" ")).hasValidProperty("name");
        if (softly.errorsCollected().size() != 2) {
            throw new AssertionError("expected 2 collected errors, but was " + softly.errorsCollected().size());
        }
        System.out.println("module-path consumer passed on " + Runtime.version() + " with " + module.getDescriptor());
    }
}
