import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
    id("com.google.gms.google-services") version "4.4.3" apply false

}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            linkerOpts("-lsqlite3")
        }

        // Remove cinterop approach due to androidx.core dependency issue
        // Using platform-specific implementation instead
    }


    
    sourceSets {
        named { it.lowercase().startsWith("ios") }.configureEach {
            languageSettings {
                optIn("kotlinx.cinterop.ExperimentalForeignApi")
            }
        }
    }

    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        compilations.configureEach {
            compilerOptions.configure {
                freeCompilerArgs.addAll(listOf(
                    "-Xruntime-logs=gc=info",
                    "-Xdisable-phases=VerifyBitcode"
                ))
            }
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(project.dependencies.platform("com.google.firebase:firebase-bom:34.2.0"))
            implementation("com.google.firebase:firebase-analytics")
            implementation(libs.onesignal.android)
            implementation(libs.ktor.client.okhttp)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation(libs.androidx.navigation.compose)

            implementation(libs.koin.compose)
            implementation(libs.koin.compose.vm)
            implementation(libs.koin.compose.nav)
            implementation(libs.koin.core)

            implementation(libs.sky.dove.bottom.sheet)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.logging)
            implementation(libs.ktor.serialization.kotlinx.json)

            implementation("org.jetbrains.compose.material:material-icons-extended:1.7.3")

            implementation(libs.purchases.core)
            implementation(libs.purchases.datetime)   // Optional
            implementation(libs.purchases.either)     // Optional
            implementation(libs.purchases.result)

            implementation(libs.androidx.datastore.core)
            implementation(libs.androidx.datastore.preferences)

            implementation("com.gyanoba.inspektor:inspektor:0.3.9")


        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.keak.aishou"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.keak.aishou"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 3
        versionName = "1.1.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    lint {
        disable += setOf(
            "NullSafeMutableLiveData",
            "RememberInComposition",
            "UnnecessaryComposedModifier",
            "ComposableNaming",
            "ModifierParameter"
        )
        // Ignore lint errors related to Kotlin version incompatibility
        abortOnError = false
        checkReleaseBuilds = false
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

