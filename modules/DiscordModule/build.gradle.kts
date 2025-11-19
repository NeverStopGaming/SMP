import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

group = "net.derfarmer.moduleloader"
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
    shadow("net.dv8tion:JDA:6.1.1")
}
