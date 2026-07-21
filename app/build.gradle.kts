plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp)
}
apply(plugin = "therouter")

android {
    namespace = "com.example.lijiemusic"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }
    buildFeatures{
        viewBinding=true
    }

    defaultConfig {
        applicationId = "com.example.lijiemusic"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.media3.common.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.material)

    implementation(libs.media3.exoplayer)
    implementation(libs.media3.session)
    implementation(libs.media3.ui)

    // TheRouter
    implementation(libs.therouter.router)
    ksp(libs.therouter.apt)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    implementation (libs.glide)
    implementation(project(":core:base"))
    implementation(project(":core:net"))
    implementation(libs.okhttp)
    implementation(project(":core:util"))
    implementation(project(":core:therouter"))
    implementation(project(":core:model"))
    implementation(project(":feature:home"))
    implementation(project(":feature:login"))
    implementation(project(":feature:searchpage"))
    implementation(project(":feature:search"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:playlist"))
    implementation(project(":feature:mv"))
    implementation(project(":feature:player"))
    implementation(project(":feature:dynamics"))
    implementation(project(":feature:comment"))
}