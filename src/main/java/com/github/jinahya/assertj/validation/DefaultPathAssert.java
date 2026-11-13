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

/**
 * A class for verifying {@link Path} values.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public class DefaultPathAssert
        extends AbstractPathAssert<DefaultPathAssert> {

    /**
     * A class for verifying {@link Path.Node} values, of any kind.
     */
    public static class DefaultNodeAssert
            extends AbstractNodeAssert<DefaultNodeAssert> {

        /**
         * A class for verifying {@link Path.BeanNode} values.
         */
        public static class DefaultBeanNodeAssert
                extends AbstractBeanNodeAssert<DefaultBeanNodeAssert> {

            /**
             * Creates a new instance for verifying specified bean node.
             *
             * @param actual the bean node to verify.
             */
            public DefaultBeanNodeAssert(final Path.BeanNode actual) {
                super(actual, DefaultBeanNodeAssert.class);
            }
        }

        /**
         * A class for verifying {@link Path.ConstructorNode} values.
         */
        public static class DefaultConstructorNodeAssert
                extends AbstractConstructorNodeAssert<DefaultConstructorNodeAssert> {

            /**
             * Creates a new instance for verifying specified constructor node.
             *
             * @param actual the constructor node to verify.
             */
            public DefaultConstructorNodeAssert(final Path.ConstructorNode actual) {
                super(actual, DefaultConstructorNodeAssert.class);
            }
        }

        /**
         * A class for verifying {@link Path.CrossParameterNode} values.
         */
        public static class DefaultCrossParameterNodeAssert
                extends AbstractCrossParameterNodeAssert<DefaultCrossParameterNodeAssert> {

            /**
             * Creates a new instance for verifying specified cross parameter node.
             *
             * @param actual the cross parameter node to verify.
             */
            public DefaultCrossParameterNodeAssert(final Path.CrossParameterNode actual) {
                super(actual, DefaultCrossParameterNodeAssert.class);
            }
        }

        /**
         * A class for verifying {@link Path.MethodNode} values.
         */
        public static class DefaultMethodNodeAssert
                extends AbstractMethodNodeAssert<DefaultMethodNodeAssert> {

            /**
             * Creates a new instance for verifying specified method node.
             *
             * @param actual the method node to verify.
             */
            public DefaultMethodNodeAssert(final Path.MethodNode actual) {
                super(actual, DefaultMethodNodeAssert.class);
            }
        }

        /**
         * A class for verifying {@link Path.ParameterNode} values.
         */
        public static class DefaultParameterNodeAssert
                extends AbstractParameterNodeAssert<DefaultParameterNodeAssert> {

            /**
             * Creates a new instance for verifying specified parameter node.
             *
             * @param actual the parameter node to verify.
             */
            public DefaultParameterNodeAssert(final Path.ParameterNode actual) {
                super(actual, DefaultParameterNodeAssert.class);
            }
        }

        /**
         * A class for verifying {@link Path.ContainerElementNode} values.
         */
        public static class DefaultContainerElementNodeAssert
                extends AbstractContainerElementNodeAssert<DefaultContainerElementNodeAssert> {

            /**
             * Creates a new instance for verifying specified container element node.
             *
             * @param actual the container element node to verify.
             */
            public DefaultContainerElementNodeAssert(final Path.ContainerElementNode actual) {
                super(actual, DefaultContainerElementNodeAssert.class);
            }
        }

        /**
         * A class for verifying {@link Path.ReturnValueNode} values.
         */
        public static class DefaultReturnValueNodeAssert
                extends AbstractReturnValueNodeAssert<DefaultReturnValueNodeAssert> {

            /**
             * Creates a new instance for verifying specified return value node.
             *
             * @param actual the return value node to verify.
             */
            public DefaultReturnValueNodeAssert(final Path.ReturnValueNode actual) {
                super(actual, DefaultReturnValueNodeAssert.class);
            }
        }

        /**
         * A class for verifying {@link Path.PropertyNode} values.
         */
        public static class DefaultPropertyNodeAssert
                extends AbstractPropertyNodeAssert<DefaultPropertyNodeAssert> {

            /**
             * Creates a new instance for verifying specified property node.
             *
             * @param actual the property node to verify.
             */
            public DefaultPropertyNodeAssert(final Path.PropertyNode actual) {
                super(actual, DefaultPropertyNodeAssert.class);
            }
        }

        // -------------------------------------------------------------------------------------------------------------

        /**
         * Creates a new instance for verifying specified node.
         *
         * @param actual the node to verify.
         */
        public DefaultNodeAssert(final Path.Node actual) {
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
}
