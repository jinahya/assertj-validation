# assertj-validation — TODO

Open work only. Finished items, and the analysis behind them, are in the git history (`_ANALYSIS.md`, removed
2026-11-14).

## 1. API consistency

### 1.1 The consumer fires at a different point in each method

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

## 2. Test coverage

- [ ] **Assert on failure-message text.** The library's whole value is its failure output, yet the tests only
      check that an `AssertionError` is thrown, never what it says.
- [ ] **`DefaultIterableOfConstraintViolationsAssert.newAbstractIterableAssert`** — untested.
- [ ] **`AbstractGroupConversionDescriptorAssert`** — no fixture declares a `@ConvertGroup`, so the assert is
      compiled but never exercised.
- [ ] **`AbstractContainerElementTypeDescriptorAssert`** — no fixture declares a container-element constraint
      (`List<@NotBlank String>`).
- [ ] Constructor return-value validation is exercised only through a custom `@Named` fixture constraint; add
      a case through a standard constraint.
- [ ] Move the tests which build a `ValidatorFactory` only to get at a validator onto
      `JinahyaValidationTestUtils.applyCachedValidatorFactory`; keep the explicit setup in
      `User_UsingValidatorFactory_Test` and `User_UsingValidator_Test`, which demonstrate it.

## 3. Enhancements

- [ ] **An assertion for `ConstraintViolationException`**, the last uncovered runtime type, e.g.
      `assertThatConstraintViolationException(e).hasViolationOn("name")`, for validation triggered by a
      framework rather than called from the test.
- [ ] **Convenience assertions on the violation set**, each removing a `Consumer` lambda from caller code:
      ```java
      assertThatBean(user).isNotValid().hasViolationOn("name");
      assertThatBean(user).isNotValid().hasViolationWithMessageTemplate("{…NotBlank.message}");
      assertThatBean(user).hasExactlyViolationsOn("name", "age");
      ```
- [ ] **A bean `Condition`** for `assertThat(users).are(valid)`, built on assertj's `VerboseCondition` so a
      failure says why. Decide whether the collection form is worth it; a plain `is(valid)` adds nothing over
      `assertThatBean(user).isValid()`.
- [ ] **A JPMS `module-info`.** The jar carries only `Automatic-Module-Name`; at release 17 a real descriptor is
      available, and both dependencies are named modules.
- [ ] **`AbstractConstructorAssert` duplicates `AbstractBeanAssert`'s executable validation** (its private
      `validating(...)` mirrors `executable(...)`); worth a shared helper.

## 4. Build

- [ ] Move the default `version.org.assertj` to 4.x once 4.0.0 is released, and retire the `assertj-4` profile.

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
