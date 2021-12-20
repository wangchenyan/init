plugins {
    id("kotlin")
    //id("com.github.dcendents.android-maven")
}

dependencies {
    annotationProcessor("com.google.auto.service:auto-service:1.0")
    implementation("com.squareup:kotlinpoet:1.10.2")
    implementation(project(":init-annotation"))
}