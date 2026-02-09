plugins {
  id("org.springframework.boot") version "3.5.8"
  id("io.spring.dependency-management") version "1.1.7"
  java
  eclipse
}

group = "com.example"
version = "1.0.0"

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(17))
  }
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-graphql")
  implementation("org.springframework.boot:spring-boot-starter-web")

  // DB (PostgreSQL)
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  runtimeOnly("org.postgresql:postgresql")

  // Password hashing (BCrypt)
  implementation("org.springframework.security:spring-security-crypto")

  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.springframework.graphql:spring-graphql-test")
}

tasks.withType<JavaCompile>().configureEach {
  options.encoding = "UTF-8"
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
}

// ---- Test split: unit/slice vs integration (JUnit5 @Tag 기반) ----
// - 기본 test: integration 태그 제외
// - integrationTest: integration 태그만 실행

tasks.named<Test>("test") {
  useJUnitPlatform {
    excludeTags("integration")
  }
}

val integrationTest by tasks.registering(Test::class) {
  group = "verification"
  description = "Runs integration tests (tag: integration)."

  testClassesDirs = sourceSets.test.get().output.classesDirs
  classpath = sourceSets.test.get().runtimeClasspath

  useJUnitPlatform {
    includeTags("integration")
  }

  shouldRunAfter(tasks.named("test"))
}

tasks.named("check") {
  dependsOn(integrationTest)
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
  archiveFileName.set("app.jar")
}

