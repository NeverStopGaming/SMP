plugins {
    id("buildsrc.convention.kotlin-jvm")
}

subprojects {
    apply { plugin("buildsrc.convention.kotlin-jvm") }

    dependencies {
        compileOnly(project(":plugin"))
        if (project.name != "PlayerSystem") {
            compileOnly(project(":modules:PlayerSystem"))
        }
    }
}