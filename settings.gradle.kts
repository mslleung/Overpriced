pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
rootProject.name = "Overpriced"
include(":presentation")
include(":application")
include(":domain")
include(":infrastructure")
include(":shared")
