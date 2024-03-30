plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    id("core-publish")
}

android {
    namespace = "com.ferhatozcelik.core"
    compileSdk = 34
    defaultConfig {
        minSdk = 21
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }


    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"

    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }

}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)

    api(platform(libs.compose.bom))
    api(libs.androidx.runtime.android)
    api(libs.androidx.lifecycle.runtime.compose)
    api(libs.androidx.compiler)
    api(libs.compose.ui)
    api(libs.compose.foundation)
    api(libs.compose.foundation.layout)

    implementation(libs.app.update.ktx)
    implementation(libs.gson)

}

