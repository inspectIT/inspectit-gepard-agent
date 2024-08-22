# Contributing

## IDE

We recommend using [IntelliJ](https://www.jetbrains.com/idea/download/#section=windows) as IDE for contributing.

## Formatting

We have [spotless](https://github.com/diffplug/spotless) configured to format the code. You can run the following commands:

- `./gradlew spotlessCheck` to validate the formatting of the code.
- `./gradlew spotlessApply` to format the code.

Be aware that the CI will fail if the code is not formatted correctly, as `spotlessCheck` is part of the build process.

## Building

An OpenTelemetry Extension can be used in two different ways. 
Either as a standalone extension or as part of an OpenTelemetry Agent.

Use the command `./gradlew extendAgent` to build both versions.

The File called `inspectit-gepard-agent-[VERSION].jar` will contain the standalone extension, while `opentelemetry-javaagent.jar` contains the extended Agent.

Be aware that the build will fail, if the code is not formatted correctly, as the `build`-task depends on `spotlessCheck`.
## Testing

To run the tests, use the command `./gradlew test`.

## Releasing

Currently, there is no automatic release process, as we are still in the early stages of development.