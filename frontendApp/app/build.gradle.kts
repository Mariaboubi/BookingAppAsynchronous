plugins {
    id("com.android.application")
}

android {
    namespace = "gr.aueb.bookingapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "gr.aueb.bookingapp"
        minSdk = 26
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
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    implementation("com.googlecode.json-simple:json-simple:1.1.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("org.slf4j:slf4j-api:1.7.30")
    androidTestImplementation("androidx.test.ext:junit:4.13.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

}

configurations.all {
    resolutionStrategy.dependencySubstitution {
        substitute(module("org.hamcrest:hamcrest-core")).using(module("junit:junit:4.13.2"))
    }
}