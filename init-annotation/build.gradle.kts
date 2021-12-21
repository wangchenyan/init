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
                from(components["java"])
            }
        }
    }
}