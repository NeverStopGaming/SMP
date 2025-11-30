import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

group = "net.derfarmer.utilmodule"
version = "unspecified"

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

tasks.named<ShadowJar>("shadowJar") {
    configurations = listOf(project.configurations.shadow.get())
}

tasks.build { dependsOn("shadowJar") }

repositories {
    maven("https://maven.maxhenkel.de/repository/public")
}

dependencies {
    testImplementation(kotlin("test"))
    shadow("de.maxhenkel.voicechat:voicechat-api:2.6.0")
}
