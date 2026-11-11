# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

# ============================================================
# WorkManager + Room (THIS FIXES YOUR CRASH)
# ============================================================
-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.InputMerger
-keep class androidx.work.impl.WorkDatabase
-keep class androidx.work.impl.WorkDatabase_Impl
-keep class androidx.work.** { *; }

-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# ============================================================
# AndroidX Startup
# ============================================================
-keep class androidx.startup.InitializationProvider
-keep class * extends androidx.startup.Initializer

# ============================================================
# Your existing rules (Firestore + kotlinx.serialization)
# ============================================================

# Keep data classes used with Firestore's reflection-based deserialization
-keepclassmembers class com.dmag.carscape.data.model.** {
    <fields>;
    <init>(...);
}
-keep class com.dmag.carscape.data.model.** { *; }

# Firestore/Firebase general safety
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn com.google.firebase.**

# kotlinx.serialization (used for local level parsing)
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclasseswithmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keep,includedescriptorclasses class com.dmag.carscape.**$$serializer { *; }
-keepclassmembers class com.dmag.carscape.** { *** Companion; }

# Hilt / Dagger
-dontwarn dagger.hilt.**