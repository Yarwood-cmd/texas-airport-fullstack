# Keep line numbers for debugging
-keepattributes SourceFile,LineNumberTable

# Retrofit
-keepattributes Signature
-keepattributes Exceptions

# Gson
-keepattributes *Annotation*
-keep class com.airport.android.model.** { *; }
