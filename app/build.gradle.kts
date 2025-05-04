import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
    id("kotlin-kapt")
    id("kotlin-parcelize")
    alias(libs.plugins.hilt.gradle)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.thesun.drinksapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.thesun.drinksapp"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        buildConfigField("String", "STRIPE_PUBLIC_KEY", "\"${getPropertyFromLocal("PUBLIC_KEY")}\"")
        buildConfigField("String", "STRIPE_SECRET_KEY", "\"${getPropertyFromLocal("SECRET_KEY")}\"")

    }
    hilt {
        enableAggregatingTask = false
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}
fun getPropertyFromLocal(propertyName: String): String {
    val props = Properties()
    file("../local.properties").inputStream().use { stream ->
        props.load(stream)
    }
    return props.getProperty(propertyName) ?: ""
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation ("androidx.core:core-splashscreen:1.0.1")
    implementation (libs.material)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)
    implementation(libs.play.services.auth)
    implementation (libs.firebase.storage.ktx)
    // Hilt Dependency Injection
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Arch Components
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    //Momo SDK
    implementation("com.github.momo-wallet:mobile-sdk:1.0.7") {
        exclude(group = "com.android.support")
    }
    //Stripe SDK
    implementation("com.stripe:stripe-android:21.12.0")
    implementation("com.stripe:financial-connections:21.12.0")

    //Coil
    implementation(libs.coil.compose)
    // Accompanist Pager
    implementation(libs.accompanist.pager.v0320)
    implementation(libs.accompanist.pager.indicators)

    //Gson
    implementation(libs.gson)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}