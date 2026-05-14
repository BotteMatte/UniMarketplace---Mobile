// Carica local.properties
val localProperties = java.util.Properties()
val localFile = File(rootDir, "local.properties")
if (localFile.exists()) {
    localProperties.load(localFile.inputStream())
}

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "UniMarketplace"
include(":app")
 