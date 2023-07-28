plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.google.devtools.ksp") version "1.8.20-1.0.11"
    id("auto-register")
}

/** InitTask Start */
autoregister {
    registerInfo = listOf(
        mapOf(
            "scanInterface" to "me.wcy.init.annotation.ModuleTaskRegister",
            "codeInsertToClassName" to "me.wcy.init.api.FinalTaskRegister",
            "codeInsertToMethodName" to "init",
            "registerMethodName" to "register",
            "include" to listOf("me/wcy/init/apt/taskregister/.*")
        )
    )
}

kapt {
    arguments {
        arg("moduleName", project.name)
    }
}

ksp {
    arg("moduleName", project.name)
}

/** InitTask End */

android {
    namespace = "me.wcy.init.sample"
    compileSdk = rootProject.extra["compileSdk"] as Int

    defaultConfig {
        applicationId = "me.wcy.init.sample"
        minSdk = rootProject.extra["minSdk"] as Int
        targetSdk = rootProject.extra["targetSdk"] as Int
        versionCode = 1
        versionName = "1.0"
    }

    android.buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                file("proguard-rules.pro")
            )
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    // kapt(project(":init-compiler"))
    ksp(project(":init-compiler-ksp"))
    implementation(project(":init-api"))
    implementation(project(":sample-lib"))
}