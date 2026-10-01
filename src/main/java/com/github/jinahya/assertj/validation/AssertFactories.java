package com.github.jinahya.assertj.validation;

/*-
 * #%L
 * assertj-validation
 * %%
 * Copyright (C) 2021 - 2024 Jinahya, Inc.
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

final class AssertFactories {

//    public static <NODE extends Path.Node, ASSERT extends PathAssert.NodeAssert<? extends ASSERT, NODE>> ASSERT nodeAssert(
//            final Class<NODE> nodeType, final AssertFactory<? super NODE, ? extends ASSERT> factory) {
//        return null;
//    }
//
//    public static <NODE extends Path.Node, ASSERT extends PathAssert.NodeAssert<? extends ASSERT, NODE>> InstanceOfAssertFactory<NODE, ASSERT> nodeAssert(
//            final Class<NODE> nodeType, final AssertFactory<? super NODE, ? extends ASSERT> factory) {
//        return null;
//    }

    private AssertFactories() {
        throw new AssertionError("instantiation is not allowed");
    }
}
