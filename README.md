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

## Compatibilities

Both APIs this module sits between are *provided*-scoped, so a consumer brings its own:

| | |
|---|---|
| Java | 17 (tests are built at 25) |
| Jakarta Validation | 3.0 and 3.1 |
| AssertJ | the latest [org.assertj:assertj-core](https://javadoc.io/doc/org.assertj/assertj-core/latest/index.html) |

### Jakarta EE generations

The build runs against one generation at a time, selected by a profile. Each pins the platform BOM — which in
turn pins `jakarta.validation-api` — along with the Hibernate Validator and Expressly releases aligned with it.

| profile | `jakarta.validation-api` | Hibernate Validator | Expressly |
|---|---|---|---|
| `jakarta-ee-11` (default) | 3.1.1 | 9 | 6 |
| `jakarta-ee-10` | 3.0.2 | 8 | 5 |

```shell
./mvnw -P jakarta-ee-10 test
```

Only Hibernate Validator and Expressly are test-scoped: nothing under `src/main` references an implementation,
which is the point of extending the API rather than the engine.

## Usages

See
the [package-info](https://javadoc.io/doc/io.github.jinahya/assertj-validation/latest/com/github/jinahya/assertj/validation/package-summary.html).

[6.1.1. Validation methods]: https://jakarta.ee/specifications/bean-validation/3.0/jakarta-bean-validation-spec-3.0.html#validationapi-validatorapi-validationmethods
