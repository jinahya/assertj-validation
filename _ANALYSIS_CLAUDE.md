# assertj-validation against AssertJ's own style — Claude's analysis

2026-11-14. Compared with `assertj/assertj-core` at `86e645780` (main, 2026-11-12), and with the
`assertj-build-3.27.7` and `assertj-build-4.0.0-M1` tags. Done independently of Codex's analysis
(`_ANALYSIS_CODEX.md`).

## Summary

The shape is AssertJ's: `Abstract*Assert<SELF, ACTUAL>` with a thin concrete class, one entry-point class, an
`InstanceOfAssertFactories` twin, and navigation through `extracting(Function, AssertFactory)`. The real gap is
not cosmetic. **The assertions do not work with AssertJ soft assertions**, on any AssertJ version the README
supports, and the fix differs between 3.x/4.0.0-M1 and the coming 4.0.0. After that the gaps are failure
messages, which are hand-built strings rather than AssertJ's `ErrorMessageFactory` style, and `has*` messages
that do not name what they compared.

| # | finding | severity |
|---|---|---|
| 1 | assertions are not soft-assertion aware (verified) | high |
| 2 | `has*` failures do not say which attribute differed (verified) | medium |
| 3 | failure text built by hand, not through `ErrorMessageFactory` / `Representation` | medium |
| 4 | failure-message tests match fragments, not the whole message | low |
| 5 | naming: `Default*Assert`, nested node asserts, no `then*` / `With*` | low, mostly justified |

## 1. Soft assertions — verified broken

Checked with a throwaway program running the packaged jar on Hibernate Validator 9.1.4:

```java
SoftAssertions softly = new SoftAssertions();
softly.proxy(DefaultBeanAssert.class, Object.class, invalidBean).isValid();   // THROWS immediately
softly.proxy(DefaultBeanAssert.class, Object.class, invalidBean).isNull();    // collected (AssertJ's own method)
```

Same result on 3.27.7 and 4.0.0-M1.

- **3.27.x and 4.0.0-M1** proxy the assert class with ByteBuddy (`SoftProxies`, matching `any()` methods).
  A `final` method cannot be overridden, so it is never intercepted. Every validation method here is
  `public final`: 13 in `AbstractBeanAssert`, 6 in `AbstractConstructorAssert`, 3 in `AbstractPropertyAssert`
  and 3 in `AbstractValidationAssert`. AssertJ itself makes a method `final` only where `@SafeVarargs` forces it.
  Even then it delegates to a non-final `…ForProxy` method, e.g. `AbstractIterableAssert.contains(ELEMENT...)`
  → `containsForProxy`, precisely so the proxy can see it.
