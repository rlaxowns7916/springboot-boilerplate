dependencies {
    implementation(project(":common:profile"))

    implementation(project(":modules:lock"))
    implementation(project(":modules:local-cache"))
    implementation(project(":modules:pagination"))

    implementation(project(":storage:rdb"))
    implementation(project(":storage:redis"))
}
