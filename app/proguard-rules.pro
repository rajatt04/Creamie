# Creamie ProGuard Rules

# ==================== Kotlin ====================
-dontwarn kotlin.**
-keepclassmembers class kotlin.Metadata { *; }

# ==================== Coroutines ====================
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# ==================== Retrofit + OkHttp ====================
-keepattributes Signature
-keepattributes Exceptions
-keepattributes *Annotation*

# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep class okio.** { *; }

# ==================== Gson ====================
-keep class com.google.gson.** { *; }
-keep class com.google.gson.stream.** { *; }
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep data classes used for Gson serialization
-keep class com.rajatt7z.creamie.data.remote.dto.** { *; }

# ==================== Room ====================
-keep class * extends androidx.room.RoomDatabase
-keepclassmembers class * extends androidx.room.RoomDatabase {
    abstract <methods>;
}
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao interface *

# ==================== Hilt / Dagger ====================
-dontwarn dagger.**
-keep class dagger.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.internal.GeneratedComponent

# ==================== Coil ====================
-dontwarn coil.**
-keep class coil.** { *; }

# ==================== Palette ====================
-keep class androidx.palette.** { *; }

# ==================== WorkManager ====================
-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.ListenableWorker
-keepclassmembers class * extends androidx.work.ListenableWorker {
    public <init>(...);
}

# ==================== Glance ====================
-keep class * extends androidx.glance.appwidget.GlanceAppWidget

# ==================== R8 Compatibility ====================
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ==================== Compose ====================
# Keep Compose stability annotations if using @Stable/@Immutable
-keep @androidx.compose.runtime.Immutable class *
-keep @androidx.compose.runtime.Stable class *