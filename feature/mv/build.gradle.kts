plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.video"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    viewBinding {
        enable = true
    }

}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    //导入base
    implementation(project(":core:base"))
    implementation(project(":core:net"))
    implementation(project(":core:therouter"))
    implementation(project(":core:model"))
    implementation(project(":core:util"))

    //viewmodel
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.androidx.fragment.ktx)

    //协称
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    //导入therouter
    implementation(libs.therouter.router)
    ksp(libs.therouter.apt)

    //导入的okhttp和gson的相关依赖
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.gson)

    implementation (libs.glide)

    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // GSYVideoPlayer 视频播放器 (v9.0.0 — 已拆分为多模块)
    implementation("com.github.CarGuo.GSYVideoPlayer:gsyVideoPlayer-java:v9.0.0-release-jitpack")
    implementation("com.github.CarGuo.GSYVideoPlayer:GSYVideoPlayer-exo2:v9.0.0-release-jitpack")
    implementation("com.github.CarGuo.GSYVideoPlayer:gsyVideoPlayer-arm64:v9.0.0-release-jitpack")
    implementation("com.google.android.flexbox:flexbox:3.0.0")
}