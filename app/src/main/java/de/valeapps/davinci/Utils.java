package de.valeapps.davinci;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import de.valeapps.davinci.score.ScoreActivity;
import de.valeapps.davinci.substitutetable.SubstituteTableActivity;
import de.valeapps.davinci.teacher.Team;
import de.valeapps.davinci.timetable.TimeTableActivity;
import de.valeapps.davinci.yearbook.YearbookActivity;

public class Utils {

    public static String TAG = "DaVinci";

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        } else {
            Log.e(Utils.TAG, "ERROR in Utils.");
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void NavigationItemSelected(Context context, int id) {
        switch (id) {
            case R.id.startseite:
                context.startActivity(new Intent(context, MainActivity.class));
                break;
            case R.id.stundenplan:
                context.startActivity(new Intent(context, TimeTableActivity.class));
                break;
            case R.id.vertretungsplan:
                context.startActivity(new Intent(context, SubstituteTableActivity.class));
                break;
            case R.id.noten:
                context.startActivity(new Intent(context, ScoreActivity.class));
                break;
            case R.id.team:
                context.startActivity(new Intent(context, Team.class));
                break;
            case R.id.website:
                WebsiteActivity.startWebsite(context);
                break;
            case R.id.jahrbuch:
                context.startActivity(new Intent(context, YearbookActivity.class));
                break;
        }
    }

    public static void OptionsItemSelected(Context context, int id) {
        switch (id) {
            case R.id.action_settings:
                context.startActivity(new Intent(context, SettingsActivity.class));
                break;
            case R.id.mail:
                String to = "vale@valeapps.de";
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, to);
                email.setType("message/rfc822");
                context.startActivity(Intent.createChooser(email, "Email Client auswählen:"));
                break;
        }
    }
}
