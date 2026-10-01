# assertj-validation — TODO

Open work as of 2026-10-01, after the build realignment, the interface-layer collapse and the API-coverage
completion. Section references point at `_ANALYSIS.md`, which carries the full reasoning for each item.

Current state: `mvn verify` passes with 118 tests, 0 failures; `javadoc:javadoc` builds with **zero** warnings;
67 of 68 Jakarta Validation 3.1 API members are covered; every concrete assertion has a navigation route, a
static entry point and an `InstanceOfAssertFactory`; the visibility census reports no deviation.

**Validator lifecycle, where things stand.** Three separate pieces, often conflated:

Settled, after several passes. The validator surface is now two methods, and the module contains exactly one
`ValidatorFactory`, inside `ValidationAssertDelegate.applyValidator`:

| | outcome |
|---|---|
| `usingValidator(Validator)` | **kept** — the general currency; caller owns it |
| `usingValidatorFactory(ValidatorFactory)` | **kept** (§8.13) — caller owns it, never closed here |
| default, when neither supplied | one factory built, used and **closed** inside the single assertion |
| `usingValidatorSuppliedBy(Supplier<Validator>)` | **removed** (§8.9) |
| `usingValidatorFactorySuppliedBy(Supplier, ...)` | **refused** (§6) |

No static state, no unowned resource, nothing whose ownership needs documenting.

---

## 1. Correctness

### 1.1 ~~The default validator comes from a closed factory~~ — §2.4 — **DONE**

Fixed by collapsing the validator plumbing rather than patching the default. `ValidationAssertDelegate` held a
`Supplier<? extends Validator>`, whose default built a factory, closed it, and returned the now-orphaned
validator — a use-after-close on every assertion, and a factory bootstrap per call.

A `ValidatorFactory` exposes no mutator, so there was nothing a supplier could usefully re-read. The field is
now a plain `Validator`, the default a single held factory and validator behind a lazy holder, never closed by
design. `usingValidatorSuppliedBy` is gone; `usingValidatorFactory(f)` is `usingValidator(f.getValidator())`.

Then reworked again (§8.11): holding a factory forever fixed the spec violation but left process-scoped
state with no owner. The delegate now exposes `applyValidator(Function)` — a configured validator is applied
as-is, otherwise a factory is built, used and closed inside the one call. `getValidator()` is deleted so no
validator can escape. The delegate holds no static state; the default path costs ~1.20 ms per assertion,
accepted as the price of having no unowned resource.

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

### 3.1 ~~Four empty placeholder classes~~ — §4.1 — **DONE**

All four deleted, after checking what assertj's own conventions say each would have held. Three turned out
to have no convention behind them at all:

| | what assertj does | verdict |
|---|---|---|
| `ValidationAssertConstants` | assertj ships **no** `*Constants` class anywhere; constants live where they are used | nothing to infer — deleted |
| `ValidationAssertUtils` | utilities live in `org.assertj.core.util` as classes named for a concern — `Strings`, `Lists`, `Preconditions`, `Closeables` — never a `*Utils` grab-bag | deleted; a future utility earns its own named class |
| `AssertFactories` | the role is `InstanceOfAssertFactories`, which this module already has as `ValidationInstanceOfAssertFactories` | duplicate — deleted. Its two commented-out sketches were node assert factories, and **all 9 already ship** in `ValidationInstanceOfAssertFactories` |
| `BeanConditions` | assertj ships `Condition` and its combinators but **no ready-made `Condition` constants** — users build their own | deleted; see 5.5 for the convention-correct version if it is ever wanted |

Also removed in the same pass: two imports left unused by the delegate inlining, and the last commented-out
method sketch (`extractingAs` in `AbstractPathAssert`). The module now has **no unreferenced type, no unused
import and no commented-out code**.

## 4. Test coverage

The suite grew 64 → 105, and the metadata, executable-validation, node-navigation, message-template and
descriptor-member areas are now covered. What remains:

- [x] ~~**`ValidationAssertMessages` has no direct test**~~ — **done**. Eight tests cover both `format`
      overloads, the null/empty guards and non-instantiability, and two of them pin the §2.3 regression
      directly: no literal `%n` survives, and entries are joined on `System.lineSeparator()`.
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

### 5.3 ~~Make the delegate's stored violations earn their place~~ — §6.5 — **DONE**

Resolved by deletion (§8.14). Every write was read only inside the method that wrote it, so it was a local
promoted to a field. It is a local again; the field, its three accessors and the `unchecked` cast are gone,
and assertion objects are reentrant.

### 5.5 A bean `Condition`, done the assertj way

`BeanConditions` was deleted as dead (3.1), but the idea behind it is sound and currently unexpressible:

```java
assertThat(users).are(valid);     // every bean in a collection
assertThat(user).is(valid);
```

The deleted version swallowed the `AssertionError` and so reported nothing about *why* (§3.7). assertj's
answer to that is `VerboseCondition`:

```java
VerboseCondition.verboseCondition(predicate, description, objectUnderTestDescriptor)
```

which keeps the failure text. Worth having only if the collection form is wanted — a plain
`is(valid)` adds nothing over `assertThatBean(user).isValid()`.

- [ ] Decide whether `are(valid)` over a collection is worth a `VerboseCondition`-based bean condition.

### 5.4 Smaller items — §6.6

- [x] ~~**`acceptViolations` wraps too broadly**~~ — **done**. The `catch` is removed, not narrowed:
      `Consumer.accept` declares no checked exception, so it could only ever re-wrap an unchecked one and
      lose its type, and the throwable that matters — an `AssertionError` from an assertion inside the
      consumer — is an `Error` and was never caught anyway.
- [x] ~~**`getViolations()` allocates a new `HashSet` per call**~~ — **done**, by deletion. The method went
      with the `violations` field (§8.14); each assertion now holds one precisely-typed local.
- [ ] **A JPMS `module-info`.** The jar carries only `Automatic-Module-Name`. At release 17 a real descriptor
      is available, and both dependencies are already named modules.
- [x] ~~**`package-info.java` carries its license header after the package declaration**~~ — **not a
      defect**; the item was based on a misreading. Every file in the module puts `package` first and the
      license block after it, and `package-info` does the same (package at 100, license at 101). What
      precedes its package declaration is the package javadoc, which Java requires to go there.
- [x] ~~**README does not mention the metadata or executable-validation assertions**~~ — **done**, in
      `package-info`, which is where the README's "Usages" section points. It now groups the 26 entry points
      into running validation, inspecting what validation produced, and inspecting metadata.

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
