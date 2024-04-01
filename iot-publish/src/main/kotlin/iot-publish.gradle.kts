import java.io.File
import java.util.Properties

plugins {
    `maven-publish`
    signing
}

val isSnapshot = false

val mavenPropertiesFile = File(rootProject.projectDir, "maven.properties")
val mavenProperties = Properties()
if (mavenPropertiesFile.exists()) {
    mavenProperties.apply {
        load(mavenPropertiesFile.inputStream())
    }
    System.getProperties().putAll(mavenProperties)
}

val versionPropertiesFile = File(rootProject.projectDir, "version.properties")
val versionProperties = Properties()
if (versionPropertiesFile.exists()) {
    versionProperties.apply {
        load(versionPropertiesFile.inputStream())
    }
    System.getProperties().putAll(versionProperties)
}

val mavenCentralUrl = uri("https://s01.oss.sonatype.org/content/repositories/releases/")
val mavenSnapshotUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
val snapshotIdentifier = "-SNAPSHOT"

val pomName = "IOT"
val pomDescription = "IOT library for Android Things"
val libVersionName = versionProperties.getProperty("iot") as String + if (isSnapshot) snapshotIdentifier else ""
val artifactName = "iot"

val group = mavenProperties.getProperty("GROUP") as String

val projectUrl = mavenProperties.getProperty("POM_URL") as String

val licenseName = mavenProperties.getProperty("LICENCE_NAME") as String
val licenseUrl = mavenProperties.getProperty("LICENCE_URL") as String

val developerId = mavenProperties.getProperty("DEVELOPER_ID") as String
val developerName = mavenProperties.getProperty("DEVELOPER_NAME") as String

val scmConnection = mavenProperties.getProperty("SCM_CONNECTION") as String
val scmDevConnection = mavenProperties.getProperty("SCM_DEV_CONNECTION") as String

// Load the repository credentials from the secret properties
val repositoryUsername = System.getenv("MAVEN_CENTRAL_USERNAME") as String
val repositoryPassword = System.getenv("MAVEN_CENTRAL_PASSWORD") as String

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = group
            artifactId = artifactName
            version = libVersionName
            afterEvaluate {
                from(components["release"])
            }
            pom {
                name.set(pomName)
                description.set(pomDescription)
                url.set(projectUrl)
                licenses {
                    license {
                        name.set(licenseName)
                        url.set(licenseUrl)
                    }
                }
                developers {
                    developer {
                        id.set(developerId)
                        name.set(developerName)
                    }
                }
                scm {
                    connection.set(scmConnection)
                    developerConnection.set(scmDevConnection)
                    url.set(projectUrl)
                }
            }
        }
        repositories {
            maven {
                credentials {
                    username = repositoryUsername
                    password = repositoryPassword
                }
                url = when {
                    libVersionName.endsWith(snapshotIdentifier) -> {
                        mavenSnapshotUrl
                    }
                    else -> {
                        mavenCentralUrl
                    }
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["release"].name)
}
