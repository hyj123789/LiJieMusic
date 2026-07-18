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
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        //导入GSYVideoPlayer
//        mavenCentral()
//        maven("https://jitpack.io")
//        maven("https://maven.aliyun.com/repository/public") // 阿里云加速
//        mavenCentral()

        maven(url = "https://jitpack.io")

        // 下面是你原有的其他仓库
        maven(url = "https://maven.aliyun.com/repository/public")
        google()
        mavenCentral()
    }
}

rootProject.name = "LiJieMusic"
include(":app")
include(":core:net")
include(":core:base")
include(":core:util")
include(":feature:player")
include(":core:therouter")
include(":feature:home")
include(":feature:login")
include(":feature:search")
include(":feature:searchpage")
include(":feature:mv")
include(":feature:profile")
include(":core:model")
