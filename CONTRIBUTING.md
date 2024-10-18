# Contributing

## IDE

We recommend using [IntelliJ](https://www.jetbrains.com/idea/download/#section=windows) as IDE for contributing.

## Dependencies

We integrate our [inspectit-gepard-config](https://github.com/inspectIT/inspectit-gepard-config) model as dependency. We download the dependency
from GitHub Packages, which requires authentication. To set up your authentication, follow these steps:

1. Create a `gradle.properties` file in `%userprofile%\.gradle`
2. Create a [(classic) personal access token (PAT)](https://github.com/settings/tokens) with `read:packages` permissions.
3. Paste the following content into your `gradle.properties`:

```
gpr.inspectit.gepard.user=<YOUR_GITHUB_USERNAME>
gpr.inspectit.gepard.token=<YOUR_GITHUB_ACCESS_TOKEN>
```

You can find more information here as well: https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry

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
