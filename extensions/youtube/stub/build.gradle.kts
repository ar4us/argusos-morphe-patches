import com.android.build.api.dsl.LibraryExtension

plugins {
    alias(libs.plugins.android.library)
}

configure<LibraryExtension> {
    namespace = "app.argusos.extension"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
    }
}
