import org.gradle.api.tasks.compile.JavaCompile

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "idea")

    tasks.withType<JavaCompile> {
        options.compilerArgs.addAll(listOf("--release", "17"))
    }
}
