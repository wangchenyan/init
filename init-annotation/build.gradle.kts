plugins {
    id("kotlin")
    `maven-publish`
    //id("com.github.dcendents.android-maven")
}

dependencies {
}

// Because the components are created only during the afterEvaluate phase, you must
// configure your publications using the afterEvaluate() lifecycle method.
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = "com.github.wangchenyan.init"
                artifactId = "init-annotation"
                version = "0.1"

                from(components["java"])
            }
        }
    }
}