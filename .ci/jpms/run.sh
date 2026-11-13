#!/usr/bin/env bash
# Compiles and runs a consumer on the MODULE path against the packaged jar in target/; run from the repository root,
# after the jar is packaged. The modules are resolved by .ci/java17/pom.xml; any -D option is passed on to that
# resolution, e.g. -Dassertj.version=4.0.0-M1.
set -euo pipefail
dir=.ci/jpms
jars=()
for jar in target/assertj-validation-*.jar; do
  case "$jar" in *-sources.jar | *-javadoc.jar | *-tests.jar) ;; *) jars+=("$jar") ;; esac
done
test "${#jars[@]}" -eq 1 || { echo "expected one packaged jar in target/, found ${#jars[@]}" >&2; exit 1; }
mkdir -p "$dir/target"
./mvnw -B -q -f .ci/java17/pom.xml "$@" \
  org.apache.maven.plugins:maven-dependency-plugin:3.8.1:build-classpath -Dmdep.outputFile="$PWD/$dir/target/modulepath.txt"
mp="${jars[0]}:$(cat "$dir/target/modulepath.txt")"
rm -rf "$dir/target/classes" && mkdir -p "$dir/target/classes"
javac --release 17 --module-path "$mp" -d "$dir/target/classes" $(find "$dir/src" -name '*.java')
# - --add-modules: the provider and its expression language are resolved as services or automatic modules, which
#   nothing requires.
# - --add-reads, --add-exports: what a module-path user of soft-assertion proxies needs on released AssertJ (3.x,
#   4.0.0-M1), which generates the proxy into this module's package, reached through privateLookupIn: org.assertj.core
#   must read this module (the descriptor already opens the package to it), and on 3.27.7 the proxy also needs an
#   AssertJ package that AssertJ's own descriptor does not export. Neither is needed without soft assertions.
module=com.github.jinahya.assertj.validation
java --module-path "$dir/target/classes:$mp" --add-modules ALL-MODULE-PATH \
  --add-reads "org.assertj.core=$module" \
  --add-exports "org.assertj.core/org.assertj.core.api.recursive.assertion=$module" \
  --module consumer/consumer.ModulePathConsumer
