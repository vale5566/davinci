package de.valeapps.davinci;


import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class Website {

    public static void startWebsite(Context context) {

        Uri websiteUri = Uri.parse("http://davinci-gesamtschule.de/");

        CustomTabsIntent.Builder customtabintent = new CustomTabsIntent.Builder();
        customtabintent.setToolbarColor(Color.parseColor("#3F51B5"));
        customtabintent.setShowTitle(true);
        customtabintent.enableUrlBarHiding();
        if (isAppInstalled(context, "com.android.chrome")) {
            customtabintent.build().intent.setPackage("com.android.chrome");
        } else {
            Toast.makeText(context, "Chrome nicht installiert.", Toast.LENGTH_LONG).show();
        }
        customtabintent.build().launchUrl(context, websiteUri);
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
