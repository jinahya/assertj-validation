# assertj-validation — TODO

Open work as of 2026-10-01, after the build realignment, the interface-layer collapse and the API-coverage
completion. Section references point at `_ANALYSIS.md`, which carries the full reasoning for each item.

Current state: `mvn verify` passes with 116 tests, 0 failures; `javadoc:javadoc` builds with **zero** warnings;
67 of 68 Jakarta Validation 3.1 API members are covered; every concrete assertion has a navigation route, a
static entry point and an `InstanceOfAssertFactory`; the visibility census reports no deviation.

**Validator lifecycle, where things stand.** Three separate pieces, often conflated:

| | status |
|---|---|
| `usingValidatorFactory(ValidatorFactory)` — caller supplies and owns the factory | **done** |
| `usingValidatorFactorySuppliedBy(Supplier, ...)` — any disposing form | **refused** (§6) |
| the **default** validator, built from a factory that was closed before use | **done** (§1.1) |
| `usingValidatorSuppliedBy(Supplier<Validator>)` | **removed** — see §1.1 |

All settled.

---

## 1. Correctness

### 1.1 ~~The default validator comes from a closed factory~~ — §2.4 — **DONE**

Fixed by collapsing the validator plumbing rather than patching the default. `ValidationAssertDelegate` held a
`Supplier<? extends Validator>`, whose default built a factory, closed it, and returned the now-orphaned
validator — a use-after-close on every assertion, and a factory bootstrap per call.

A `ValidatorFactory` exposes no mutator, so there was nothing a supplier could usefully re-read. The field is
now a plain `Validator`, the default a single held factory and validator behind a lazy holder, never closed by
design. `usingValidatorSuppliedBy` is gone; `usingValidatorFactory(f)` is `usingValidator(f.getValidator())`.

Measured: **1.20 ms → 0.00010 ms** per `getValidator()`.

### 1.2 `DefaultPathAssert.newAbstractIterableAssert` fabricates a fake `Path` — §4.3

```java
return new DefaultPathAssert(() -> (Iterator<Path.Node>) iterable.iterator());
```

`Path` is `Iterable<Node>`, so a lambda compiles — but the result is not a real path and has no meaningful
`toString()`, which is exactly what a `Path` is read for. Any failure message after a `filteredOn` /
`extracting` on a path assertion degrades to a lambda's identity hash. Reachable, because the inherited
`AbstractIterableAssert` operations are public on `AbstractPathAssert`.

- [ ] Wrap in a real `Path` implementation with a spec-shaped `toString()`, or override the message.

---

## 2. API consistency

### 2.1 The consumer fires at a different point in each method — §3.2

| method | consumer runs | on failure the consumer… |
|---|---|---|
| `isValid(Consumer)` | before the assertion | sees the violations |
| `isValidFor(…, Consumer)` | before the assertion | sees the violations |
| `isNotValid(Consumer)` | after the assertion | never runs |
| `hasValidProperty(…, Consumer)` | after the assertion | never runs |

Whether a consumer is a callback that always observes or a post-success hook is a contract, and today it
depends on which method you called. Nothing in the javadoc mentions it. The new executable-validation methods
inherited the "after" shape, so they need the same decision applied.

- [ ] Pick one — "before" reads better for a test library, since the violation set matters most exactly when
      the assertion is about to fail — apply it everywhere, and document it.

---

## 3. Dead code

### 3.1 Four empty placeholder classes — §4.1

| file | state |
|---|---|
| `ValidationAssertUtils` | empty but for a throwing private constructor |
| `ValidationAssertConstants` | empty but for a throwing private constructor |
| `AssertFactories` | empty; body is two commented-out method sketches |
| `BeanConditions` | no caller in main or test |

- [ ] Finish them or delete them. As placeholders they cost a reader time on every pass.

### 3.2 `BeanConditions.valid` swallows the assertion error — §3.7

An empty `catch (AssertionError ae)` with a named-but-unused variable. Returning `false` is right for a
`Condition`, but the discarded error carries the only description of *why*, and nothing replaces it.

- [ ] Set a description from `ae.getMessage()`, or comment that the message is deliberately dropped. Moot if
      the class goes under 3.1.

---

## 4. Test coverage

The suite grew 64 → 105, and the metadata, executable-validation, node-navigation, message-template and
descriptor-member areas are now covered. What remains:

- [ ] **`ValidationAssertMessages` has no direct test** — the class that renders every failure message this
      library produces, and the reason §2.3 (literal `%n`) survived to be found by inspection. §5
- [ ] **Assert on failure-message text.** The library's whole value is its failure output, and nothing
      asserts a single character of it. §5
- [ ] **`DefaultPathAssert.newAbstractIterableAssert`** — untested, and item 1.2 above lives there.
- [ ] **`AbstractGroupConversionDescriptorAssert`** — no fixture declares a `@ConvertGroup`, so the assert is
      compiled but never exercised.
- [ ] **`AbstractContainerElementTypeDescriptorAssert`** — no fixture declares a container-element
      constraint (`List<@NotBlank String>`), so the same applies. This would also exercise
      `extractingContainerElementNode`.
- [ ] Constructor return-value validation is exercised through a custom `@Named` fixture constraint; a second
      case through a standard constraint would be worth having.

Note: a plain `grep` for type names across `src/test` lists far more types as "unnamed", but most are
exercised indirectly through the `assertThat*` entry points. The items above are the ones with no behavioural
coverage at all.

---

## 5. Enhancements

### 5.1 An assertion for `ConstraintViolationException` — §8.5

