pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ferhatozcelik-core"
includeBuild("core-publish")
includeBuild("ads-publish")
includeBuild("firebase-publish")
includeBuild("iot-publish")
include(":app")
include(":core")
include(":ads")
include(":firebase")
include(":iot")
