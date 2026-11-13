# assertj-validation against AssertJ's own conventions

2026-11-14. This is the combined result of two independent reviews and a two-round cross-check:
- Claude's review: `_ANALYSIS_CLAUDE.md`.
- Codex's review: `_ANALYSIS_CODEX.md`. Codex wrote it without seeing Claude's.

Each reviewer then checked the other's report item by item, running small programs where a claim could be run. This
document records the agreed outcome. Every actionable item is a GitHub issue labelled
[`assertj-alignment`](https://github.com/jinahya/assertj-validation/labels/assertj-alignment).

Reference: `assertj/assertj-core` `main` at `86e645780` (4.0.0-SNAPSHOT), plus the `assertj-build-3.27.7` and
`assertj-build-4.0.0-M1` tags. "Reproduced" means a program was run against the packaged jar
(`assertj-validation-0.6.2-SNAPSHOT`, Hibernate Validator 9.1.4) on **both** AssertJ 3.27.7 and 4.0.0-M1, unless
stated otherwise.

## Verdict

The design is AssertJ's. The hierarchy uses `Abstract*Assert<SELF, ACTUAL>` with thin concrete classes. There is
one entry-point class and an `InstanceOfAssertFactories` twin. Navigation goes through
`extracting(Function, AssertFactory)`, and configuration uses the `using…` vocabulary. Keep all of that.

What falls short is **integration with AssertJ's own machinery**, not cosmetics:
- soft assertions;
- the factory contract;
- representation;
- failure messages that name what they compared.

Behind those is a test suite that checks failures mostly by their type.

## Issues, ranked

| # | issue | severity | found by | evidence |
|---|---|---|---|---|
| [#23](https://github.com/jinahya/assertj-validation/issues/23) | Validation assertions are not collected by `SoftAssertions.proxy()` | **high** | Claude; Codex had it as an extensibility nit, then raised it to high | reproduced by both |
| [#24](https://github.com/jinahya/assertj-validation/issues/24) | AssertJ `main` (4.0.0) contract changes: `executeAssertion`, handler propagation, `toAssert(value)`, `ObjectAssertFactory`, Assumptions | medium now, **high** on adoption | both, merged | Codex compiled `main` in memory; Claude found the `ObjectAssertFactory` removal |
| [#25](https://github.com/jinahya/assertj-validation/issues/25) | Typed node factories: NPE on `null`; wrong-kind failure drops `as()` / `withFailMessage()` | medium | Codex | reproduced by both |
| [#26](https://github.com/jinahya/assertj-validation/issues/26) | `has*` failures do not name the attribute | medium | Claude | reproduced by both |
| [#27](https://github.com/jinahya/assertj-validation/issues/27) | Failures ignore `Representation`; context text copy-pasted | medium | both | Codex reproduced it with a custom representation |
| [#9](https://github.com/jinahya/assertj-validation/issues/9) | Consumer timing and mutability (existing issue, now labelled) | medium | both | code reading |
| [#28](https://github.com/jinahya/assertj-validation/issues/28) | Test contracts: six empty test classes, null/description cases, navigation contract test | medium | both | inventory |
| [#29](https://github.com/jinahya/assertj-validation/issues/29) | Project-owned `@CheckReturnValue` | low | Claude; Codex added the package split | jar listings |
| [#30](https://github.com/jinahya/assertj-validation/issues/30) | Copied Javadoc on `Default*Assert` classes | low | Codex | Claude confirmed |
| [#31](https://github.com/jinahya/assertj-validation/issues/31) | Navigation polish: return types, `…Satisfying` docs, `hostsConstraintTarget` alias, additive `extractingViolations()` | low | both | code reading |
| [#32](https://github.com/jinahya/assertj-validation/issues/32) | Test naming: `should_*` methods, uniform `*_Test` classes | low | Claude | code reading |
| [#33](https://github.com/jinahya/assertj-validation/issues/33) | 1.0: plain concrete names; node asserts out of `AbstractPathAssert` | low, optional | both | code reading |
| [#34](https://github.com/jinahya/assertj-validation/issues/34) | BDD `then*` and `WithValidationAssertions` facades | low, optional | both | — |

Suggested order:
1. #23, then #25, #26 and #27. #27 is also #19's shared helper.
2. #28, which needs #23 for its navigation contract test.
3. #24 alongside #20.
4. Everything else.

## Key findings

### Soft assertions (#23, #24)

```java
softly.proxy(DefaultBeanAssert.class, Object.class, invalidBean).isValid(); // throws at once, 0 collected
softly.proxy(DefaultBeanAssert.class, Object.class, invalidBean).isNull();  // AssertJ's own: collected
```

- **Released versions (3.27.x, 4.0.0-M1).** `SoftProxies` subclasses the assert with ByteBuddy, and 25 `public final`
  validation methods cannot be intercepted. AssertJ keeps proxied methods overridable; where `@SafeVarargs` forces
  `final`, it delegates to a non-final `…ForProxy` method.
- **Workaround.** `softly.check(() -> assertThatBean(b).isValid())` works.
- **AssertJ `main`.** ByteBuddy is gone (`17b51320d`). Bodies must run in `executeAssertion(...)` and navigation in
  `executeAssertionNavigation(...)`, with the handler carried by `withAssertionState`. Codex showed in memory that
  `AbstractPathAssert.inheritInfo` loses the handler, so node failures are thrown rather than collected.
- **Assumptions** fail on the released versions for a different reason (`Unsupported assumption creation`).

### Factory contract (#25)

- `METHOD_NODE.createAssert((Object) null)` throws `NullPointerException` from `requireKind`, where AssertJ's
  `STRING.createAssert(null)` returns an assertion over `null`.
- A wrong-kind narrowing throws a bare `AssertionError`, so `as("…")` and `withFailMessage("…")` vanish.
- Keep the kind check: Java subtypes cannot tell Hibernate Validator's node kinds apart.

### Messages (#26, #27)

`hasInvalidValue("x")` fails with only:

```
expected: "x"
 but was: ""
```

There is no `invalidValue` label. AssertJ's equivalent names the attribute (`ShouldHaveMessage`). Validation failures
are `String.format` text, so a user `Representation` is ignored and a bean prints as `Bean@45099dd3`.
`failure(...)` remains a supported way to fail. One shared, representation-aware helper is enough; one `ShouldXxx`
class per assertion is not required.

## Resolved in discussion

Claude withdrew or corrected:
- **"Soft assertions don't work."** Narrowed to `proxy()` and navigation; `softly.check(...)` works.
- **"AssertJ uses `final` only for `@SafeVarargs`."** Overstated: `AbstractBooleanAssert` has final comparator
  methods.
- **"Assumptions fail the same way."** They fail differently.
- **"`Assertions` is a final class."** It is not; `BDDAssertions` extends it.
- **"Core has no nested assert classes."** False: `FactoryBasedAssert`. The real problem is deep *public* nesting.
- **"AssertJ prefers interfaces to inheritance."** It uses both.
- **"Tests only use the public API from outside the package."** False: some share the production package.
- **"Only one method has description tests."** False: `User_DescriptionAndOverride_Test` spans beans, properties,
  executables and paths.
- **"Replace the `Consumer` overloads with navigation."** Navigation is now additive (#31); the overloads stay.
- **"One `ErrorMessageFactory` per failure."** Narrowed to shared, representation-aware diagnostics (#27).

Codex withdrew or corrected:
- **"`final` methods are an extensibility inconsistency."** Promoted to a high-priority interoperability defect (#23).
- **"Test methods are behaviour-named."** Withdrawn; many are `__()`.
- **Typed-factory null handling.** Downgraded from high to medium.
- **Navigation state.** Merged into #23 and #24.

Disagreement left open:
- **Consumer timing.** Codex wanted a new issue for it. Claude kept it on #9, which already covers both timing and
  mutability, and added the new evidence there as a comment. The substance is agreed: it is medium priority and
  `extractingViolations()` does not settle it.

## Keep as it is

Both reviewers agree on these:
- **Named entry points** (`assertThatBean`, `assertThatProperty`, …). Overloads could not tell a bean from a
  property value.
- **Abstract return types from entry points.** This is the same as `assertThat(String)`.
- **`SELF`/`ACTUAL` and the `(actual, selfType)` constructor.** Keep the public `(ACTUAL)` constructors too, which
  `proxy()` needs.
- **`ValidationInstanceOfAssertFactories`**, once #25 is fixed.
- **The `(extractor, AssertFactory)` navigation overloads.**
- **The configuration API:** `usingValidator`, `usingValidatorFactory` and `targetingGroups`. It is confined to
  assertions that validate, and is not carried into inspection asserts.
- **`AbstractBeanAssert extends AbstractPropertyAssert`.** A bean can be validated as a property value.
- **`isNotNull()` guards.** Bean, violation, descriptor and path asserts use them. Property asserts deliberately
  accept `null`.
- **`isEmpty()` / `isNullOrEmpty()` returning `void`,** as `EnumerableAssert` does.
- **The provider-backed example scenarios in the tests.**
