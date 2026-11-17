# assertj-validation against AssertJ's own style — Codex's analysis

2026-11-14. Codex's report as it returned it, unedited. Codex did not see Claude's analysis
(`_ANALYSIS_CLAUDE.md`). It is static analysis only: Codex ran no builds and changed no files. Reference:
`assertj/assertj-core` at `86e645780`.

Project paths are under `src/main/java/com/github/jinahya/assertj/validation/` unless noted. AssertJ paths are
under `assertj-core/src/main/java/org/assertj/core/api/`.

## Summary

The project follows AssertJ's core design well, but has several behavioral integration gaps worth fixing before cosmetic alignment. Its strengths are self-typed abstract assertions, small concrete implementations, typed navigation, reuse of existing assertions, and provider-backed examples.

The highest-priority findings are:

1. Typed node factories mishandle `null` and bypass configured failure context when node kinds differ.
2. Custom navigation does not consistently preserve AssertJ assertion state.
3. The freshly fetched AssertJ snapshot requires an iterable hook this project does not implement.
4. Custom validation diagnostics bypass AssertJ's representation system.
5. Tests demonstrate useful scenarios but provide uneven method-level contract coverage.

Codex inspected the 42 production Java files, the test-source inventory and representative implementations, the docs, and the POM.

**Version boundary:** the project declares AssertJ 3.27.7, with a 4.0.0-M1 profile (pom.xml:90, pom.xml:349). The reference checkout (commit 86e645780) is 4.0.0-SNAPSHOT. The snapshot findings below do not establish failures against either declared dependency.

## Entry points

- **Return types match AssertJ.** `ValidationAssertions.assertThatBean` (ValidationAssertions.java:55) returns `AbstractBeanAssert<?, ACTUAL>` but constructs the concrete class. `Assertions.assertThat(String)` (Assertions.java:3283) does the same with `AbstractStringAssert<?>` and `StringAssert`. This is aligned, not a defect.
- **Named factories are a justified divergence.** The project uses `assertThatBean`, `assertThatProperty` and `assertThatPath` instead of overloaded `assertThat`. A bean and a property value are both plain objects, so overloads could not tell them apart. Keep these names. For violations and descriptors, overloads would look more like core (Assertions.java:3273) but would only add convenience.
- **No BDD entry points.** There is no `thenBean` or `thenProperty`. Core has `BDDAssertions.then(...)` (BDDAssertions.java:1017). A BDD facade would be an optional addition, not a correctness issue.
- **`InstanceOfAssertFactory` integration is good, with two real problems.** `ValidationInstanceOfAssertFactories.java:66` supplies factories for paths, nodes, descriptors and exceptions, plus generic factories for violations and constraint descriptors.
  - **Typed node factories reject null by accident.** Their delegates (ValidationInstanceOfAssertFactories.java:111) call `requireKind` (AbstractPathAssert.java:1164), which dereferences `node.getKind()`. `METHOD_NODE.createAssert((Object) null)` therefore throws an NPE instead of building an assertion over null. Core tests that null narrowing is allowed (`AbstractAssert_asInstanceOf_with_InstanceOfAssertFactory_Test.java:82`).
  - **Wrong-kind failures lose assertion context.** `requireKindOf` (AbstractPathAssert.java:1146) throws a plain `AssertionError`. Core's `asInstanceOf` (AbstractAssert.java:641) applies the originating assertion's state only after the factory returns, so descriptions and overriding messages are bypassed.
  - **Keep the kind checks themselves.** `PathAssert_NodeKind_Test.java:58` shows that Java subtype checks cannot distinguish Hibernate Validator node kinds.
  - The generic factories use unchecked class casts, so the type argument states caller intent without validating it. Core offers both generic methods and constants such as `OPTIONAL`, and takes explicit type tokens (InstanceOfAssertFactories.java:177).

## Main class structure

- **The abstract/concrete hierarchy is sound.** It uses `SELF extends AbstractBeanAssert<SELF, ACTUAL>`. Abstract constructors take `actual` and `Class<?> selfType`, and concrete constructors supply the class token. This mirrors core. See AbstractBeanAssert.java:49 and DefaultBeanAssert.java:31 against AbstractStringAssert.java:42 and StringAssert.java:25.
- **Naming.** The `Default*Assert` prefix differs from core's `StringAssert`/`AbstractStringAssert`. It is consistent internally and low priority. Renaming public classes would break compatibility for no behavioral gain.
- **Domain inheritance is mostly appropriate.**
  - Validator and group configuration is limited to assertions that actually validate (AbstractValidationAssert.java:39).
  - Exception assertions derive from `AbstractThrowableAssert` (AbstractConstraintViolationExceptionAssert.java:45).
  - Violation collections derive from `AbstractIterableAssert` (AbstractIterableOfConstraintViolationsAssert.java:37). Core supports this explicitly (AbstractIterableAssert.java:3234).
  - `AbstractBeanAssert extends AbstractPropertyAssert` is justified, because a bean can also be checked as another bean's property value.
