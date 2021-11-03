plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-parcelize")
}

android {
    compileSdk = 30
    buildFeatures.viewBinding = true

    defaultConfig {
        applicationId = "im.bnw.android"
        minSdk = 24
        targetSdk = 30
        versionCode = 2
        versionName = "1.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "DB_NAME", "\"bnwDb\"")
        buildConfigField("int", "DB_VERSION", "1")

        buildConfigField("String", "BASE_URL", "\"https://bnw.im/api/\"")
        buildConfigField("String", "LOCAL_LINK", "\"app://im.bnw.android/\"")
        buildConfigField("String", "POST_PATH_SEGMENT", "\"p\"")
        buildConfigField("String", "USER_PATH_SEGMENT", "\"u\"")
        buildConfigField("String", "USER_AVA_URL", "\"https://cn.bnw.im/u/%s/avatar\"")
        buildConfigField("String", "USER_AVA_THUMB_URL", "\"https://cn.bnw.im/u/%s/avatar/thumb\"")
        buildConfigField("String", "DONATE_URL", "\"https://yoomoney.ru/to/41001208815080\"")
    }

    lintOptions {
        isWarningsAsErrors = true
        disable("NullSafeMutableLiveData")
    }

    buildTypes {
        val projectName = "Bnw"
        val projectNameDebug = "$projectName debug"

        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            resValue("string", "app_name", projectName)
        }

        getByName("debug") {
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            resValue("string", "app_name", projectNameDebug)
        }

        setProperty(
            "archivesBaseName",
            "$projectName-v${defaultConfig.versionName}(${defaultConfig.versionCode})"
        )
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.21")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.1")

    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.activity:activity-ktx:1.2.4")
    implementation("androidx.fragment:fragment-ktx:1.3.6")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.datastore:datastore-preferences:1.0.0-rc02")

    implementation("com.google.android.material:material:1.4.0")

    val lifecycleVersion = "2.3.1"
    implementation("androidx.lifecycle:lifecycle-common-java8:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")

    val modoVersion = "0.6.1"
    implementation("com.github.terrakok:modo:$modoVersion")
    implementation("com.github.terrakok:modo-render-android-fm:$modoVersion")

    implementation("com.hannesdorfmann:adapterdelegates4-kotlin-dsl-viewbinding:4.3.0")

    implementation("me.zhanghai.android.materialprogressbar:library:1.6.1")

    val glideVersion = "4.12.0"
    implementation("com.github.bumptech.glide:glide:$glideVersion")
    kapt("com.github.bumptech.glide:compiler:$glideVersion")

    val markwonVersion = "4.6.2"
    implementation("io.noties.markwon:core:$markwonVersion")
    implementation("io.noties.markwon:linkify:$markwonVersion")

    val retrofitVersion = "2.9.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofitVersion")
    implementation("com.squareup.okhttp3:okhttp:4.2.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.2.0")

    val moshiVersion = "1.12.0"
    implementation("com.squareup.moshi:moshi-kotlin:$moshiVersion")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:$moshiVersion")

    implementation("com.jakewharton.timber:timber:4.7.1")

    implementation("com.github.YarikSOffice:lingver:1.3.0")

    val daggerVersion = "2.35.1"
    api("com.google.dagger:dagger:$daggerVersion")
    api("com.google.dagger:dagger-android-support:$daggerVersion")
    kapt("com.google.dagger:dagger-compiler:$daggerVersion")
    implementation("com.google.dagger:dagger-android:$daggerVersion")
    kapt("com.google.dagger:dagger-android-processor:$daggerVersion")

    val roomVersion = "2.3.0"
    implementation("androidx.room:room-runtime:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")

    implementation("com.github.stfalcon:stfalcon-imageviewer:1.0.1")

    implementation("com.github.xabaras:RecyclerViewSwipeDecorator:1.3")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}
