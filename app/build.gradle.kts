plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.utt.gymbros"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.utt.gymbros"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Retrofit
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    //Encrypted Shared Preferences
    implementation("androidx.security:security-crypto:1.1.0-alpha03")

    // Material Design
    implementation("com.google.android.material:material:1.12.0")

    //Alerter
    implementation("com.github.tapadoo:alerter:7.2.4")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}