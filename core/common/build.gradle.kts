plugins {
    alias(libs.plugins.kotlin.jvm)
    id("java-test-fixtures")
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)

    testFixturesImplementation(libs.kotlinx.coroutines.core)
    testFixturesImplementation(libs.kotlinx.coroutines.test)
}