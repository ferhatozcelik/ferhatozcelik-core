import java.io.File
import java.util.Properties

plugins {
    `maven-publish`
    signing
}

val isSnapshot = false

val secretPropertiesFile = File(rootProject.projectDir, "secret.properties")
val secretProperties = Properties()
if (secretPropertiesFile.exists()) {
    secretProperties.apply {
        load(secretPropertiesFile.inputStream())
    }
    System.getProperties().putAll(secretProperties)
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

val pomName = "Ads"
val pomDescription = "Easy ads integration for Android"
val libVersionName = versionProperties.getProperty("ads") as String  + if (isSnapshot) snapshotIdentifier else ""
val artifactName = "ads"

val group = secretProperties.getProperty("GROUP") as String

val projectUrl = secretProperties.getProperty("POM_URL") as String

val licenseName = secretProperties.getProperty("LICENCE_NAME") as String
val licenseUrl = secretProperties.getProperty("LICENCE_URL") as String

val developerId = secretProperties.getProperty("DEVELOPER_ID") as String
val developerName = secretProperties.getProperty("DEVELOPER_NAME") as String

val scmConnection = secretProperties.getProperty("SCM_CONNECTION") as String
val scmDevConnection = secretProperties.getProperty("SCM_DEV_CONNECTION") as String

val repositoryUsername = secretProperties.getProperty("mavenCentralUsername") as String
val repositoryPassword = secretProperties.getProperty("mavenCentralPassword") as String

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
    useInMemoryPgpKeys(
        secretProperties.getProperty("signing.keyId") as String,
        secretProperties.getProperty("signing.secretKeyRingFile") as String,
        secretProperties.getProperty("signing.password") as String,
    )
}
