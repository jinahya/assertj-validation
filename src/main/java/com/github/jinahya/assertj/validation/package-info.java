/**
 * Defines assertion classes, based on top of <a href="https://assertj.github.io/doc/">AssertJ</a>, for fluently
 * verifying objects and values using <a href="https://beanvalidation.org/">Bean-Validation</a>.
 * {@snippet lang = "java" id = "user":
 * class User {
 *
 *     @NotBlank
 *     final String name;
 *
 *     @Max(0x7F)
 *     @Min(value = 60, groups = {Senior.class}) // @highlight regex="Senior|60" type=highlighted
 *     @Max(value = 59, groups = {Junior.class}) // @highlight regex="Junior|59" type=highlighted
 *     @PositiveOrZero
 *     final int age;
 * }
 *
 * abstract class Registration {
 *
 *     @Valid @NotNull final User user; // @highlight regex="@(Valid|NotNull)" type=highlighted
 * }
 *
 * class JuniorRegistration extends Registration { // @highlight regex="Junior" type=highlighted
 *
 *     @Junior // age should be less than or equal to 59 // @highlight regex="@Junior|59" type=highlighted
 *     @Override User getUser() { return super.getUser(); }
 * }
 *
 * class SeniorRegistration extends Registration { // @highlight regex="Senior" type=highlighted
 *
 *     @Senior // age should be greater that or equal to 60 // @highlight regex="@Senior|60" type=highlighted
 *     @Override User getUser() { return super.getUser(); }
 * }
 *}
 * <h2>Verifying values against properties</h2>
 * {@snippet lang = "java" id = "assertThatProperty":
 * // @highlight region substring="fail" type=highlighted
 * // @link region substring="assertThatBean" target="com.github.jinahya.assertj.validation.ValidationAssertions#assertThatBean(Object)"
 * // @link region substring="assertThatProperty" target="ValidationAssertions#assertThatProperty(Object)"
 * // @link region substring="targetingGroups" target="ValidationAssert#targetingGroups(Class[])"
 * // @link region substring=".isValid()" target="BeanAssert#isValid()"
 * // @link region substring="isValidFor" target="PropertyAssert#isValidFor(Class, String)"
 * // @link region substring="isNotValidFor" target="PropertyAssert#isNotValidFor(Class, String)"
 * assertThatProperty("Jane").isValidFor(User.class, "name"); // should pass
 * assertThatProperty(  null).isValidFor(User.class, "name"); // should fail // @highlight regex="\-?(null|name)" type=highlighted
 * assertThatProperty(    "").isValidFor(User.class, "name"); // should fail // @highlight regex='(\"\"|name)' type=highlighted
 * assertThatProperty(   " ").isValidFor(User.class, "name"); // should fail // @highlight regex='(\"\s\"|name)' type=highlighted
 *
 * assertThatProperty(  0).isValidFor(User.class, "age"); // should pass
 * assertThatProperty( 28).isValidFor(User.class, "age"); // should pass
 * assertThatProperty( -1).isValidFor(User.class, "age"); // should fail // @highlight regex="\-?(\d+|age)" type=highlighted
 * assertThatProperty(300).isValidFor(User.class, "age"); // should fail // @highlight regex="\-?(\d+|age)" type=highlighted
 *
 * assertThatProperty( 59)               .isValidFor   (User.class, "age")
 *         .targetingGroups(Junior.class).isValidFor   (User.class, "age")
 *         .targetingGroups(Senior.class).isNotValidFor(User.class, "age");
 *
 * assertThatProperty( 60)               .isValidFor   (User.class, "age")
 *         .targetingGroups(Junior.class).isNotValidFor(User.class, "age")
 *         .targetingGroups(Senior.class).isValidFor   (User.class, "age");
 * // @end
 * // @end
 * // @end
 * // @end
 * // @end
 * // @end
 * // @end
 *}
 * <h2>Verifying beans</h2>
 * {@snippet lang = "java" id = "assertThatBean":
 * // @highlight region substring="fail" type=highlighted
 * // @link region substring="assertThatBean" target="com.github.jinahya.assertj.validation.ValidationAssertions#assertThatBean(Object)"
 * // @link region substring="assertThatProperty" target="ValidationAssertions#assertThatProperty(Object)"
 * // @link region substring="targetingGroups" target="ValidationAssert#targetingGroups(Class[])"
 * // @link region substring=".isValid()" target="BeanAssert#isValid()"
 * // @link region substring="isValidFor" target="PropertyAssert#isValidFor(Class, String)"
 * // @link region substring="isNotValidFor" target="PropertyAssert#isNotValidFor(Class, String)"
 * assertThatBean(new User("Jane", 28)).hasValidProperty("name"); // should pass
 * assertThatBean(new User("Jane", 28)).hasValidProperty( "age"); // should pass
 * assertThatBean(new User(  null,  0)).hasValidProperty("name"); // should fail // @highlight regex="\-?(null|name)" type=highlighted
 * assertThatBean(new User(  null,  0)).hasValidProperty( "age"); // should pass
 * assertThatBean(new User("John", -1)).hasValidProperty("name"); // should pass
 * assertThatBean(new User("Jane", -1)).hasValidProperty( "age"); // should fail // @highlight regex="\-?(\d+|age)" type=highlighted
 *
 * assertThatBean(new User("Jane", 28))
 *         .isValid()                                       // should pass
 *         .isValidFor(JuniorRegistration.class, "user")    // should pass
 *         .isNotValidFor(SeniorRegistration.class, "user") // should pass
 *         ;
 * // @end
 * // @end
 * // @end
 * // @end
 * // @end
 * // @end
 * // @end
 *}
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
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
