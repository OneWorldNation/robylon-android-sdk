plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}
apply {
    from("$rootDir/ktlint.gradle")
}
android {
    namespace = "${Deps.SDK_PACKAGE_NAME}.android"
    compileSdk = Deps.Android.compileSdk
    buildToolsVersion= Deps.Android.buildToolsVersion

    defaultConfig {
        applicationId = "${Deps.SDK_PACKAGE_NAME}.android"
        minSdk =Deps.Android.minSdk
        targetSdk = Deps.Android.targetSdk
        versionCode = Deps.APP_VERSION_CODE
        versionName = Deps.APP_VERSION_NAME
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    signingConfigs {
        getByName("debug") {
            storeFile = file("../debug.jks")
            storePassword = "debugdebug"
            keyAlias = "debug"
            keyPassword = "debugdebug"
        }
        create("release") {
            storeFile = file("../debug.jks")
            storePassword = "debugdebug"
            keyAlias = "debug"
            keyPassword = "debugdebug"
        }
    }
    buildFeatures {
        dataBinding = true
        buildConfig = true
    }

    defaultConfig{
        buildConfigField("String", "OWN_CONFIG_BASE_URL_STAGE", "\"${Deps.OWN_CONFIG_BASE_URL_STAGE}\"")
        buildConfigField("String", "OWN_API_KEY", "\"${Deps.OWN_API_KEY}\"")
    }

    buildTypes {
        getByName("debug") {
            versionNameSuffix = "(d)"
            isDebuggable = true
            isCrunchPngs = false
            isMinifyEnabled = false
        }
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isDebuggable = false
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))

    implementation("org.jetbrains.kotlin:kotlin-stdlib:${Deps.JetBrains.Kotlin.VERSION}")
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("com.google.android.material:material:${Deps.material}")
    implementation("androidx.constraintlayout:constraintlayout:2.1.0")
    implementation("com.google.firebase:firebase-crashlytics:18.2.1")

    if (Deps.RUN_LIB) {
        implementation(project(":sdkLibrary"))
        println("Using shared library :sdkLibrary")
    } else {
        val dependency = "${Deps.Publication.GROUP}:${Deps.Publication.PUBLISH_ARTIFACT_ID}:${Deps.Publication.VERSION}"
        implementation(dependency)
        println("Using remote library - $dependency")
    }



    implementation("com.github.bumptech.glide:glide:4.12.0")
    kapt("com.github.bumptech.glide:compiler:4.12.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.7")
    implementation("com.github.angads25:toggle:1.1.0")
    implementation ("io.noties.markwon:core:4.6.2")
    implementation ("io.noties.markwon:html:4.6.2")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}
tasks.register("testClasses")