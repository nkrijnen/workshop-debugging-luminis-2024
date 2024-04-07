plugins {
    java
    application
}

dependencies {
    implementation("software.amazon.awscdk:aws-cdk-lib:2.135.0")
    implementation("software.constructs:constructs:10.3.0")
}

application {
    mainClass = "stack.LambdaApp"
}