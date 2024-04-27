rootProject.name = "tj-spring-boilerplate"

include(
    "core-api",
    "domain",
    "storage",
    "modules:pagination",
    "modules:lock"
)

pluginManagement {
    val springBootVersion: String by settings
    val ktlintVersion: String by settings
    val springDependencyManagementVersion: String by settings
    val kotlinVersion: String by settings

    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "org.springframework.boot" -> useVersion(springBootVersion)
                "io.spring.dependency-management" -> useVersion(springDependencyManagementVersion)
                "org.jlleitschuh.gradle.ktlint" -> useVersion(ktlintVersion)
                "org.jetbrains.kotlin.plugin.jpa" -> useVersion(kotlinVersion)
                "org.jetbrains.kotlin.jvm" -> useVersion(kotlinVersion)
                "org.jetbrains.kotlin.kapt" -> useVersion(kotlinVersion)
                "org.jetbrains.kotlin.plugin.spring" -> useVersion(kotlinVersion)
            }
        }
    }
}
