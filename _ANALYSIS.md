# assertj-validation — analysis

An AssertJ extension for Jakarta Validation: ~5.6k lines of main source in one package,
`com.github.jinahya.assertj.validation`, plus 48 test sources (34 of them `*Test`).

This document records the state of the module as of 2026-10-01, the three changes applied in this pass — the
build realignment, the interface-layer collapse and the API-coverage completion — and the problems and
enhancements left open. Everything below was read against the working tree, and every defect marked
**confirmed** was reproduced.

The still-open items are collected as a checklist in [_TODO.md](_TODO.md); this document keeps the reasoning
behind each.

---

## 1. Build realignment (applied)

The build was realigned, and the module went Jakarta-only in the process.

### 1.1 Why it had to move

The build did not work. On the installed JDK 25:

```
[ERROR] Failed to execute goal ...:compile (default-compile) on project assertj-bean-validation:
        Fatal error compiling: java.lang.ExceptionInInitializerError:
        com.sun.tools.javac.code.TypeTag :: UNKNOWN
```

Two causes, both structural:

- Lombok 1.18.32 sat on the **main** compilation's `annotationProcessorPaths`, although no class under
  `src/main` uses Lombok. That version does not understand JDK 25's `javac` internals, so it brought down a
  compilation it had no business being part of.
- `maven.compiler.release` was `8`, against a test release of `17`, with a hand-rolled matrix of seven
  classifier jars (`jakarta`, `release-11`, `release-11-jakarta`, `release-17`, `release-17-jakarta`,
  `release-21`, `release-21-jakarta`) produced by `maven-compiler-plugin` executions feeding
  `eclipse-transformer`. The javax/jakarta axis and the Java-release axis were multiplied out by hand.

### 1.2 What changed

| | before | after |
|---|---|---|
| parent | `com.github.jinahya:jinahya-parent:0.9.1` | `io.github.jinahya:jinahya-parent:1.0.9` |
| coordinates | `com.github.jinahya:assertj-bean-validation` | `io.github.jinahya:assertj-validation` |
| validation API | `javax.validation:validation-api:2.0.1` | `jakarta.validation-api`, from `jakarta.jakartaee-bom` |
| main / test release | 8 / 17 | 17 / 25 |
| jars published | 8 (1 plain + 7 classifiers) | 1 |
| generation axis | `attach-jakarta-transformed` + transformer | `jakarta-ee-11` (default) / `jakarta-ee-10` profiles |
| CI | single JDK 18 job | matrix over both generations on JDK 25 |
| pom size | 922 lines | 300 lines |

Concretely:

- **All `javax.validation` references rewritten to `jakarta.validation`** across main and test (87 references).
  `eclipse-transformer`, `animal-sniffer`, and every classifier execution are gone; the module now compiles
  directly against the API it documents.
- **`jakarta.platform:jakarta.jakartaee-bom` imported.** A profile chooses a *generation*, and the three
  versions that move with it — the platform BOM (which pins `jakarta.validation-api`), Hibernate Validator, and
  Expressly. `jakarta-ee-11` holds the defaults and so overrides nothing; `jakarta-ee-10` overrides all three.
- **`jakarta.validation-api` and `assertj-core` are `provided`.** Both are APIs this module sits between, and a
  consumer brings its own. Hibernate Validator and Expressly are `test` only — nothing under `src/main`
  references an implementation, which is the point of extending the API rather than the engine.
