dependencyResolutionManagement {

    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/") { name = "papermc-repo" }
        maven("https://oss.sonatype.org/content/groups/public/") { name = "sonatype" }
    }

    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "buildSrc"