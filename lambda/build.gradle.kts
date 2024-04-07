plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

dependencies {
    implementation(platform("software.amazon.awssdk:bom:2.25.24"))
    implementation("software.amazon.awssdk:aws-crt-client")
    implementation("software.amazon.awssdk:dynamodb")
    implementation("software.amazon.awssdk:s3")

    implementation("com.amazonaws:aws-lambda-java-core:1.2.3")
    implementation("com.amazonaws:aws-lambda-java-events:3.11.4")
}

application {
    mainClass = "eu.luminis.debugging.report.Handler"
}

tasks.shadowJar {
//    mergeServiceFiles()
//    minimize()
}