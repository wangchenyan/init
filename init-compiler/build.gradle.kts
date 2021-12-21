plugins {
    id("kotlin")
    id("maven-publish")
}

dependencies {
    annotationProcessor("com.google.auto.service:auto-service:1.0")
    implementation("com.squareup:kotlinpoet:1.10.2")
    implementation(project(":init-annotation"))
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = "com.github.wangchenyan.init"
                artifactId = "init-compiler"
                version = "0.1"

                from(components["java"])
            }
        }
    }
}
