# ============================================================================
# R8 / ProGuard rules for KFU Portal
# ============================================================================

# --- General Kotlin / Java ---
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keepattributes EnclosingMethod, InnerClasses
-keepattributes RuntimeVisibleAnnotations, RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations, RuntimeInvisibleParameterAnnotations
-keepattributes RuntimeVisibleTypeAnnotations, RuntimeInvisibleTypeAnnotations

# Keep Kotlin metadata (needed by reflection-heavy libraries)
-keep class kotlin.Metadata { *; }

# --- App data classes (parsed manually via org.json.JSONObject) ---
-keep class com.example.data.** { *; }

# --- OkHttp (official) ---
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn org.conscrypt.**
-keep class okhttp3.** { *; }
-keep class okio.** { *; }
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# --- Retrofit (official) ---
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# --- Moshi (official for codegen) ---
-keep class **JsonAdapter { <init>(...); *; }
-keepclassmembers @com.squareup.moshi.JsonClass class * {
    *;
}
-keep @com.squareup.moshi.JsonClass class * { *; }
-dontwarn org.jetbrains.annotations.**

# --- Coil ---
-dontwarn coil.**
-keep class coil.** { *; }

# --- Kotlin Coroutines ---
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# --- Compose (mostly handled by compiler, keep lambdas) ---
-keepclassmembers class * {
    @androidx.compose.runtime.* <methods>;
}
-dontwarn androidx.compose.**

# --- Keep enum values (used in switch statements via reflection-safe patterns) ---
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# --- WebView JS interface (if used) ---
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# --- Keep BuildConfig ---
-keep class com.example.BuildConfig { *; }

# --- Room (generated code is usually safe, but keep entities just in case) ---
-keep class * extends androidx.room.RoomDatabase { *; }
-dontwarn androidx.room.paging.**

# --- Lifecycle / ViewModel ---
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keep class * extends androidx.lifecycle.AndroidViewModel { *; }
