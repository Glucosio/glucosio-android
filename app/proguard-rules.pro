-optimizationpasses 5
-dump class_files.txt
-printseeds seeds.txt
-printusage unused.txt
-printmapping mapping.txt
-optimizations !code/simplification/arithmetic,!field/*,!class/merging*/
-allowaccessmodification
-repackageclasses ''

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.MapActivity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

-keep public class org.apache.commons.io.**
-keep public class com.google.gson.**
-keep public class com.google.gson.** {public private protected *;}

##---------------Begin: proguard configuration for Gson ----------
-keepattributes *Annotation*,Signature
-keep class org.glucosio.android.ActivityMonitor.ClassMultiPoints.** { *; }
-keep public class org.glucosio.android.ActivityMonitor$ClassMultiPoints     { public protected *; }
-keep public class org.glucosio.android.ActivityMonitor$ClassMultiPoints$ClassPoints { public protected *; }
-keep public class org.glucosio.android.ActivityMonitor$ClassMultiPoints$ClassPoints$ClassPoint { public protected *; }
-keepclassmembers enum * { *; }

##---------------End: proguard configuration for Gson ----------



# MPAndoridChart
-keep class com.github.mikephil.charting.** { *; }

# RxAndroid
-dontwarn rx.internal.util.unsafe.**

# Realm
-keep class io.realm.annotations.RealmModule
-keep @io.realm.annotations.RealmModule class *
-keep class io.realm.internal.Keep
-keep @io.realm.internal.Keep class * { *; }
-dontwarn javax.**
-dontwarn io.realm.**

## AppCompat
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }

-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}

## Google Play Services
-dontnote com.google.vending.licensing.ILicensingService
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

## Instabug
-dontwarn org.apache.http.**
-dontwarn android.net.http.AndroidHttpClient
-dontwarn com.google.android.gms.**
-dontwarn com.android.volley.toolbox.**
-dontwarn com.instabug.**

## Smooch
-dontwarn okio.**
-keep class okio.**
-keep class com.google.gson.**


-keep class * extends java.util.ListResourceBundle {
    protected java.lang.Object[][] getContents();
}

# Keep SafeParcelable value, needed for reflection. This is required to support backwards
# compatibility of some classes.
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

# Keep the names of classes/members we need for client functionality.
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

# Needed for Parcelable/SafeParcelable Creators to not get stripped
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

#  Needed by google-api-client to keep generic types and @Key annotations accessed via reflection

-keepattributes Signature,RuntimeVisibleAnnotations,AnnotationDefault

-keepclassmembers class * {
   @com.google.api.client.util.Key <fields>;
}