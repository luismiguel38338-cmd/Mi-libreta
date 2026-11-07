# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Room Database Preservation Rules
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-dontwarn androidx.room.paging.**
-keepclassmembers class * {
    @androidx.room.TypeConverter *;
}

# Preserve Data Models
-keep class com.example.data.model.** { *; }
-keepclassmembers class com.example.data.model.** { *; }
-keepclassmembers enum com.example.data.model.** { *; }

# Preserve SQLite and Coroutines
-dontwarn kotlinx.coroutines.**
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
