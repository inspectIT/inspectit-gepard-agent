#!/bin/bash -e
# script to update the OpenTelemetry Java agent version


# version, which should be used for the java agent
version=$1

if [[ $version == *-SNAPSHOT ]]; then
  alpha_version=${version//-SNAPSHOT/-alpha-SNAPSHOT}
else
  alpha_version=${version}-alpha
fi

sed -Ei "s/(opentelemetryJavaagent *: )\"[^\"]*\"/\1\"$version\"/" build.gradle
sed -Ei "s/(opentelemetryJavaagentAlpha *: )\"[^\"]*\"/\1\"$alpha_version\"/" build.gradle

sed -Ei "s/(io.opentelemetry.instrumentation.muzzle-generation\" version )\"[^\"]*\"/\1\"$alpha_version\"/" build.gradle
sed -Ei "s/(io.opentelemetry.instrumentation.muzzle-check\" version )\"[^\"]*\"/\1\"$alpha_version\"/" build.gradle
