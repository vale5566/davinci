package de.valeapps.davinci;


import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

import de.valeapps.davinci.score.ScoreActivity;
import de.valeapps.davinci.substitutetable.SubstituteTableActivity;
import de.valeapps.davinci.substitutetable.UpdateSubstituteTableService;
import de.valeapps.davinci.teacher.Team;
import de.valeapps.davinci.timetable.TimeTableActivity;
import de.valeapps.davinci.timetable.UpdateTimeTableService;
import de.valeapps.davinci.yearbook.YearbookActivity;

public class Utils {

    public static String TAG = "DaVinci";

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
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

    public static void scheduleTimeTableJob(Context context) {
        long UPDATE_TIME_TABLE_IN_MILLISECONDS_MINIMUM = 604800000;
        long UPDATE_TIME_TABLE_IN_MILLISECONDS_MAXIMUM = 864000000;
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (!Utils.isScheduled(jobScheduler, 0)) {
            ComponentName serviceComponent = new ComponentName(context, UpdateTimeTableService.class);
            JobInfo jobInfo = new JobInfo.Builder(0, serviceComponent)
                    .setMinimumLatency(UPDATE_TIME_TABLE_IN_MILLISECONDS_MINIMUM)
                    .setOverrideDeadline(UPDATE_TIME_TABLE_IN_MILLISECONDS_MAXIMUM)
                    .setPersisted(true)
                    .build();

            jobScheduler.schedule(jobInfo);
        }
    }

    public static void scheduleSubstituteTableJob(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        String updatestring = sp.getString("time", "30");
        int update = Integer.parseInt(updatestring);

        if (update > 1) {
            JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            if (!Utils.isScheduled(jobScheduler, 1)) {
                ComponentName serviceComponent = new ComponentName(context, UpdateSubstituteTableService.class);
                JobInfo jobInfo = new JobInfo.Builder(1, serviceComponent)
                        .setMinimumLatency(60 * 1000 * update)
                        .setOverrideDeadline(50 * 1000 * update)
                        .setPersisted(true)
                        .build();

                jobScheduler.schedule(jobInfo);
            }
        } else {
            Log.i(Utils.TAG, "Ist auf Never");
        }
    }

    private static boolean isScheduled(JobScheduler js, int JobID) {
        List<JobInfo> jobs = js.getAllPendingJobs();
        if (jobs == null) {
            return false;
        }
        for (int i = 0; i < jobs.size(); i++) {
            if (jobs.get(i).getId() == JobID) {
                return true;
            }
        }
        return false;
    }
}
