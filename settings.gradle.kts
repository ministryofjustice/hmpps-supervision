rootProject.name = "hmpps-supervision"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            library("applicationinsights", "com.microsoft.azure:applicationinsights-web:3.7.8")
            library("aws", "io.awspring.cloud:spring-cloud-aws-dependencies:4.0.2")
            library("otel", "io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations:2.27.0")
            library("springdoc", "org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.3")
            library("sentry", "io.sentry:sentry-spring-boot-4:8.41.0")

            bundle("telemetry", listOf("applicationinsights", "otel", "sentry"))
        }
    }
}

plugins {
    id("com.gradle.develocity") version "4.4.1"
}
develocity {
    buildScan {
        publishing.onlyIf { System.getenv("CI") != null }
        termsOfUseUrl.set("https://gradle.com/help/legal-terms-of-use")
        termsOfUseAgree.set("yes")
    }
}

// include all projects from the "projects" directory
File(rootProject.projectDir, "projects")
    .listFiles { project -> project.isDirectory && File(project, "build.gradle.kts").run { exists() && isFile } }
    .forEach { projectDir ->
        include(projectDir.name)
        project(":${projectDir.name}").projectDir = projectDir
    }
