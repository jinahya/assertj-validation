# assertj-validation — analysis

An AssertJ extension for Jakarta Validation: ~3.3k lines of main source in one package,
`com.github.jinahya.assertj.validation`, plus 35 test classes.

This document records the state of the module as of 2026-10-01, the build realignment applied in this pass,
and the problems and enhancements left open. Everything below was read against the working tree, and every
defect marked **confirmed** was reproduced.

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

### 1.4 Left undone — needs your hand

The sandbox refused the deletions. These files are now orphaned and should go:

```shell
git rm -r sub src/test/java-sub-hibernate-validator-referenceguide .mvn_hiberate_validator_referenceguide.sh
```

- `sub/bval`, `sub/hibernate-validator` — git submodules, both uninitialised, referenced only by the
  `sub-hibernate-validator-referenceguide` profile that no longer exists. Already removed from `.gitmodules`.
- `src/test/java-sub-hibernate-validator-referenceguide/` — four test classes that extend Hibernate Validator 6
  (javax-era) reference-guide classes living in that submodule. They cannot survive the Jakarta move, and no
  profile adds the source root any more, so they are dead weight rather than a build failure. Git keeps them
  if you want to port them later.
- `.mvn_hiberate_validator_referenceguide.sh` — driver for the deleted profile (and a typo in its own name).

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

### 2.2 `AbstractPathAssert.nodeAt` is off by one — **confirmed, open**

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

Fix: `for (int i = 0; i < index; i++)`.

This is the single most consequential bug in the module, and §5 explains why nothing caught it.

### 2.3 Multi-violation failure messages join on a literal `%n` — **confirmed, open**

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

Fix: `Collectors.joining(System.lineSeparator())`. Note that the per-violation `format(ConstraintViolation)`
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

### 3.1 Dead branch left in `isNotValid()` — open

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

### 3.3 Inconsistent variance on the consumer parameter — open

```java
SELF isValid           (Consumer<? super Set<ConstraintViolation<ACTUAL>>> consumer);  // BeanAssert:46
SELF isNotValid        (Consumer<      Set<ConstraintViolation<ACTUAL>>> consumer);    // BeanAssert:116
SELF hasValidProperty  (String, Consumer<? super Set<…>> consumer);                    // BeanAssert:133
```

`isNotValid` is the odd one out, so a `Consumer<Object>` that compiles against `isValid` is rejected by
`isNotValid`. Add the `? super`.

### 3.4 `doesNotHaveValidProperty` does not record its violations — open

`AbstractBeanAssert.java:166` validates into a local variable, while the other four methods route through
`delegate.setViolations(…)`. After a `doesNotHaveValidProperty` call the delegate still holds whatever the
*previous* assertion left behind. Nothing public reads that state today (see §4.2), which is the only reason
this is latent rather than a live bug.

### 3.5 `hasMessage` casts instead of using `myself` — open

`AbstractConstraintViolationAssert.java:113` returns `(SELF) this` where `AbstractAssert.myself` is in scope
and already typed. The cast is unchecked and unsuppressed; every sibling method in the same class returns
`myself`.

### 3.6 Interface and implementation disagree on the descriptor bound — open

```java
interface ConstraintDescriptorAssert<SELF, ACTUAL extends ConstraintDescriptor<? extends T>, T …>
abstract class AbstractConstraintDescriptorAssert<SELF, ACTUAL extends ConstraintDescriptor<T>, T …>
```

The class narrows the interface's bound, so the wildcard the interface advertises is unusable through the only
implementation. One of the two is wrong; the class's bound is the one the code actually needs.

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
| `ConstraintValidatorAssert` | package-private interface, empty body, nothing implements it |
| `BeanConditions` | package-private, no caller in main or test |

Roughly 180 lines that compile to nothing. Either finish them or delete them; as placeholders they cost a
reader time on every pass.

### 4.2 An entire feature is sealed off from callers

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
written). Separately, `ConstraintDescriptorAssert` has five empty section banners —
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

- **`PathAssert` + `AbstractPathAssert` + `DefaultPathAssert` are 999 lines, 30% of the main source, and have
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

### 6.1 Expose the set-of-violations assertion (highest value)

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

### 6.2 Fill in the `Validator` surface

The module wraps three of the Jakarta Validation entry points — `validate`, `validateProperty`,
`validateValue`. `ExecutableValidator` is untouched: `validateParameters`, `validateReturnValue`,
`validateConstructorParameters`, `validateConstructorReturnValue`. `ConstraintViolationAssert` already has
`extractingExecutableParameters` and `extractingExecutableReturnValue`, so half the plumbing for method
validation exists with no way to produce the violations it reads.

Likewise `Validator.getConstraintsForClass` → `BeanDescriptor`, which would let assertions run against a bean's
*metadata* rather than an instance.

### 6.3 Finish `ConstraintDescriptorAssert`

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
  is gone. They do not fail the build. `ConstraintDescriptorAssert` and `ConstraintViolationAssert` account
  for most of them.
- **`package-info.java` carries its license header *after* the package declaration.** Legal, since it is just a
  comment, but it is the only file in the module that does this.
- **`ValidationAssertions.assertThatConstraintDescriptor` has no javadoc**, alone among the public entry points.

---

## 7. Priorities

| | item | § |
|---|---|---|
| 1 | `nodeAt` off-by-one — silently returns the wrong path node | 2.2 |
| 2 | Multi-violation messages join on a literal `%n` | 2.3 |
| 3 | Complete the deletions the sandbox refused | 1.4 |
| 4 | Tests for `PathAssert` and `ValidationAssertMessages` — the two uncovered areas that hold items 1 and 2 | 5 |
| 5 | Validator built from a closed factory, rebuilt per assertion | 2.4 |
| 6 | `if (true)` dead branch; consumer-timing and variance inconsistencies | 3.1–3.3 |
| 7 | Make the set-of-violations assertion public | 4.2 / 6.1 |
| 8 | Delete the five empty placeholder classes | 4.1 |
