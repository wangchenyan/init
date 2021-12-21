plugins {
    id("kotlin")
    id("maven-publish")
}

dependencies {
}

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