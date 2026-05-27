dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-security-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("tools.jackson.module:jackson-module-kotlin")
    implementation("io.awspring.cloud:spring-cloud-aws-starter-sns")
    implementation("io.awspring.cloud:spring-cloud-aws-starter-sqs")
    implementation(platform(libs.aws))
    implementation(libs.springdoc)
    implementation(libs.bundles.telemetry)

    dev("org.testcontainers:testcontainers-oracle-free")

    runtimeOnly("com.oracle.database.jdbc:ojdbc11")

    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.security:spring-security-test")

}