- **`assertj-bom`, `junit-bom`, `mockito-bom`, `slf4j-bom` imported**, with the sibling's `byte-buddy`
  exclusion on `assertj-core` (an imported BOM carries no exclusions, so without it declaration order would
  hand the resolution to AssertJ's older pin).
- **Lombok moved to the `default-testCompile` execution's processor path only.** The main compilation now runs
  with no annotation processor at all, which is what it wants.
- `.mvn/jvm.config` added (the `--add-exports`/`--add-opens` set Lombok needs on a modern JDK), Maven wrapper
  moved to 3.9.11 `only-script`, `.java-version` set to 25, `.gitignore` taken from the sibling.
- `README.md` rewritten: new coordinates, the generation table, and an explicit note that the `javax` flavors
  are gone and stay published under the old artifactId.

### 1.3 Verification

| profile | `jakarta.validation-api` | Hibernate Validator | Expressly | result |
|---|---|---|---|---|
| `jakarta-ee-11` | 3.1.1 | 9.1.3.Final | 6.0.0 | `package` ✅ 64/64 tests |
| `jakarta-ee-10` | 3.0.2 | 8.0.3.Final | 5.0.0 | `package` ✅ 64/64 tests |

`mvn javadoc:javadoc` also succeeds — after the fix in §2.1.

### 1.4 The javax-era reference-guide build, removed

Everything the deleted `sub-hibernate-validator-referenceguide` profile pulled in went with it:

- `sub/bval`, `sub/hibernate-validator` — git submodules, both uninitialised, referenced only by that profile.
  Also dropped from `.gitmodules`; only `.idea/codeStyles` remains.
- `src/test/java-sub-hibernate-validator-referenceguide/` — four test classes extending Hibernate Validator 6
  (javax-era) reference-guide classes that lived in the submodule. They could not survive the Jakarta move.
  Git keeps them if they are ever worth porting to the Hibernate Validator 9 reference guide.
- `.mvn_hiberate_validator_referenceguide.sh` — driver for the profile (and a typo in its own name).

An empty `sub/` directory is left on disk; git does not track directories, so it is untracked clutter rather
than part of the tree.

### 1.5 Dependency and plugin updates

Driven by `versions-maven-plugin` (2.21.0, from the parent), run per profile.

| | from | to | |
|---|---|---|---|
| `ch.qos.logback` | 1.6.3 | 1.6.5 | |
| `org.mockito` | 5.23.0 | 5.24.0 | |
| `org.slf4j` | 2.0.19 | 2.0.20 | surfaced only after the pre-release filter below |
| `org.junit` | 5.14.4 | 6.1.3 | major; see note |
| `hibernate-validator` (ee-11) | 9.1.3.Final | 9.1.4.Final | within generation 9 |
| `hibernate-validator` (ee-10) | 8.0.3.Final | 8.0.5.Final | **never offered by the plugin** |

Held back: `maven-compiler-plugin` 4.0.0-beta-5 and `slf4j` 2.1.0-alpha1 (pre-release), `jakartaee-bom` 11.0.0
and `assertj` 4.0.0-M1 (already current — 4.0.0-M1 is deliberately a milestone and has to be bumped by hand).
`versions:display-plugin-updates` offered nothing else: every remaining proposal requires Maven 4.

#### The Jakarta axis is where a bulk update goes wrong

Run against `jakarta-ee-10`, the plugin proposed:

```
${version.jakarta.jakartaee-bom} ................... 10.0.0 -> 11.0.0
${version.org.glassfish.expressly} ................... 5.0.0 -> 6.0.0
${version.org.hibernate.validator} ....... 8.0.3.Final -> 9.1.4.Final
```

Taken, that would not have updated the profile — it would have collapsed it into a copy of `jakarta-ee-11`,
and the two-generation matrix would have gone on reporting green while testing one generation twice. The
plugin reads each property independently and cannot know the three move together.

Meanwhile it never offered the update that generation *did* need: **8.0.3 → 8.0.5**, two patch releases, hidden
behind the 9.x proposal.

Two guards now, in the pom:

1. **Per-profile major ranges** on the three axis properties — `[11,12)`/`[9,10)`/`[6,7)` under `jakarta-ee-11`,
   `[10,11)`/`[8,9)`/`[5,6)` under `jakarta-ee-10`.
2. **A pre-release filter** (`ignoredVersions`) on the plugin. Ranges alone are not enough: Maven orders a
   qualifier *below* the release it precedes, so `11.0.0-RC1` still satisfies `[10,11)`. Without the filter the
   guarded ee-10 build still proposed `jakartaee-bom → 11.0.0-RC1`, `expressly → 6.0.0-M1` and
   `hibernate-validator → 9.0.0.CR1`.

With both, `versions:display-property-updates` reports the axis as current under each profile, and moving a
generation is a deliberate edit of four lines rather than something a bulk update does.

#### JUnit 6 — tried, held at 5.14.4

`junit-bom` 6.1.3 was taken and then reverted. It is not that it failed — 64/64 passed on both profiles and
the enforcer's `dependencyConvergence` rule was satisfied. It is what made it pass:

`mockito-junit-jupiter` 5.24.0 still declares `junit-jupiter-api` **5.13.4**. Under `junit-bom` 6.x the BOM
pulls that up to 6.1.3, so Mockito's JUnit integration does not get *updated* — it gets *overridden*, and runs
against an API two majors past the one it was built and tested on. It held here only because these tests reach
Mockito through `Mockito.spy`/`when` in three files and never touch `MockitoExtension`, which is precisely the
surface that would break first. A green run that depends on not exercising the risky path is not evidence the
pairing is sound.

So: **wait for Mockito.** The property is pinned at `5.14.4`, which also keeps it level with the sibling
`jinahya-object-randomizer`. The pre-release filter deliberately does *not* suppress the 6.x proposal — the
plugin should go on offering it. Take it once `mockito-junit-jupiter` declares `junit-jupiter-api` 6.

### 1.6 Ordering

`<dependencyManagement>` (5 entries), `<dependencies>` (9) and `<build><plugins>` (5) are each sorted by
groupId, then artifactId — verified mechanically, ignoring nested `<exclusions>` and
`<annotationProcessorPaths>`, which carry groupId/artifactId pairs of their own and are not siblings.

Sorting plugins is only safe where declaration order carries no meaning. Maven falls back to POM order for
executions bound to the **same phase**, and no two plugins here share one: compiler at compile/test-compile,
surefire at test, jar at package, javadoc and versions with no execution at all. The relative order of those
phases comes from the lifecycle, not the POM. Adding an execution that collides with a phase already in use
would make the order significant — pin that pair's position then, and say why.

---

## 2. Correctness defects

### 2.1 `package-info` snippet link regions overlap — **confirmed, fixed**

`mvn javadoc:javadoc` failed outright:

```
package-info.java:35: error: snippet link tags:
    PropertyAssert#isValidFor(Class, String) and BeanAssert#isValid() overlap
```

`// @link region substring="isValid"` also matches inside `isValidFor`, so the two regions overlapped in both
snippets. Narrowed to `substring=".isValid()"`; javadoc now builds. This blocked any release profile that runs
the javadoc plugin.

### 2.2 `AbstractPathAssert.nodeAt` is off by one — **confirmed, fixed**

`src/main/java/com/github/jinahya/assertj/validation/AbstractPathAssert.java:273-277`

```java
Path.Node node = iterator.next();            // consumes element 0
for (int i = 1; i < index; i++) {            // runs index-1 times, so index-1 total
    node = iterator.next();
}
```

The initial `next()` already consumes element 0, and the loop then advances only `index - 1` more times.

| call | returns | expected |
|---|---|---|
| `nodeAt(…, 0)` | element 0 | element 0 ✅ |
| `nodeAt(…, 1)` | element 0 | element 1 ❌ |
| `nodeAt(…, 2)` | element 1 | element 2 ❌ |
| `nodeAt(…, 3)` | element 2 | element 3 ❌ |

Every index above 0 is wrong, which means `extractingNode(int)`, `extractingBeanNode(int)` and
`extractingPropertyNode(int)` all read the wrong node — and silently, since the node one position earlier is
usually a perfectly valid `Path.Node` that simply belongs to a different path segment. On a path of length *n*,
asking for the last node returns the second-to-last and never throws.

Fixed: the loop now starts at `0`, so it advances `index` times past the element the initial `next()`
already consumed. Still untested — see §5.

This is the single most consequential bug in the module, and §5 explains why nothing caught it.

### 2.3 Multi-violation failure messages join on a literal `%n` — **confirmed, fixed**

`ValidationAssertMessages.java:53`

```java
.collect(Collectors.joining("%n"));
```

`Collectors.joining` takes a literal delimiter — it does no format processing. `%n` is a `String.format`
directive, and nothing formats the joined result afterwards. So a failure reporting two or more violations
renders as one run-on line:

```
-> 	message        : must not be blank
	propertyPath   : name
	...%n-> 	message        : must be greater than or equal to 0
	propertyPath   : age
```

Fixed: now `Collectors.joining(System.lineSeparator())`. Note that the per-violation `format(ConstraintViolation)`
above it is correct — it goes through `String.format`, so its `%n`s do resolve. Only the join is broken, which
is why single-violation messages look fine and the defect only shows on beans with more than one violation.

### 2.4 The default validator comes from a closed factory — open

`ValidationAssertDelegate.java:38-42`

```java
try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
    return factory.getValidator();
}
```

The factory is closed before the `Validator` it produced is ever used. The Jakarta Validation specification
says nothing about a `Validator` outliving its factory, and `ValidatorFactory.close()` is defined as releasing
the resources the factory allocated. It works with Hibernate Validator today — the 64 tests prove that much —
but it is unspecified behaviour, and an engine that pooled or cached anything factory-scoped would be within
its rights to fail or leak.

A second, quieter cost: the supplier is invoked per `getValidator()` call, so every assertion bootstraps a
fresh `ValidatorFactory`. Factory construction scans the classpath and parses `META-INF/validation.xml`; it is
the expensive part of the API, and doing it per assertion is the wrong shape for a test library.

Fix: build the factory once, hold it, and let the `Validator` it produced live as long as it does.

---

## 3. API design problems

### 3.1 Dead branch left in `isNotValid()` — fixed

`AbstractBeanAssert.java:89-112`

```java
public final SELF isNotValid() {
    if (true) {
        return isNotValid(s -> { });
    }
    isNotNull();                      // ← 20 unreachable lines follow
    ...
}
```

An `if (true)` guard fronting an abandoned implementation. The duplicate body below it is the thing
`isNotValid(Consumer)` already does. Delete lines 92-111 and the guard.

### 3.2 The consumer fires at a different point in each method — open

Three methods take a `Consumer<Set<ConstraintViolation<…>>>` and disagree about when it runs:

| method | consumer runs | so on failure the consumer… |
|---|---|---|
| `isValid(Consumer)` (`AbstractBeanAssert:66-69`) | **before** the assertion | …sees the violations |
| `isValidFor(…, Consumer)` (`AbstractPropertyAssert:57-58`) | **before** the assertion | …sees the violations |
| `isNotValid(Consumer)` (`AbstractBeanAssert:128`) | **after** the assertion | …never runs |
| `hasValidProperty(…, Consumer)` (`AbstractBeanAssert:156`) | **after** the assertion | …never runs |

Whether a consumer is a *callback that always observes* or a *post-success hook* is a contract, and right now
it depends on which method you called. Nothing in the javadoc mentions the difference. Pick one — "before"
reads better for a test library, since the violation set is most interesting precisely when the assertion is
about to fail — and document it.

### 3.3 Inconsistent variance on the consumer parameter — fixed

```java
SELF isValid           (Consumer<? super Set<ConstraintViolation<ACTUAL>>> consumer);  // BeanAssert:46
SELF isNotValid        (Consumer<      Set<ConstraintViolation<ACTUAL>>> consumer);    // BeanAssert:116
SELF hasValidProperty  (String, Consumer<? super Set<…>> consumer);                    // BeanAssert:133
```

`isNotValid` is the odd one out, so a `Consumer<Object>` that compiles against `isValid` is rejected by
`isNotValid`. Fixed: `isNotValid` now takes `? super`, matching the other two.

### 3.4 `doesNotHaveValidProperty` does not record its violations — fixed

`AbstractBeanAssert.java:166` validates into a local variable, while the other four methods route through
`delegate.setViolations(…)`. After a `doesNotHaveValidProperty` call the delegate still holds whatever the
*previous* assertion left behind. Nothing public reads that state today (see §4.2), which is the only reason
this is latent rather than a live bug.

### 3.5 `hasMessage` casts instead of using `myself` — fixed

`AbstractConstraintViolationAssert.java:113` returns `(SELF) this` where `AbstractAssert.myself` is in scope
and already typed. The cast is unchecked and unsuppressed; every sibling method in the same class returns
`myself`.

### 3.6 Interface and implementation disagree on the descriptor bound — fixed

```java
interface ConstraintDescriptorAssert<SELF, ACTUAL extends ConstraintDescriptor<? extends T>, T …>
abstract class AbstractConstraintDescriptorAssert<SELF, ACTUAL extends ConstraintDescriptor<T>, T …>
```

The class narrowed the interface's bound, so the wildcard the interface advertised was unusable through the
only implementation. Resolved by construction in §7: the interface is gone and the class's bound — the one the
code actually needs — is now the only declaration of it.

### 3.7 `BeanConditions.valid` swallows the assertion error — open

`BeanConditions.java:36-43`

```java
} catch (final AssertionError ae) {
}
return false;
```

An empty catch with a named-but-unused variable. As a `Condition` the boolean is the contract, so returning
`false` is right — but the discarded `AssertionError` carries the only description of *why* it failed, and the
condition reports nothing in its place. Either set a description from `ae.getMessage()` or add a comment
saying the message is deliberately dropped. The class is unused anyway (§4.1).

### 3.8 `ValidationInstanceOfAssertFactories` is a constant interface — open

Declared `public interface` holding one `static` method. An interface cannot forbid instantiation and *can* be
`implements`-ed, which is the constant-interface antipattern; the module's four other factory/utility holders
(`AssertFactories`, `ValidationAssertConstants`, `ValidationAssertUtils`, `ValidationAssertions`) are all
`final class` with a throwing private constructor. Make it match.

---

## 4. Dead and unreachable code

### 4.1 Classes with no reachable use

| file | state |
|---|---|
| `ValidationAssertUtils` | empty but for a throwing private constructor |
| `ValidationAssertConstants` | empty but for a throwing private constructor |
| `AssertFactories` | empty; body is two commented-out method sketches |
| ~~`ConstraintValidatorAssert`~~ | ~~package-private interface, empty body, nothing implements it~~ — deleted in §7 |
| `BeanConditions` | package-private, no caller in main or test |

Roughly 180 lines that compile to nothing. Either finish them or delete them; as placeholders they cost a
reader time on every pass. `ConstraintValidatorAssert` was deleted as part of §7; the other four remain.

### 4.2 An entire feature is sealed off from callers — fixed in §8.2

`ValidationAssertions.assertThatIterableOfConstraintViolations` (line 72) is **package-private**, as are
`IterableOfConstraintViolationsAssert` and `AbstractIterableOfConstraintViolationsAssert`. Every other
`assertThat*` entry point is `public`. So the one assertion for the type `Validator.validate` actually returns
— a `Set<ConstraintViolation<T>>` — cannot be reached from outside the package. For a Jakarta Validation
extension this is the most natural entry point there is, and it is the one that is not exposed.

Related: `AbstractIterableOfConstraintViolationsAssert` declares its `ELEMENT_ASSERT` type parameter as
`DefaultConstraintViolationAssert<T>`, a package-private class. Even made public, the assert would leak a type
callers cannot name. It should be `AbstractConstraintViolationAssert<?, ConstraintViolation<T>, T>`.

### 4.3 `DefaultPathAssert.newAbstractIterableAssert` fabricates a fake `Path`

`DefaultPathAssert.java:107-110`

```java
return new DefaultPathAssert(() -> (Iterator<Path.Node>) iterable.iterator());
```

`Path` is `Iterable<Node>`, so a lambda satisfies the compiler — but the result is not a real path. It has no
meaningful `toString()`, which is exactly what `Path` is normally read for, so any failure message produced
after a `filteredOn`/`extracting` on a path assertion degrades to a lambda's identity hash. Inherited
`AbstractIterableAssert` operations are reachable on `AbstractPathAssert`, so this is reachable.

### 4.4 Commented-out code blocks

`AbstractValidationAssert:76-78`, `AbstractPathAssert:228-236`, `ConstraintDescriptorAssert:56-63` and `:70`,
`AbstractConstraintDescriptorAssert` (several `// ---` section headers marking members that were never
written). Separately, `AbstractConstraintDescriptorAssert` has five empty section banners —
`messageTemplate`, `payload`, `valueWrapping`, `reportAsSingleViolation`, `constraintValidatorClasses` — for
`ConstraintDescriptor` members with no assertion at all. That is the descriptor API's real coverage gap, listed
in §6.3.

---

## 5. Test coverage

64 tests pass, and that number is misleading. **19 of 38 main types are never named by any test:**

```
AbstractConstraintDescriptorAssert   AbstractConstraintViolationAssert
AbstractIterableOfConstraintViolationsAssert  AbstractPropertyAssert
AbstractValidationAssert             AssertFactories
BeanConditions                       ConstraintDescriptorAssert
ConstraintValidatorAssert            DefaultBeanAssert
DefaultConstraintDescriptorAssert    DefaultPathAssert
DefaultPropertyAssert                IterableOfConstraintViolationsAssert
PathAssert                           ValidationAssertConstants
ValidationAssertMessages             ValidationAssertUtils
package-info
```

This is not a uniform thinness — it is concentrated exactly where the bugs are:

- **`AbstractPathAssert` + `DefaultPathAssert` are 841 lines, 28% of the main source, and have
  zero direct tests.** `grep` for `extractingNode`, `nodeAt`, `extractingBeanNode` or `extractingPropertyNode`
  across `src/test` returns nothing. That is why §2.2 survived.
- **`ValidationAssertMessages` has no test**, which is why §2.3 survived — and it is the class that renders
  every failure message this library produces.
- The tests that do exist cluster on `example/user`: 22 of 35 files exercise one `User` bean through
  `assertThatBean`/`assertThatProperty`. Valuable as documentation, but they are breadth-one.

Highest-value additions, in order:

1. `nodeAt` / `extractingNode(int)` across a multi-segment path — a nested `@Valid` bean gives a two-node path
   in four lines of setup, and would have caught §2.2 immediately.
2. `ValidationAssertMessages.format(Set)` with two violations — catches §2.3.
3. The `ConstraintDescriptor` assertions, none of which are tested at all.
4. A test that fails on purpose and asserts the *message text*. The library's whole value is its failure
   output, and nothing currently asserts a single character of it.

---

## 6. Enhancements

### 6.1 Expose the set-of-violations assertion (highest value) — done in §8.2

Make `assertThatIterableOfConstraintViolations` public, widen its element assert (§4.2), and add the overload
people actually reach for:

```java
assertThatConstraintViolations(validator.validate(bean))
        .hasSize(2)
        .extractingPropertyPath()          // -> paths
        .containsExactly("name", "age");
```

Today the only way to inspect a violation set is to pass a `Consumer` into `isValid`/`isNotValid` and assert
inside it, which breaks the fluent chain.

### 6.2 Fill in the `Validator` surface — done in §8.3 and §8.4

The module wraps three of the Jakarta Validation entry points — `validate`, `validateProperty`,
`validateValue`. `ExecutableValidator` is untouched: `validateParameters`, `validateReturnValue`,
`validateConstructorParameters`, `validateConstructorReturnValue`. `AbstractConstraintViolationAssert` already has
`extractingExecutableParameters` and `extractingExecutableReturnValue`, so half the plumbing for method
validation exists with no way to produce the violations it reads.

Likewise `Validator.getConstraintsForClass` → `BeanDescriptor`, which would let assertions run against a bean's
*metadata* rather than an instance.

### 6.3 Finish `AbstractConstraintDescriptorAssert` — done in §8.2

Five declared-but-empty sections (§4.4): `messageTemplate`, `payload`, `valueWrapping`,
`reportAsSingleViolation`, `constraintValidatorClasses`. `messageTemplate` in particular is what you assert when
testing a custom constraint, and it is the obvious next one.

### 6.4 Convenience assertions on the violation set

The common shapes have no shorthand:

```java
assertThatBean(user).isNotValid().hasViolationOn("name");
assertThatBean(user).isNotValid().hasViolationWithMessageTemplate("{jakarta.validation.constraints.NotBlank.message}");
assertThatBean(user).hasExactlyViolationsOn("name", "age");
```

Each is a few lines over the existing `delegate.getViolations()`, and each removes a `Consumer` lambda from
caller code.

### 6.5 Make the delegate's stored violations earn their place

`ValidationAssertDelegate` keeps `violations` as a field, written by every assertion and read only to build
failure messages — a scratch local promoted to state, which also makes an assert object non-reentrant. Either
expose it (`SELF satisfiesViolations(Consumer)`, or §6.1's chaining, which would give the field a reason to
exist) or demote it back to a local.

### 6.6 Smaller items

- **`acceptViolations` wraps too broadly** (`ValidationAssertDelegate:82-86`): any `Exception` from a
  caller's consumer is re-wrapped in a bare `RuntimeException`. `AssertionError` is an `Error` and passes
  through unharmed — which is the case that matters — but everything else loses its type at the boundary.
- **`getViolations()` allocates a new `HashSet` per call**, and `isValid` calls it three times per assertion.
- **A JPMS `module-info`.** The jar carries only `Automatic-Module-Name`. At release 17 a real module
  descriptor is available, and both dependencies (`jakarta.validation`, `org.assertj.core`) are already
  named modules.
- **100 javadoc warnings** (missing `@param`, undocumented public members) now that `<doclint>none</doclint>`
  is gone. They do not fail the build. `AbstractConstraintDescriptorAssert` and `AbstractConstraintViolationAssert` account
  for most of them.
- **`package-info.java` carries its license header *after* the package declaration.** Legal, since it is just a
  comment, but it is the only file in the module that does this.
- **`ValidationAssertions.assertThatConstraintDescriptor` has no javadoc**, alone among the public entry points.

---

## 7. The interface layer, removed (applied)

### 7.1 What assertj-core actually does

Checked against `assertj-core` 4.0.0-M1 — the version this module builds against, and the current release on
Central — plus `assertj-guava` on `main` and the reference documentation's §2.6.2 *Custom Assertions*.

Core's shape is uniform across all 70 of its `Abstract*` classes:

```
AbstractAssert<SELF, ACTUAL> implements Assert<SELF, ACTUAL>    ← only the root implements an interface
  └─ AbstractXxxAssert<SELF, …>   [implements <capability mixin>]   ← all behavior
       └─ XxxAssert extends AbstractXxxAssert<XxxAssert>            ← concrete, ~5 lines, no interface
```

Of 232 files in `org.assertj.core.api`, 29 are interfaces, and every one is either the root contract
(`Assert`), a **capability mixin** shared by unrelated hierarchies (`NumberAssert` — 9 implementors,
`EnumerableAssert` — 7, `ComparableAssert`, `ObjectEnumerableAssert`, `ArraySortedAssert`, `Array2DAssert`,
`Descriptable`, `ExtensionPoints`), or SPI that is not an assertion at all (`AssertFactory`, `AssertProvider`,
`AssertDelegateTarget`, `InstanceOfAssertFactories`, the soft-assertion providers). There is no
`StringAssert`, `FileAssert`, `OptionalAssert` or `ListAssert` interface — not one per-concept interface
anywhere in the library.

The official extension module is flatter still. `assertj-guava` has no abstract layer either:

```java
public class MultimapAssert<K, V> extends AbstractAssert<MultimapAssert<K, V>, Multimap<K, V>>
public class RangeAssert<T extends Comparable<T>> extends AbstractAssert<RangeAssert<T>, Range<T>>
```

and the reference guide prescribes exactly that for extensions: *"create a class inheriting from
`AbstractAssert` and add your custom assertions methods"*, plus a static entry point. Interfaces appear
nowhere in the extension guidance.

### 7.2 Why this module's interfaces were redundant

The module ran three layers per concept where core runs two and guava runs one, and four findings made the
middle layer indefensible:

1. **Every interface had exactly one implementor.** `ValidationAssert`, `PropertyAssert`, `BeanAssert`,
   `ConstraintViolationAssert`, `ConstraintDescriptorAssert` and `PathAssert` were each implemented by one
   `Abstract*` class with one `Default*` subclass. None was doing the mixin job that justifies core's
   interfaces.
2. **They were not on the public API surface.** Every entry point returns the abstract class —
   `AbstractBeanAssert<?, ACTUAL> assertThatBean(…)`, `AbstractPathAssert<?, ?> assertThatPath(…)` — as does
   `ValidationInstanceOfAssertFactories`. Outside their own `extends`/`implements` chain the interfaces
   appeared in main sources exactly once, as the return type of
   `ConstraintViolationAssert.extractingConstraintDescriptor()`, which was itself inconsistent with its
   sibling extractors. A caller could never name these types.
3. **The abstraction pointed the wrong way.** `ConstraintViolationAssert`'s own signatures referenced
   `AbstractConstraintDescriptorAssert`, `AbstractObjectArrayAssert` and `AbstractAssert`. It was not a
   boundary; it was circularly coupled to the layer it was meant to abstract.
4. **The logic had migrated into the interfaces**, inverting core's layout: `PathAssert` was 43 default
   methods against 1 abstract, `ConstraintViolationAssert` 18 against 2, while the `Abstract*` classes were
   shells of `@Override` delegations. `PropertyAssert` was the opposite extreme — 1 default method, 0
   abstract. Two parallel declarations of one contract is what allowed §3.6 to drift.

### 7.3 What changed

Six interfaces folded into their abstract classes and deleted: `ValidationAssert`, `PropertyAssert`,
`BeanAssert`, `ConstraintViolationAssert`, `ConstraintDescriptorAssert`, `PathAssert`. Dead
`ConstraintValidatorAssert` deleted outright (§4.1). Every default method moved down as a concrete method with
its Javadoc; the `(SELF) this` unchecked casts in those bodies became `myself`, which removed nine
`@SuppressWarnings("unchecked")` annotations along the way.

`AbstractValidationAssert` was widened from package-private to `public`, so that `targetingGroups`,
`usingValidator` and `usingValidatorSuppliedBy` keep a documented declaring type now that the public
interface that used to carry them is gone.

**Kept as interfaces:** the four node mixins, `HasContainerClass`, `HasTypeArgumentIndex`, `HasParameterTypes`
and `HasParameterIndex`, promoted out of `PathAssert.NodeAssert` to nested types of `AbstractPathAssert`. Each
is multiply inherited by node asserts that share no base beyond `_AbstractNodeAssert` — `HasContainerClass` by
the bean, container-element and property nodes; `HasParameterTypes` by the constructor and method nodes — so
Java's single class inheritance leaves no alternative. They were re-bounded self-referentially
(`SELF extends HasContainerClass<SELF>`) the way core bounds `NumberAssert` and `EnumerableAssert`, rather
than on the package-private `_AbstractNodeAssert`, which would have leaked an unnameable type into a public
signature.

**Also deleted:** the eight empty per-node interfaces (`BeanNodeAssert`, `ConstructorNodeAssert`,
`ContainerElementNodeAssert`, `CrossParameterNodeAssert`, `MethodNodeAssert`, `ParameterNodeAssert`,
`PropertyNodeAssert`, `ReturnValueNodeAssert`), each of which had an empty body and one implementor, and whose
only content was combining `NodeAssert<SELF, X>` with mixins the implementing class can list directly.

On the test side, `ValidationAssertTest`, `PropertyAssertTest`, `BeanAssertTest` and `AbstractBeanAssertTest`
were deleted. The four mirrored the interface chain, declared no `@Test` method between them, had no concrete
subclass, and their one `assertionClass` field was never read. Two live references were rebound:
`ConstraintViolationAssert_$Test.assertion` now returns `AbstractConstraintViolationAssert`, and
`User_TargetingGroups_Test`'s Javadoc link now targets `AbstractValidationAssert`. The eight `{@snippet}`
`@link` targets in `package-info` were repointed at the abstract classes.

### 7.4 Verification

`mvn verify` passes: 64 tests, 0 failures, 0 errors — identical to the pre-change baseline. `javadoc:javadoc`
builds with no unresolved reference, which exercises every `{@snippet}` `@link` target. `javap` over the built
classes confirms every assertion method from the six deleted interfaces is still present and publicly
reachable on the corresponding abstract class.

The published API surface is now the `Abstract*Assert` classes, the four `Has*` mixins, `DefaultPathAssert`,
`ValidationAssertions` and `ValidationInstanceOfAssertFactories` — core's shape.

### 7.5 The one thing given up

A third party can no longer attach these assertion contracts to a class that already extends something else.
Since the entry points never exposed the interfaces, nothing outside this package could have been doing that.

---

## 8. API coverage, completed (applied)

Audited against `jakarta.validation-api` 3.1.1, the artifact this build resolves, and the published
[3.1 apidocs](https://jakarta.ee/specifications/bean-validation/3.1/apidocs/).

### 8.1 What the audit found

Of 88 top-level API types &mdash; 32 annotations, 9 exceptions, 7 enums, 39 interfaces and 1 class &mdash; the
annotations, exceptions and the bootstrap/SPI interfaces are inputs or plumbing, not assertion targets. What
remains splits into three lobes, and the module covered roughly one of them:

| lobe | reached via | before | after |
|---|---|---|---|
| validation results | `Validator.validate` / `validateProperty` / `validateValue` | near-complete | complete |
| metadata | `Validator.getConstraintsForClass` | 1 of 18 types | complete |
| executable validation | `Validator.forExecutables` | 0 of 4 methods | complete |

Method-level, before: `ConstraintViolation` 9/11, `ConstraintDescriptor` 5/11, `Path.Node` and its eight
subtypes complete but mostly unreachable, `ExecutableValidator` 0/4, the metadata descriptors 0.

### 8.2 Finishing work on what existed

- **The set-of-violations assertion is public.** `assertThatIterableOfConstraintViolations` was
  package-private, so the one assertion for the type `Validator.validate` actually returns could not be called
  from outside the package. It and its chain are now public, joined by `assertThatConstraintViolations(Set)`
  reading in the shape callers have.
  <br>§4.2 proposed widening `ELEMENT_ASSERT` to `AbstractConstraintViolationAssert<?, ConstraintViolation<T>, T>`.
  That cannot compile: assertj bounds it `ELEMENT_ASSERT extends AbstractAssert<ELEMENT_ASSERT, ELEMENT>`, a
  self-type a wildcard cannot satisfy. Resolved instead by publishing the concrete
  `DefaultConstraintViolationAssert`, which is what assertj does with `ObjectAssert` as `ListAssert`'s element
  assert.
- **`ConstraintViolation` is 11/11.** Added `hasMessageTemplate` / `extractingMessageTemplate` and
  `extractingAsUnwrapped` / `isEqualToWhenUnwrappedAs`. The template assertion matters on its own: `hasMessage`
  asserts the *interpolated* message, so until now there was no locale-independent way to assert which
  constraint fired.
- **`ConstraintDescriptor` is 11/11.** The six empty section banners are filled: `attributes`,
  `constraintValidatorClasses`, `messageTemplate`, `payload`, `valueUnwrapping` (the banner was misspelled
  `valueWrapping`) and `reportAsSingleViolation`. The two commented-out method sketches noted in §4.4 are gone,
  replaced by working implementations.
- **All nine node kinds are reachable.** `AbstractPathAssert` exposed only `extractingNode`,
  `extractingBeanNode` and `extractingPropertyNode`, so six of the eight typed node asserts had no entry point,
  and `AbstractContainerElementNodeAssert` and `AbstractReturnValueNodeAssert` had no concrete subclass at all
  &mdash; uninstantiable even internally. Added the six missing `extracting*Node` / `has*NodeSatisfying` pairs
  and the two missing `Default*` classes, and deleted `DefaultParameterizedNodeAssert`, a stray duplicate of
  `DefaultParameterNodeAssert`. The five package-private node assert classes are now public, matching the four
  that already were.
  <br>The generic `extractingNode(int, Class, AssertFactory)` was bound on the package-private
  `_AbstractNodeAssert`, leaking an unnameable type into a public signature; it is now bound on
  `AbstractAssert`, consistent with every other extractor in the module.

### 8.3 The metadata lobe

Thirteen new assertion classes covering the whole `jakarta.validation.metadata` tree, reached from
`assertThatBeanDescriptor(validator.getConstraintsForClass(Foo.class))` and navigable down to properties,
methods, constructors, parameters, return values, cross-parameters, container element types and group
conversions.

The notable design point: `CascadableDescriptor` and `ContainerDescriptor` look like a case for mixin
interfaces, but in the spec they **always co-occur** &mdash; `PropertyDescriptor`, `ParameterDescriptor`,
`ReturnValueDescriptor` and `ContainerElementTypeDescriptor` each extend `ElementDescriptor`,
`CascadableDescriptor` *and* `ContainerDescriptor`, never a subset. One intersection-bounded class,
`AbstractCascadableContainerDescriptorAssert<SELF, ACTUAL extends ElementDescriptor & CascadableDescriptor &
ContainerDescriptor>`, covers the combination, so the metadata family needs no interface at all. This follows
the rule §7 established: a class unless multiple inheritance makes one impossible.

`ElementDescriptor.ConstraintFinder` is a builder rather than a value, so it gets no assertion class; it is
exposed as `extractingConstraintDescriptors(UnaryOperator<ConstraintFinder>)`, which configures the finder and
asserts on the matched set.

### 8.4 Executable validation

`ExecutableValidator`'s four methods, split by what the `actual` value can be:

- **Method validation** hangs off `AbstractBeanAssert`, where the instance under validation is already the
  actual: `hasValidParameters(Method, Object...)`, `hasValidReturnValue(Method, Object)`, their
  `doesNotHave...` counterparts and consumer overloads.
- **Constructor validation** has no instance &mdash; the object does not exist yet &mdash; so it takes the
  `Constructor` as the actual, through the new `AbstractConstructorAssert` and
  `assertThatConstructor(Constructor)`.

One spec detail worth recording: `validateConstructorReturnValue` with a `null` created object is an
`IllegalArgumentException` by contract, not a constraint violation, so a `@NotNull` on a constructor can never
fail. The test fixture carries a custom `@Named` constructor constraint so the failing path is genuinely
exercised, and a test pins that the `IllegalArgumentException` is propagated rather than reported as a failed
assertion.

### 8.5 Verification

`mvn verify` passes: **105 tests, 0 failures, 0 errors**, up from 64. `javadoc:javadoc` builds with no
unresolved reference across 53 documented types. A re-run of the method-level audit shows 67 of 68 API members
covered.

The one deliberate omission is `Validator.unwrap(Class)`, a provider escape hatch for reaching
implementation-specific types; it yields no value a test would assert on. The bootstrap, configuration and SPI
interfaces (`Configuration`, `ValidatorFactory`, `ValidatorContext`, `Validation`, `spi.*`, `bootstrap.*`,
`MessageInterpolator`, `TraversableResolver`, `ParameterNameProvider`, `ClockProvider`,
`ConstraintValidatorFactory`) remain out of scope by design: they configure validation rather than produce
values to verify.

Still uncovered and worth a later decision: `ConstraintViolationException`, whose `getConstraintViolations()`
is the natural target when validation is triggered by a framework rather than called directly.

---

## 9. Priorities

| | item | § |
|---|---|---|
| ~~1~~ | ~~`nodeAt` off-by-one~~ — fixed | 2.2 |
| ~~2~~ | ~~Multi-violation messages join on a literal `%n`~~ — fixed | 2.3 |
| ~~3~~ | ~~Remove the javax-era reference-guide build~~ — done | 1.4 |
| 4 | Tests for `AbstractPathAssert` and `ValidationAssertMessages` — the two uncovered areas that hold items 1 and 2 | 5 |
| 5 | Validator built from a closed factory, rebuilt per assertion | 2.4 |
| 6 | Consumer-timing inconsistency (§3.1, §3.3–3.6 fixed) | 3.2 |
| ~~7~~ | ~~Make the set-of-violations assertion public~~ — done | 8.2 |
| 8 | Delete the four remaining empty placeholder classes | 4.1 |
| ~~9~~ | ~~Collapse the redundant interface layer~~ — done | 7 |
| ~~10~~ | ~~Finish `ConstraintViolation` and `ConstraintDescriptor`~~ — done | 8.2 |
| ~~11~~ | ~~Make every `Path.Node` kind reachable~~ — done | 8.2 |
| ~~12~~ | ~~Cover the metadata API~~ — done | 8.3 |
| ~~13~~ | ~~Cover executable validation~~ — done | 8.4 |
| 14 | An assertion for `ConstraintViolationException` | 8.5 |
