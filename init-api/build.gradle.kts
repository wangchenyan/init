plugins {
    id("com.android.library")
    id("kotlin-android")
    `maven-publish`
    //id("com.github.dcendents.android-maven")
}

android {
    compileSdk = rootProject.extra["compileSdk"] as Int

    defaultConfig {
        minSdk = rootProject.extra["minSdk"] as Int
        targetSdk = rootProject.extra["targetSdk"] as Int
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")
    api(project(":init-annotation"))
}

// Because the components are created only during the afterEvaluate phase, you must
// configure your publications using the afterEvaluate() lifecycle method.
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = "com.github.wangchenyan.init"
                artifactId = "init-api"
                version = "0.1"

                from(components["release"])
            }
        }
    }
}