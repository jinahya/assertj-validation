package com.github.jinahya.assertj.validation;

/*-
 * #%L
 * assertj-validation
 * %%
 * Copyright (C) 2021 Jinahya, Inc.
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

import jakarta.validation.Path;
import java.util.Iterator;

/**
 * A class for verifying values against specified properties of specified bean types.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public class DefaultPathAssert
        extends AbstractPathAssert<DefaultPathAssert, DefaultPathAssert.DefaultNodeAssert> {

    public static class DefaultNodeAssert
            extends AbstractNodeAssert<DefaultNodeAssert> {

        static class DefaultBeanNodeAssert
                extends AbstractBeanNodeAssert<DefaultBeanNodeAssert> {

            DefaultBeanNodeAssert(final Path.BeanNode actual) {
                super(actual, DefaultBeanNodeAssert.class);
            }
        }

        static class DefaultParameterizedNodeAssert
                extends AbstractParameterNodeAssert<DefaultParameterizedNodeAssert> {

            DefaultParameterizedNodeAssert(final Path.ParameterNode actual, final Class<?> selfType) {
                super(actual, selfType);
            }
        }

        static class DefaultConstructorNodeAssert
                extends AbstractConstructorNodeAssert<DefaultConstructorNodeAssert> {

            DefaultConstructorNodeAssert(final Path.ConstructorNode actual) {
                super(actual, DefaultConstructorNodeAssert.class);
            }
        }

        static class DefaultCrossParameterNodeAssert
                extends AbstractCrossParameterNodeAssert<DefaultCrossParameterNodeAssert> {

            DefaultCrossParameterNodeAssert(final Path.CrossParameterNode actual) {
                super(actual, DefaultCrossParameterNodeAssert.class);
            }
        }

        static class DefaultMethodNodeAssert
                extends AbstractMethodNodeAssert<DefaultMethodNodeAssert> {

            DefaultMethodNodeAssert(final Path.MethodNode actual) {
                super(actual, DefaultMethodNodeAssert.class);
            }
        }

        static class DefaultParameterNodeAssert
                extends AbstractParameterNodeAssert<DefaultParameterNodeAssert> {

            DefaultParameterNodeAssert(final Path.ParameterNode actual) {
                super(actual, DefaultParameterNodeAssert.class);
            }
        }

        public static class DefaultPropertyNodeAssert
                extends AbstractPropertyNodeAssert<DefaultPropertyNodeAssert> {

            DefaultPropertyNodeAssert(final Path.PropertyNode actual) {
                super(actual, DefaultPropertyNodeAssert.class);
            }
        }

        // -------------------------------------------------------------------------------------------------------------
        DefaultNodeAssert(final Path.Node actual) {
            super(actual, DefaultNodeAssert.class);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance for verifying specified actual value.
     *
     * @param actual the actual value to verify.
     */
    public DefaultPathAssert(final Path actual) {
        super(actual, DefaultPathAssert.class);
    }

    @Override
    protected DefaultNodeAssert toAssert(final Path.Node value, final String description) {
        return new DefaultNodeAssert(value);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    protected DefaultPathAssert newAbstractIterableAssert(final Iterable<? extends Path.Node> iterable) {
        return new DefaultPathAssert(() -> (Iterator<Path.Node>) iterable.iterator());
    }
}
