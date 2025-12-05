import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

group = "net.derfarmer.webmodule"
version = "unspecified"

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

tasks.named<ShadowJar>("shadowJar") {
    configurations = listOf(project.configurations.shadow.get())
}

tasks.build { dependsOn("shadowJar") }

dependencies {
    testImplementation(kotlin("test"))
    shadow("io.javalin:javalin:6.7.0")
}
