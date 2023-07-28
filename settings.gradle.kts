pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "service-loader"
include(":init-compiler")
include(":init-compiler-ksp")
include(":init-api")
include(":init-annotation")
include(":sample")
include(":sample-lib")
