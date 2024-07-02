#!/bin/bash -e
# script to update the OpenTelemetry SDK version

# version, which should be used for the SDK
version=$1

sed -Ei "s/(opentelemetrySdk *: )\"[^\"]*\"/\1\"$version\"/" build.gradle
