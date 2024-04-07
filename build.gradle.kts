group = "eu.luminis.debugging"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        mavenCentral()
    }
    group = rootProject.group
    version = rootProject.version
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "idea")

    tasks.withType<JavaCompile> {
        options.compilerArgs.addAll(listOf("--release", "17"))
    }
}
