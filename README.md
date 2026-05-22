# HMPPS Supervision

[![Repository Standards](https://img.shields.io/badge/dynamic/json?color=blue&logo=github&label=MoJ%20Compliant&query=%24.message&url=https%3A%2F%2Foperations-engineering-reports.cloud-platform.service.justice.gov.uk%2Fapi%2Fv1%2Fcompliant_public_repositories%2Fhmpps-supervision)](https://operations-engineering-reports.cloud-platform.service.justice.gov.uk/repository-standards/hmpps-supervision "Link to report")

A collection of API services to enable HMPPS Digital Services to interact with core supervision data.

## Projects

* [Supervision Packages](projects/supervision-packages-api/README.md)

## Tooling

* Code is written in [Kotlin](https://kotlinlang.org/), using [Spring Boot](https://spring.io/projects/spring-boot)
* Built using [Gradle](https://gradle.org/) on [GitHub Actions](https://help.github.com/en/actions)
* Container images are built with [Jib](https://github.com/GoogleContainerTools/jib#readme), and pushed
  to [GitHub Packages](https://github.com/orgs/ministryofjustice/packages?repo_name=hmpps-supervision)
* Code formatting by [IntelliJ IDEA formatter](https://www.jetbrains.com/help/idea/command-line-formatter.html),
  according to [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)

## Structure

This repository is structured as a monorepo containing an individually deployable project per service.

The directory layout is as follows:

```
├── .github           ~ GitHub actions workflows and configuration
├── .idea             ~ IntelliJ IDEA configuration
└── projects          ~ Source code for each project
    ├── project-1
    ├── ...
    └── project-n
```

## Development

To set up your development environment:

1. Open the project in [IntelliJ IDEA](https://www.jetbrains.com/idea/). Select "Import project from external model", then "Gradle".
2. To run the tests for a service, right-click the project folder and select "Run tests".
3. To start a service locally, launch the corresponding Spring Boot run/debug configuration.
   See [Launch a run configuration](https://www.jetbrains.com/help/idea/run-debug-configuration.html#launch-run-configuration).

### Code formatting

Kotlin code is formatted using IntelliJ IDEA's code formatter,
which follows the [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html).

GitHub Actions will automatically fix any formatting issues when you open a pull request. See [format.yml](.github/workflows/format.yml).

You can also use <strong title="Command">⌘</strong><strong title="Option">⌥</strong>**L** (macOS),
or **Ctrl+Alt+L** (Windows/Linux) to manually reformat your code in IntelliJ IDEA.
See [Reformat code](https://www.jetbrains.com/help/idea/reformat-and-rearrange-code.html).

Note: The code formatter does not remove unused imports by default. You should
enable [Optimise on save](https://www.jetbrains.com/help/idea/creating-and-optimizing-imports.html#optimize-on-save) in
your IntelliJ IDEA settings to ensure you do not commit unused imports.

## Build

IntelliJ will automatically build your code as needed. To build using Gradle, follow the instructions below.

### Gradle

Any tasks you run from the root project, without specifying a project name will be run on all the children. To build the
entire repository using Gradle, run:

```shell
./gradlew build
```

To build just a specific project:

```shell
./gradlew <project-name>:build

# For example,
./gradlew supervision-packages-api:build
```

### Docker

To build Docker images locally, run:

```shell
./gradlew jibDockerBuild
```

## Run

### Gradle

To run Gradle tasks in a subproject, prepend the task name with the name of the project. Environment variables can be
used to set the Spring profile. For example,

```shell
SPRING_PROFILES_ACTIVE=dev ./gradlew projects:<project-name>:bootRun
```

## Support

For any issues or questions, please contact the Probation Integration team via the [#probation-integration-team](https://moj.enterprise.slack.com/archives/C02QSERFGMB)
Slack channel. Or feel free to create a [new issue](https://github.com/ministryofjustice/hmpps-supervision/issues/new)
in this repository.