- **main, the future 4.0.0**, removed ByteBuddy (`17b51320d`, "Remove bytebuddy use for SoftAssertions and
  Assumptions", #4199). An assertion is now soft-aware only if its body runs inside
  `executeAssertion(Runnable)`. Navigation methods must use `executeAssertionNavigation(...)`. Both are protected
  hooks on `AbstractAssert`, and `AbstractObjectAssert` alone calls `executeAssertion` 11 times. Nothing here
  calls them, so removing `final` will not be enough once 4.0.0 ships.
- No test uses `SoftAssertions`, `BDDSoftAssertions` or `Assumptions`, so nothing caught this.

The same mechanism drives `Assumptions`, so `assumeThat`-style use is affected the same way.

## 2. Entry points

`ValidationAssertions` (`ValidationAssertions.java`) follows `Assertions`: a final class with a private
constructor and static `assertThat*` factories. `ValidationInstanceOfAssertFactories` mirrors
`InstanceOfAssertFactories` closely: `UPPER_SNAKE` constants, generic factory methods where the type needs a
parameter, and `CONSTRAINT_VIOLATION_EXCEPTION` for `assertThatThrownBy(...).asInstanceOf(...)`. Both are good.

Differences from AssertJ:

- **Named entry points (`assertThatBean`, `assertThatPath`, …) instead of overloads of `assertThat`.** This is
  justified. `assertThatBean(Object)` would collide with `Assertions.assertThat(Object)` under static import, and
  the metadata types would make an overload set fragile. Keep it.
- **No `@CheckReturnValue`.** `Assertions` carries it at class level (`Assertions.java:159`), so a dangling
  `assertThatBean(user);` is flagged by Error Prone / IntelliJ. Nothing here has it. It is cheap to add, on the
  entry-point classes and the `extracting*` methods.
- **No BDD or mix-in variants.** AssertJ ships `BDDAssertions.then`, and `WithAssertions` for a test class to
  implement. None exists here. This is optional. If added, it should be one `then*` per `assertThat*`, generated
  or tested for parity.
- Entry points return the abstract type (`AbstractBeanAssert<?, ACTUAL>`). AssertJ does the same for many types
  (`AbstractStringAssert<?> assertThat(String)`), so this is consistent.

## 3. Main class structure

Matches AssertJ:

- `Abstract*Assert<SELF extends …<SELF, …>, ACTUAL>` extending `AbstractAssert`, with the constructor taking
  `(actual, selfType)`. This is AssertJ's pattern exactly.
- One concrete class per abstract one, with a public `(ACTUAL)` constructor. That constructor is what
  `SoftAssertionsProvider.proxy(...)` needs on every AssertJ version, so keep it.
- Navigation returns a new assert through `isNotNull().extracting(extractor, assertFactory)`
  (`AbstractConstraintViolationAssert`). Description and assertion state then propagate the way AssertJ's own
  navigation does. The `(extractor, AssertFactory)` overload next to a no-arg default is a good, AssertJ-like
  extension point.
- `usingValidator` / `usingValidatorFactory` follow AssertJ's `using…` vocabulary for configuring an assertion
  (`usingComparator`, `usingRecursiveComparison`). `targetingGroups` is a fair addition.

Diverges:

- **`Default*Assert` names.** AssertJ calls the concrete class by the plain name: `ObjectAssert` extends
  `AbstractObjectAssert`, `ClassAssert` extends `AbstractClassAssert`. Here it would be `BeanAssert`,
  `ConstraintViolationAssert`, `PathAssert`. Renaming is a breaking change to released 0.6.x, so it is a
  1.0 decision, not a fix. The `Default` prefix is clear, just not AssertJ's.
- **Node asserts are nested in `AbstractPathAssert`** (1,817 lines), with `DefaultPathAssert.DefaultNodeAssert.Default…NodeAssert`
  two levels deep. Their traits are interfaces with `default` methods. AssertJ keeps one assert type per file and
  has no nested assert classes. The mix-in interfaces are a reasonable answer to the `Path.Node` sub-interfaces,
  but the nesting makes the public type names long (`AbstractPathAssert.AbstractPropertyNodeAssert<?>`) and the
  file the largest in the module. Moving each node assert to its own top-level file is a source-incompatible
  move, so again a 1.0 decision.
- **`AbstractBeanAssert extends AbstractPropertyAssert`.** A bean can be validated as a property value
  (`isValidFor`), so this is defensible. AssertJ, though, models "also usable as" through shared interfaces,
  not inheritance. Fine to keep; worth a javadoc sentence.
- **Mutable configuration on the assertion** (`groups`, `validator`, `validatorFactory`). AssertJ keeps
  comparison strategy on the assertion too, so this is in style. Unlike AssertJ, it is not carried across
  navigation, which is correct because navigated asserts do not validate.

## 4. Assertion method style

Matches AssertJ:

- Vocabulary: `isValid` / `isNotValid`, `has…` / `doesNotHave…`, `hasNo…`, `extracting…`, `…Satisfying`.
- `isNotNull()` before touching `actual`, so a null bean fails as `Expecting actual not to be null`. Verified,
  and the same as AssertJ.
- `AbstractPropertyAssert` correctly does *not* null-check, since a null property value is valid input.
- Argument checks use `Objects.requireNonNull(x, "x is null")`. AssertJ uses `requireNonNull(x, "...")`
  in the same way.
- Failures go through `failure(...)` / `failWithMessage(...)`, so `as(...)` and `withFailMessage(...)` are
  honoured. `AbstractValidationAssert.checkViolations` documents this. `AbstractPathAssert` uses AssertJ's own
  `ShouldBeEmpty`, `ShouldHaveSize`, … factories through `throwAssertionError`, which is the most AssertJ-like
  code in the module.

Diverges:

- **`has*` messages lose the attribute name (verified).** `hasMessageTemplate`, `hasInvalidValue`,
  `hasRootBeanClass` and the like are `extractingX().isEqualTo(expected)`. A failure reads only:

  ```
  expected: "x"
   but was: ""
  ```

  It does not say "invalidValue". AssertJ's equivalents name the attribute: `AbstractThrowableAssert.hasMessage`
  fails with `ShouldHaveMessage` ("Expecting message to be: … but was: …"), and `extracting(String)` sets the
  description `Extracted: name`. Either build a `ShouldHave…`-style factory, or describe the extracted assert,
  combining with the caller's own description rather than replacing it.
- **Validation failures are hand-formatted strings.** `checkViolations` and its callers build a multi-line
  `String.format` text, and the context text is copy-pasted five times in `AbstractBeanAssert` and again in
  `AbstractPropertyAssert` / `AbstractConstructorAssert`. AssertJ's convention is one `ErrorMessageFactory` per
  kind of failure (`BasicErrorMessageFactory` subclasses named `ShouldBeValid`, `ShouldNotBeValid`, …).
  Its values are formatted through the assertion's `Representation`. Here `actual` and the groups print with
  `toString()`: `MsgCheck$Bean@45099dd3`, `[interface jakarta.validation.groups.Default]`. A
  `Representation` registered by the user is therefore ignored. Moving to factories would also give #19 its
  shared helper.
- **Consumer overloads instead of `…Satisfying`.** `isValid(Consumer<Set<ConstraintViolation>>)` passes the raw
  violation set. The AssertJ way to inspect a by-product is to navigate to it and assert on it there. Here, for
  instance, an `extractingViolations()` would return the `AbstractIterableOfConstraintViolationsAssert`. That
  also decides #9 (consumer timing) and #13 (chained `hasViolationOn`) without caching state. This is a design
  direction, not a defect.

## 5. Tests

Matches AssertJ:

- **Per-method test classes** named `Subject_method_Test` (`User_IsValid_Test`, `ConstraintViolationAssert_InvalidValue_Test`).
  This is AssertJ's `ObjectAssert_hasFieldOrProperty_Test` convention; assertj-core has 2,994 `*_Test` classes.
- Tests sit in `example.user` / `example.service`, outside the main package, so they exercise the public API only.
  This is a good discipline that AssertJ's own tests (same package) do not have.

Diverges:

- **Test method names.** AssertJ names each case by its outcome, `should_fail_if_actual_is_null`
  (696 of those alone), `should_pass_if_…`. Here the most common names are `__()`, `__ValidAge()`, `isValid__()`,
  with the meaning in `@DisplayName`. The display name is not shown in a plain `mvn` failure line, and many
  `__` methods read the same in a stack trace.
- **Failures asserted by type only.** `assertThatThrownBy(...)` appears 139 times and mostly stops at
  `.isInstanceOf(AssertionError.class)`. AssertJ uses `expectAssertionError(() -> …)` (`util/AssertionsUtil`) and
  then `then(error).hasMessage(shouldX(...).create())`. That compares the whole message, built by the same
  factory. `User_FailureMessages_Test` checks fragments with `hasMessageContaining`, which is the right first step.
  Once item 3 introduces factories, whole-message checks become possible.
- **Missing cases AssertJ always has:** `should_fail_if_actual_is_null` per assertion, a `…honoring_current_description`
  case for `as(...)`, and, given item 1, a soft-assertion case. Only `User_DescriptionAndOverride_Test` covers
  description and override, for one method.
- Naming is not uniform: `ValidationAssertMessagesTest`, `UserConditionsTest` and `ConstraintViolationAssert_$Test`
  sit beside the `_Test` classes.

## 6. Recommendations, ranked

1. **Make assertions soft-aware.** Drop `final` from the public assertion methods, keeping `final` only on
   internals. Add a test with `SoftAssertions.proxy(...)` on 3.27.x and 4.0.0-M1. Before 4.0.0 becomes the
   default (#20), wrap each body in `executeAssertion(...)` and each navigation in `executeAssertionNavigation(...)`.
   That needs the 4.x baseline, so it belongs with #20. Open an issue for it.
2. **Name the attribute in `has*` failures.** Use either `ShouldHave…`-style factories or a described extracted
   assert. Add a test asserting the attribute name appears.
3. **Move validation failures to `ErrorMessageFactory` classes** (`ShouldBeValid`, `ShouldNotBeValid`,
   `ShouldHaveValidProperty`, …), formatting values through `Representation`. This removes the five copy-pasted
   context blocks and gives #19 its shared helper.
4. **Add `@CheckReturnValue`** to `ValidationAssertions`, `ValidationInstanceOfAssertFactories` and the
   `extracting*` methods.
5. **Tests:** rename to `should_…`, add per-assertion null and description cases, and compare whole messages once 3
   lands.
6. **Hold for 1.0:** `Default*Assert` → plain names, node asserts out of `AbstractPathAssert`, and an
   `extractingViolations()` navigation in place of the `Consumer` overloads (#9, #13). Each is source-incompatible.

Keep as they are: the named entry points, `ValidationInstanceOfAssertFactories`, the `(extractor, AssertFactory)`
navigation overloads, `using…` configuration, and tests living outside the main package.
