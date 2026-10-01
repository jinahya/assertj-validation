package com.github.jinahya.assertj.validation;

/*-
 * #%L
 * assertj-validation
 * %%
 * Copyright (C) 2021 - 2022 Jinahya, Inc.
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

import org.assertj.core.api.Condition;

import static com.github.jinahya.assertj.validation.ValidationAssertions.assertThatBean;

/**
 * Conditions for beans.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
final class BeanConditions {

    public static final Condition<Object> valid = new Condition<Object>() {
        @Override
        public boolean matches(final Object value) {
            try {
                assertThatBean(value).isValid();
                return true;
            } catch (final AssertionError ae) {
            }
            return false;
        }
    };

    private BeanConditions() {
        throw new AssertionError("instantiation is not allowed");
    }
}
