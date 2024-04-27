package com.github.jinahya.assertj.validation;

@SuppressWarnings({
        "java:S119" // <ASSERT ...>
})
abstract class AbstractBeanAssertTest<ASSERT extends AbstractBeanAssert<ASSERT, ?>>
        extends BeanAssertTest<ASSERT> {

    AbstractBeanAssertTest(final Class<ASSERT> assertionClass) {
        super(assertionClass);
    }
}
