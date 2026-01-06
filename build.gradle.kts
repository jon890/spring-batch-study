plugins {
    id("java")
    id("org.springframework.boot") version "4.0.1"
    id("io.spring.dependency-management") version "1.1.7"
    id("application")
    kotlin("jvm")
}

group = "com.system.batch"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Batch Starter (Spring Boot 4의 모듈화로 인해 batch 모듈 명시적 추가 필요)
    implementation("org.springframework.boot:spring-boot-starter-batch")
    implementation("org.springframework.batch:spring-batch-core")
    implementation("org.springframework.batch:spring-batch-infrastructure")
    
    // JDBC Starter (PlatformTransactionManager 자동 구성 및 JobRepository를 위한 JDBC 지원)
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    
    implementation("org.springframework.boot:spring-boot-starter-json")
    implementation("tools.jackson.datatype:jackson-datatype-jsr310:3.0.0-rc2")
    runtimeOnly("com.h2database:h2")
    
    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.batch:spring-batch-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation(kotlin("stdlib-jdk8"))
}

tasks.test {
    useJUnitPlatform()
}