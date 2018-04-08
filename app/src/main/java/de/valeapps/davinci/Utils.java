package de.valeapps.davinci;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import de.valeapps.davinci.score.ScoreActivity;
import de.valeapps.davinci.substitutetable.SubstituteTableActivity;
import de.valeapps.davinci.teacher.TeacherActivity;
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
            case R.id.main:
                context.startActivity(new Intent(context, MainActivity.class));
                break;
            case R.id.timetable:
                context.startActivity(new Intent(context, TimeTableActivity.class));
                break;
            case R.id.substitutetable:
                context.startActivity(new Intent(context, SubstituteTableActivity.class));
                break;
            case R.id.score:
                context.startActivity(new Intent(context, ScoreActivity.class));
                break;
            case R.id.teacher:
                context.startActivity(new Intent(context, TeacherActivity.class));
                break;
            case R.id.website:
                Website.startWebsite(context);
                break;
            case R.id.yearbook:
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
