#!/usr/bin/env bash
# Compiles and runs the Java 17 consumer against the packaged jar in target/; run from the repository root, on JDK 17,
# after the jar is packaged. Any -D option is passed on to the classpath resolution, e.g. -Dassertj.version=4.0.0-M1.
set -euo pipefail
dir=.ci/java17
jars=()
for jar in target/assertj-validation-*.jar; do
  case "$jar" in *-sources.jar | *-javadoc.jar | *-tests.jar) ;; *) jars+=("$jar") ;; esac
done
test "${#jars[@]}" -eq 1 || { echo "expected one packaged jar in target/, found ${#jars[@]}" >&2; exit 1; }
./mvnw -B -q -f "$dir/pom.xml" "$@" \
  org.apache.maven.plugins:maven-dependency-plugin:3.8.1:build-classpath -Dmdep.outputFile=target/classpath.txt
cp="${jars[0]}:$(cat "$dir/target/classpath.txt")"
rm -rf "$dir/target/classes" && mkdir -p "$dir/target/classes"
javac --release 17 -cp "$cp" -d "$dir/target/classes" "$dir/Java17Consumer.java"
java -cp "$dir/target/classes:$cp" Java17Consumer
