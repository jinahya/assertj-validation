# assertj-validation — TODO

Open work only. Finished items, and the analysis behind them, are in the git history (`_ANALYSIS.md`, removed
2026-11-14). Every open item is tracked as a [GitHub issue](https://github.com/jinahya/assertj-validation/issues).

## 1. API consistency

### 1.1 The consumer fires at a different point in each method — [#9](https://github.com/jinahya/assertj-validation/issues/9)

| method | consumer runs | on failure the consumer… |
|---|---|---|
| `isValid(Consumer)` | before the assertion | sees the violations |
| `isValidFor(…, Consumer)` | before the assertion | sees the violations |
| `isNotValid(Consumer)` | after the assertion | never runs |
| `hasValidProperty(…, Consumer)` | after the assertion | never runs |

Whether a consumer is a callback that always observes or a post-success hook is a contract, and today it
depends on which method you called. Nothing in the javadoc mentions it. The executable-validation methods
inherited the "after" shape, so they need the same decision applied.

- [ ] Pick one — "before" reads better for a test library, since the violation set matters most exactly when
      the assertion is about to fail — apply it everywhere, and document it.

Reviewed with Codex: "before" is recommended, but it is a **behaviour change for released 0.6.x** (both 0.6.0 and
0.6.1 run these consumers after a passing assertion), so it waits for a maintainer decision. Signatures stay
compatible. The overloads that would move are `AbstractBeanAssert.isNotValid(Consumer)`,
`hasValidProperty(String, Consumer)`, `hasValidParameters(Method, Object[], Consumer)` and
`hasValidReturnValue(Method, Object, Consumer)`, and `AbstractConstructorAssert.hasValidParameters(Object[], Consumer)`
and `hasValidReturnValue(T, Consumer)` (through the private `executable(...)` and `validating(...)`). Decide also
whether every consumer gets the unmodifiable view `acceptViolations(...)` gives; these six pass the provider's set.

## 2. Test coverage

- [x] ~~**Assert on failure-message text.**~~ — **done**, `User_FailureMessages_Test`: stable fragments of the
      bean, property, group, method and constructor failures. It shows one inconsistency, pinned as it is: method
      parameter failures label themselves `method: …`, while constructor ones say `parameters of` and
      `return value of`; tracked as [#21](https://github.com/jinahya/assertj-validation/issues/21).
- [x] ~~**`DefaultIterableOfConstraintViolationsAssert.newAbstractIterableAssert`** — untested.~~ — **done**,
      through `filteredOn` in `ConstraintViolations_Test`, which keeps typed navigation.
- [x] ([#11](https://github.com/jinahya/assertj-validation/issues/11)) ~~**`AbstractGroupConversionDescriptorAssert`** — no fixture declares a `@ConvertGroup`.~~ — **done**,
      `GroupConversionDescriptorAssert_Test`.
- [x] ([#11](https://github.com/jinahya/assertj-validation/issues/11)) ~~**`AbstractContainerElementTypeDescriptorAssert`** — never exercised.~~ — **done**,
      `ContainerElementTypeDescriptorAssert_Test`, on `Bagged`'s existing `List<@NotBlank String>`.
- [x] ~~Constructor return-value validation through a standard constraint.~~ — **done**, `Account` in
      `ExecutableValidation_Test`: `@NotNull @Valid` on the constructor, `@NotBlank` reached by cascading.
- [x] ~~Move the tests which build a `ValidatorFactory` only to get at a validator onto
      `JinahyaValidationTestUtils.applyCachedValidatorFactory`.~~ — **done**, through
      `ValidationAssertionsTestUtils.applyValidator`; `User_UsingValidatorFactory_Test` and `User_UsingValidator_Test`
      keep their explicit setup, which they demonstrate.

## 3. Enhancements

- [x] ([#14](https://github.com/jinahya/assertj-validation/issues/14)) ~~**An assertion for `ConstraintViolationException`**~~ — **done**:
      `assertThatConstraintViolationException(e)` with `hasViolationOn(String)` and
      `extractingConstraintViolations()`, plus `CONSTRAINT_VIOLATION_EXCEPTION`. It extends `AbstractThrowableAssert`,
      and its violations are a `CollectionAssert<ConstraintViolation<?>>`, since one exception may carry violations
      of different root bean types.
- [ ] [#13](https://github.com/jinahya/assertj-validation/issues/13) **Convenience assertions on the violation set**, each removing a `Consumer` lambda from caller code:
      ```java
      assertThatBean(user).isNotValid().hasViolationOn("name");
      assertThatBean(user).isNotValid().hasViolationWithMessageTemplate("{…NotBlank.message}");
      assertThatBean(user).hasExactlyViolationsOn("name", "age");
      ```
      Needs a design decision first (Codex): `isNotValid()` returns the bean assertion and keeps no result, so a
      chained shortcut must either validate again, cache the result (with rules for a changed bean, groups or
      validator), or navigate explicitly to the result. Start in `AbstractIterableOfConstraintViolationsAssert`,
      where results already exist, and decide whether `hasExactlyViolationsOn` compares distinct paths.
- [ ] [#17](https://github.com/jinahya/assertj-validation/issues/17) **A bean `Condition`** for `assertThat(users).are(valid)`, built on assertj's `VerboseCondition` so a
      failure says why. Decide whether the collection form is worth it; a plain `is(valid)` adds nothing over
      `assertThatBean(user).isValid()`. If it goes ahead, take a caller-supplied `Validator`, rather than keep a
      factory or "last violations" state only to describe the failure.
- [ ] [#18](https://github.com/jinahya/assertj-validation/issues/18) **A JPMS `module-info`.** The jar carries only `Automatic-Module-Name`; at release 17 a real descriptor is
      available, and both dependencies are named modules. Codex's draft, to be checked against module-path
      consumers and the surefire setup before it ships, since an explicit module narrows readability and reflective
      access:
      ```java
      module com.github.jinahya.assertj.validation {
          requires transitive org.assertj.core;   // not static: provided means the caller supplies it, not optional
          requires transitive jakarta.validation;
          exports com.github.jinahya.assertj.validation;
      }
      ```
- [ ] [#19](https://github.com/jinahya/assertj-validation/issues/19) **`AbstractConstructorAssert` duplicates `AbstractBeanAssert`'s executable validation** (its private
      `validating(...)` mirrors `executable(...)`); worth a shared helper. After §1.1, so the helper does not
      freeze the inconsistent consumer timing; a package-private helper for the violation check and the failure
      text is enough.

## 4. Build

- [ ] [#20](https://github.com/jinahya/assertj-validation/issues/20) Move the default `version.org.assertj` to 4.x once 4.0.0 is released, and retire the `assertj-4` profile.

## 5. Deliberately not doing

Recorded so they are not re-litigated:

- **`usingValidatorFactorySuppliedBy(Supplier<? extends ValidatorFactory>)`, in any disposing form.** AssertJ
  has no post-assertion hook, so the scope would be one assertion rather than one chain; a shared factory
  passed through a supplier would be closed on the first assertion; closing would invalidate the violations,
  descriptors and paths already handed out; and Hibernate Validator does not enforce `close()`, so misuse
  passes locally. Reconsider only for a design whose factory lifetime is syntactically visible, such as a
  scoped callback: `assertThatBean(u).withValidatorFactory(supplier, a -> { a.isValid(); ... })`.
- **`Validator.unwrap(Class)`** — a provider escape hatch; yields nothing a test would assert on.
- **Bootstrap, configuration and SPI** (`Configuration`, `ValidatorFactory`, `ValidatorContext`, `Validation`,
  `spi.*`, `bootstrap.*`, `MessageInterpolator`, `TraversableResolver`, …) — they configure validation rather
  than produce values to verify.
- **Constraint annotations and exceptions** — inputs and outcomes, not assertion targets, except
  `ConstraintViolationException` (§3).
- **Per-concept assertion interfaces** — removed deliberately; assertj-core itself has none.
- **A second validation implementation** — the tests run on the reference implementation, Hibernate Validator,
  alone; if one is ever wanted, add a profile resolving it from Maven Central, as jinahya-validation does with
  Apache BVal.
