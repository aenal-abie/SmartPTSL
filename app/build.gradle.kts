import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "smartgis.project.app.smartgis"
    compileSdk = 36

    defaultConfig {
        applicationId = "smartgis.project.app.smartgis"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    buildFeatures {
        buildConfig = true
    }

    flavorDimensions += "env"

    productFlavors {
        create("development") {
            dimension = "env"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"

            buildConfigField("String", "BASE_URL", "\"https://dev-api.com\"")
        }

        create("production") {
            dimension = "env"

            buildConfigField("String", "BASE_URL", "\"https://api.com\"")
        }
    }
}



dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Import the BoM for the Firebase platform
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    //google maps
    implementation(libs.play.services.maps)
    implementation(libs.android.maps.utils)

    //dbf
    implementation(libs.albfernandez.javadbf)
    implementation(libs.geodesy)
    //bentrok dengan geodesy
    //implementation(libs.jscience)
    implementation(libs.proj4j)

    implementation(libs.play.services.location)

}