- **Extensibility is inconsistent.** Validation and configuration methods are broadly `final`, while most descriptor and navigation methods are overridable. Core methods such as `contains` are overridable (AbstractOptionalAssert.java:146). Final methods can be justified to protect validation semantics, but the project should say which behavior subclasses may customize. See AbstractBeanAssert.java:76 and AbstractValidationAssert.java:103.
- **The path structure is disproportionately complex.**
  - `AbstractPathAssert` is 1,817 lines. It holds node assertion classes, capability interfaces, navigation, formatting and size operations (AbstractPathAssert.java:84).
  - There is a package-private `_AbstractNodeAssert` (AbstractPathAssert.java:427) and public types nested two levels deep in `DefaultPathAssert.DefaultNodeAssert` (DefaultPathAssert.java:36).
  - The self-bounded capability interfaces are legitimate, since core has `NumberAssert` (NumberAssert.java:34). The concern is discoverability and maintenance.
  - Splitting public node types would be a compatibility-sensitive cleanup, not urgent.

## Assertion method style

- **Naming and chaining largely match.**
  - `isValid`, `hasMessageTemplate` (AbstractConstraintViolationAssert.java:402), `hasPropertyName` and `doesNotHaveConstraints` read naturally.
  - Predicate assertions return `SELF`. Navigation returns typed assertions by delegating to extracted string, boolean, class and collection assertions (AbstractElementDescriptorAssert.java:106), as core does (AbstractObjectAssert.java:659).
  - Path `isEmpty()` and `isNullOrEmpty()` return `void`. This matches `EnumerableAssert` (EnumerableAssert.java:50) and is not an oversight.
- **Vocabulary.**
  - `hostsConstraintTarget` (AbstractConstraintDescriptorAssert.java:273) would be clearer as something like `hasValidationAppliesTo`.
  - `hasNameSatisfying` (AbstractPathAssert.java:697) passes an assertion object to the consumer. Core's `hasValueSatisfying` passes the value (AbstractOptionalAssert.java:158). Document the difference.
- **Some navigation return types are weak.** `extractingInvalidValue()` (AbstractConstraintViolationAssert.java:253) and `extractingExecutableReturnValue()` return `Assert<?, Object>`. Core returns `AbstractObjectAssert<?, T>` (AbstractObjectAssert.java:622), which gives more navigation operations.
- **Navigation state has gaps.**
  - Most extractions go through inherited `extracting`, which transfers AssertJ state.
  - Path navigation uses `inheritInfo` (AbstractPathAssert.java:1642). It copies only the description, the representation and a lazy overriding-message supplier. Core also transfers the comparison machinery, the assertion-error handler and skipped-chain state (AbstractAssert.java:1284, 1408).
  - The lazy message handling is intentional and tested (`User_DescriptionAndOverride_Test.java:157`), but it is not equivalent to core navigation.
  - The iterable's `toAssert(value, description)` (DefaultIterableOfConstraintViolationsAssert.java:43) copies only a description. In this snapshot, core centralizes state propagation in that two-argument method and requires subclasses to implement `toAssert(value)` (AbstractIterableAssert.java:3246). The project does not implement that hook. This is a source-compatibility gap against the snapshot only.
- **Failure handling is partly aligned.**
  - Validation failures use `failure`, which preserves descriptions and overrides (AbstractValidationAssert.java:215).
  - Path size assertions reuse core `ShouldHaveSize` factories (AbstractPathAssert.java:1676).
  - Using `failure` or `failWithMessage` is supported AssertJ practice (AbstractAssert.java:257). Not every assertion needs a `ShouldXxx` class.
  - The real weakness is formatting. Validation contexts and violation details use `String.format`, plain object conversion and custom angle brackets (AbstractBeanAssert.java:82, ValidationAssertMessages.java:33). Configured representations therefore do not apply. Core routes arguments through `Representation.toStringOf` (error/MessageFormatter.java:48).
  - The current reference uses `AssertionErrorCreator` (error/AssertionErrorCreator.java:318), so do not describe core failures as all going through `Failures`.
- **Null handling is mostly correct.**
  - Bean, descriptor, violation and path assertions guard dereferences with `isNotNull`, matching core's custom-assertion example (AbstractAssert.java:267). See AbstractBeanAssert.java:76.
  - Property-value assertions deliberately let null through to `validateValue` (AbstractPropertyAssert.java:69). That is correct, because null may be the value being tested.
- **Consumer contracts are inconsistent.**
  - `isValid(consumer)` and `isValidFor(consumer)` pass an unmodifiable set to the consumer before checking validity (AbstractBeanAssert.java:76).
  - Other overloads check validity first and then pass the original set (AbstractBeanAssert.java:162, 196).
  - This changes when callbacks run, whether they can mutate the set, and which failure the user sees first. Document and test the differences, or normalize them. Core's `hasValueSatisfying` checks presence before calling its consumer (AbstractOptionalAssert.java:186).
