plugins {
    id("kotlin")
    id("maven-publish")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.devtools.ksp:symbol-processing-api:${rootProject.extra["kspVersion"]}")
    implementation("com.squareup:kotlinpoet:1.10.2")
    implementation(project(":init-annotation"))
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
            }
        }
    }
}
