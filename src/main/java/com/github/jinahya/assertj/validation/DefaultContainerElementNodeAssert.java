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
 * A class for verifying {@link Path.ContainerElementNode} values.
 */
public class DefaultContainerElementNodeAssert
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