- **Javadoc is useful but uneven.**
  - Examples and validation caveats are valuable.
  - There are copy/paste errors. `DefaultPathAssert` (DefaultPathAssert.java:25) describes property-value validation. Concrete node classes call themselves abstract and reference nonexistent value types (DefaultConstraintDescriptorAssert.java:27). Fix these first.
  - Core is moving to Markdown Javadoc. The project targets Java 17, so treat that as a toolchain-dependent choice.

## Tests

Test paths are under `src/test/java/com/github/jinahya/assertj/validation/`.

- **What works well.**
  - Test classes and methods are package-private and behavior-named, as in core (`IterableAssert_first_Test.java:36`).
  - Provider-backed scenarios cover group targeting, executable validation, metadata, typed filtering, node kinds and description/message overrides.
  - `example/user/ConstraintViolations_Test.java:72` checks that filtering keeps the specialized violation assertion type. That is stronger than checking only the runtime class.
- **Structure and coverage are uneven.**
  - Six violation test classes are empty: constraint descriptor, executable parameters, executable return value, invalid value, leaf bean and property path. See `ConstraintViolationAssert_InvalidValue_Test.java:23` and `ConstraintViolationAssert_ExecutableParameters_Test.java:23`. Other examples exercise some of these behaviors, so this does not mean they are untested. It does mean the per-method structure is unfinished.
  - `PathAssert_Size_Test` and `BeanDescriptorAssert_Test` group many API methods in one class. Core isolates one public method per class and tests null, success and mismatch (`OptionalAssert_containsInstanceOf_Test.java:29`, in `assertj-tests/assertj-integration-tests/assertj-core-tests`).
  - The project's base helper (`ConstraintViolationAssert_$Test.java:25`) only builds an assertion. Core's `BaseTestTemplate` (assertj-core/src/test/.../api/BaseTestTemplate.java:34) also verifies delegation and that the same instance is returned. Adopt the return-identity check without copying core's mocking architecture.
- **Failure verification is imprecise.**
  - The suite uses neither `expectAssertionError` nor `BDDAssertions`. Changing imports alone would not improve coverage.
  - Some tests check only `AssertionError` or a fragment such as "3" (`example/service/PathAssert_Size_Test.java:73`, `ValidationAssertionsTestUtils.java:98`). These can pass with the wrong diagnostic. Core compares full deterministic messages (`ShouldHaveSize_create_Test.java:51`).
  - Fragment checks remain justified for provider-localized messages and unordered violation sets. Test deterministic formatting separately with controlled violations.
  - Navigation needs a reusable contract test covering descriptions, overrides and representation. Core has one (`NavigationMethodBaseTest.java:40`).

## Recommendations (ranked)

| Rank | Recommendation | Location and rationale |
|---|---|---|
| 1 (High) | Repair typed node factory contracts. Allow null assertion construction. Make wrong-kind failures keep descriptions and overrides. Keep the provider-aware kind checks. | ValidationInstanceOfAssertFactories.java:111, AbstractPathAssert.java:1146. Add null, wrong-kind, description and override tests. |
| 2 (High) | Preserve navigation state consistently. Prefer inherited `extracting` where possible. Define any lazy-message deviation explicitly. | AbstractPathAssert.java:1642, DefaultIterableOfConstraintViolationsAssert.java:43. Core transfers more than presentation info. |
| 3 (High for snapshot adoption) | Resolve the new iterable `toAssert(value)` hook before claiming compatibility with current AssertJ source. Keep released-baseline compatibility separate. | DefaultIterableOfConstraintViolationsAssert.java:43, core AbstractIterableAssert.java:3256. The existing 4.0.0-M1 profile does not cover this snapshot. |
| 4 (Medium-high) | Make shared diagnostics representation-aware. Add focused error factories for validation failures and node-navigation errors. Keep description and override support. | ValidationAssertMessages.java:33, AbstractPathAssert.java:1107. |
| 5 (Medium-high) | Complete behavioral test contracts. Prioritize nulls, mismatches, return identity, extraction types and navigation state over naming-only changes. | The empty `ConstraintViolationAssert_InvalidValue_Test.java:23` and the broad `PathAssert_Size_Test.java:73`. Keep the provider-backed examples. |
| 6 (Medium) | Specify consumer timing and mutability consistently. Document deliberate diagnostic callbacks before failure. | AbstractBeanAssert.java:76, 162. |
| 7 (Medium-low) | Improve API consistency without forced renaming. Strengthen object extraction return types. Clarify the assertion-consuming `Satisfying` methods and subclass extension points. Consider BDD aliases if users want them. | AbstractConstraintViolationAssert.java:253, AbstractPathAssert.java:697. |
| 8 (Low) | Fix incorrect Javadoc and reduce path-class complexity when compatibility allows. | DefaultPathAssert.java:25, AbstractPathAssert.java:427. Keep the SELF/ACTUAL pattern and the named entry points. |