The last uncovered runtime type. Its `getConstraintViolations()` is the natural target when validation is
triggered by a framework rather than called directly from the test.

- [ ] `assertThatConstraintViolationException(e).hasViolationOn("name")`, navigating into the now-public
      set-of-violations assertion.

### 5.2 Convenience assertions on the violation set — §6.4

```java
assertThatBean(user).isNotValid().hasViolationOn("name");
assertThatBean(user).isNotValid().hasViolationWithMessageTemplate("{…NotBlank.message}");
assertThatBean(user).hasExactlyViolationsOn("name", "age");
```

Each is a few lines over the existing stored violations, and each removes a `Consumer` lambda from caller code.

- [ ] Add them.

### 5.3 Make the delegate's stored violations earn their place — §6.5

`ValidationAssertDelegate.violations` is written by every assertion and read only to build failure messages — a
scratch local promoted to state, which also makes an assert object non-reentrant.

- [ ] Either expose it (`SELF satisfiesViolations(Consumer)`, or chaining into the set assertion, which would
      give the field a reason to exist) or demote it back to a local.

### 5.4 Smaller items — §6.6

- [ ] **`acceptViolations` wraps too broadly**: any `Exception` from a caller's consumer is re-wrapped in a
      bare `RuntimeException`. `AssertionError` is an `Error` and passes through — the case that matters — but
      everything else loses its type at the boundary.
- [ ] **`getViolations()` allocates a new `HashSet` per call**, and `isValid` calls it three times per
      assertion.
- [ ] **A JPMS `module-info`.** The jar carries only `Automatic-Module-Name`. At release 17 a real descriptor
      is available, and both dependencies are already named modules.
- [ ] **`package-info.java` carries its license header after the package declaration** (line 100), the only
      file in the module that does. Legal, since it is just a comment.
- [ ] **README does not mention the metadata or executable-validation assertions**, nor the public
      set-of-violations entry point.

---

## 5b. Hierarchy

The module has six roots under assertj's `AbstractAssert` / `AbstractIterableAssert`; there is no single
generic parent, and that is correct — a descriptor, a path node and a bean have nothing in common beyond
being assertable. Only 7 of 68 types carry a `Validator`, and only three classes validate anything
(§8.10).

- [ ] `AbstractConstructorAssert` duplicates `AbstractBeanAssert`'s executable-validation logic (its private
      `validating(...)` mirrors `executable(...)`). Worth a shared base or a helper, once the validator
      lifecycle (§1.1) is settled, since both sites are where a factory would be acquired.

---

## 6. Deliberately not doing

Recorded so they are not re-litigated:

- **`usingValidatorFactorySuppliedBy(Supplier<? extends ValidatorFactory>)`, in any disposing form** — not
  added, and not to be added until there is a mechanism that cannot be used wrongly. Four things make the
  obvious designs error-prone:
  - **AssertJ has no post-assertion hook to hang it on.** `AbstractAssert` is not `AutoCloseable` and has no
    completion callback; `AfterAssertionErrorCollected` and `assertAll()` are soft-assertion only, and
    `setDescriptionConsumer` fires from `describedAs`, not from an assertion. The hook would have to be ours,
    inside all 8 validator-using methods, forcing `getValidator()` to become scoped.
  - **The scope would be one assertion, not one chain** — there is no end-of-chain event, so
    `assertThatBean(u).isValid().hasValidProperty("name")` would acquire and dispose twice. Anyone writing
    `usingValidatorFactorySuppliedBy(() -> sharedFactory)` would have their shared factory closed on the
    first assertion.
  - **Disposal would invalidate values already handed out.** The 8 methods return `SELF`, but the
    `ConstraintViolation` set escapes — retained in `delegate.violations`, passed to the `Consumer` overloads,
    and from there reachable to `ConstraintDescriptor`, `Path` and all nine node assertions. Closing the
    factory ends the life of its `Validator` by specification, and says nothing about those value objects.
  - **The failure mode is silent.** Hibernate Validator 9.1.4 does not enforce `close()` at all — a probe
    showed even `validate()` succeeds afterwards — so a misuse would pass locally and break only on a stricter
    provider.

  The bar for reconsidering: a design where the factory's lifetime is *syntactically visible* and cannot
  outlive the values derived from it. A scoped callback would clear it, because the lifetime is the block —
  something shaped like `assertThatBean(u).withValidatorFactory(supplier, a -> { a.isValid(); ... })`, closing
  once on exit. A fluent `usingValidatorFactorySuppliedBy(...)` cannot clear it, because nothing in the syntax
  marks where the factory stops being needed. §1.1 / §1.1b

- **`Validator.unwrap(Class)`** — a provider escape hatch for reaching implementation-specific types. Yields
  nothing a test would assert on. This is the 1 of 68 uncovered API members. §8.5
- **Bootstrap, configuration and SPI** — `Configuration`, `ValidatorFactory`, `ValidatorContext`,
  `Validation`, `spi.*`, `bootstrap.*`, `MessageInterpolator`, `TraversableResolver`, `ParameterNameProvider`,
  `ClockProvider`, `ConstraintValidatorFactory`. These configure validation rather than produce values to
  verify. §8.5
- **Constraint annotations and exceptions** — inputs and outcomes, not assertion targets, with
  `ConstraintViolationException` the one exception, tracked as 5.1 above.
- **Re-introducing per-concept assertion interfaces** — removed deliberately; see §7 for why, and for the two
  cases (node mixins, and nothing in the metadata family) where an interface is still the right answer.
