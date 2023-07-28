// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "7.4.2" apply false
    id("com.android.library") version "7.4.2" apply false
    id("org.jetbrains.kotlin.android") version "1.8.20" apply false
    id("com.google.devtools.ksp") version "1.8.20-1.0.11" apply false
}

buildscript {
    extra.apply {
        set("compileSdk", 33)
        set("minSdk", 14)
        set("targetSdk", 33)
    }
    dependencies {
        classpath("com.github.wangchenyan:AutoRegister:1.4.3-beta02")
    }
}
