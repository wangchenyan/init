plugins {
    id("kotlin")
    `maven-publish`
    //id("com.github.dcendents.android-maven")
}

dependencies {
    annotationProcessor("com.google.auto.service:auto-service:1.0")
    implementation("com.squareup:kotlinpoet:1.10.2")
    implementation(project(":init-annotation"))
}

// Because the components are created only during the afterEvaluate phase, you must
// configure your publications using the afterEvaluate() lifecycle method.
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
