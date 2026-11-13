# assertj-validation

[![CI](https://github.com/jinahya/assertj-validation/actions/workflows/maven.yml/badge.svg)](https://github.com/jinahya/assertj-validation/actions/workflows/maven.yml)

[![Maven Central](https://img.shields.io/maven-central/v/io.github.jinahya/assertj-validation?label=maven-central)](https://central.sonatype.com/artifact/io.github.jinahya/assertj-validation)
[![javadoc](https://javadoc.io/badge2/io.github.jinahya/assertj-validation/javadoc.svg)](https://javadoc.io/doc/io.github.jinahya/assertj-validation)

An [AssertJ](https://assertj.github.io/doc/) extension for [Jakarta Validation](https://beanvalidation.org/).

<img src="https://avatars.githubusercontent.com/u/18898355?s=400" height="100" alt="AssertJ"/>
<img src="https://beanvalidation.org/logo/logo.svg" height="100" alt="Jakarta Validation"/>

## Coordinates

```xml
<dependency>
  <groupId>io.github.jinahya</groupId>
  <artifactId>assertj-validation</artifactId>
  <version>${version.io.github.jinahya.assertj-validation}</version>
  <scope>test</scope>
</dependency>
```

The two APIs it extends are *provided*: bring your own, along with a validation provider for default validation.
For Jakarta EE 11, e.g.,

```xml
<dependency>
  <groupId>org.assertj</groupId>
  <artifactId>assertj-core</artifactId>
  <version>3.27.7</version>
  <scope>test</scope>
</dependency>
<dependency>
  <groupId>jakarta.validation</groupId>
  <artifactId>jakarta.validation-api</artifactId>
  <version>3.1.1</version>
  <scope>provided</scope>
</dependency>
<!-- the provider, and the expression language it interpolates messages with; for Jakarta EE 10, use 8.0.x and 5.0.x -->
<dependency>
  <groupId>org.hibernate.validator</groupId>
  <artifactId>hibernate-validator</artifactId>
  <version>9.1.4.Final</version>
  <scope>test</scope>
</dependency>
<dependency>
  <groupId>org.glassfish.expressly</groupId>
  <artifactId>expressly</artifactId>
  <version>6.0.0</version>
  <scope>test</scope>
</dependency>
```

A test which only ever hands the assertions a validator of its own (`usingValidator`, `usingValidatorFactory`) still
needs whatever produced that validator; no provider is reached for otherwise.

## Compatibilities

Both APIs this module sits between are *provided*-scoped, so a consumer brings its own:

| | |
|---|---|
| Java | 17 or later at runtime; JDK 25 to build and run the test suite |
| Jakarta Validation | 3.0 and 3.1 |
| AssertJ | 3.x from 3.27.7; 4.0.0-M1 is tested as a milestone, and later 4.x releases are verified as they come |

What is tested, on every push:

- the whole suite on JDK 25, for each Jakarta generation, with AssertJ 3.27.7 and with 4.0.0-M1;
- a consumer on **Java 17**, against the jar as packaged (built on AssertJ 3), for each AssertJ line and each Jakarta
  generation: [`.ci/java17`](.ci/java17).

### Jakarta EE generations

The build runs against one generation at a time, selected by a profile. Each pins the platform BOM — which in
turn pins `jakarta.validation-api` — along with the Hibernate Validator and Expressly releases aligned with it.

| profile | `jakarta.validation-api` | Hibernate Validator | Expressly |
|---|---|---|---|
| `jakartaee-11` (default) | 3.1.1 | 9 | 6 |
| `jakartaee-10` | 3.0.2 | 8 | 5 |

```shell
./mvnw -P jakartaee-10 test
```

### AssertJ

The build compiles against AssertJ 3, which every consumer can run, and targets AssertJ 4: the `assertj-4` profile
runs the same build against it. Combine it with a generation.

```shell
./mvnw -P jakartaee-11,assertj-4 test
```

Only Hibernate Validator and Expressly are test-scoped: nothing under `src/main` references an implementation,
which is the point of extending the API rather than the engine.

## Usages

See
the [package-info](https://javadoc.io/doc/io.github.jinahya/assertj-validation/latest/com/github/jinahya/assertj/validation/package-summary.html).

[6.1.1. Validation methods]: https://jakarta.ee/specifications/bean-validation/3.0/jakarta-bean-validation-spec-3.0.html#validationapi-validatorapi-validationmethods
