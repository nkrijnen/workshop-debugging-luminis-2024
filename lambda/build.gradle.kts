plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

tasks {
    shadowJar {
        transform(com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer::class.java)
    }
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")

    implementation(platform("software.amazon.awssdk:bom:2.25.24"))
    implementation("software.amazon.awssdk:aws-crt-client")
    implementation("software.amazon.awssdk:dynamodb")
    implementation("software.amazon.awssdk:s3")

    implementation("com.amazonaws:aws-lambda-java-core:1.2.3")
    implementation("com.amazonaws:aws-lambda-java-events:3.11.4")

    implementation("com.google.code.gson:gson:2.10.1")

    implementation("org.apache.logging.log4j:log4j-api:2.20.0")
    implementation("org.apache.logging.log4j:log4j-core:2.20.0")
    implementation("com.amazonaws:aws-lambda-java-log4j2:1.6.0")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.24.3")
}

application {
    mainClass = "eu.luminis.observability.report.Handler"
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}

tasks.test {
    useJUnitPlatform()
}
