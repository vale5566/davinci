package de.valeapps.davinci;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.List;

public class Website {

    private static final String SERVICE_ACTION = "android.support.customtabs.action.CustomTabsService";
    private static final String CHROME_PACKAGE = "com.android.chrome";

    public static void startWebsite(Context context) {

        Uri websiteUri = Uri.parse("http://davinci-gesamtschule.de/");

        CustomTabsIntent.Builder customtabintent = new CustomTabsIntent.Builder();
        customtabintent.setToolbarColor(Color.parseColor("#3F51B5"));
        customtabintent.setShowTitle(true);
        customtabintent.enableUrlBarHiding();
        if (isChromeCustomTabsSupported(context)) {
            customtabintent.build().intent.setPackage(CHROME_PACKAGE);
        } else {
            Toast.makeText(context, "Chrome nicht installiert.", Toast.LENGTH_LONG).show();
        }
        customtabintent.build().launchUrl(context, websiteUri);
    }



    private static boolean isChromeCustomTabsSupported(@NonNull final Context context) {
        Intent serviceIntent = new Intent(SERVICE_ACTION);
        serviceIntent.setPackage(CHROME_PACKAGE);
        List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentServices(serviceIntent, 0);
        return !(resolveInfos == null || resolveInfos.isEmpty());
    }
}
