package de.valeapps.davinci.timetable;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.StringRequestListener;

import java.io.File;
import java.io.IOException;

import de.valeapps.davinci.Utils;

public class UpdateTimeTableService extends JobService {

    final String numberurl = "http://valeapps.de/davinci/plan/number.html";
    int intoffline = 0;
    String stundenplanurl;
    private File stundenplan;

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Thread t = new Thread() {
            public void run() {
                if (Utils.isNetworkAvailable(getApplicationContext())) {

                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                    String klasse = sp.getString("klasse", "null");

                    String intofflinestring = sp.getString("time", "0");

                    if (!klasse.equals("null")) {
                        stundenplanurl = "http://valeapps.de/davinci/plan/" + klasse + ".jpg";

                        intoffline = Integer.parseInt(intofflinestring);

                        stundenplan = new File(getFilesDir(), "stundenplan.jpg");

                        AndroidNetworking.get(numberurl)
                                .setPriority(Priority.MEDIUM)
                                .build()
                                .getAsString(new StringRequestListener() {
                                    @Override
                                    public void onResponse(String s) {
                                        int number = Integer.parseInt(s);
                                        checkNumber(number);
                                        jobFinished(jobParameters, true);
                                    }

                                    @Override
                                    public void onError(ANError anError) {
                                        Log.e(Utils.TAG, anError.getErrorDetail());
                                        jobFinished(jobParameters, true);
                                    }
                                });

                    } else {
                        Log.d(Utils.TAG, "Klasse noch nicht ausgewählt");
                        jobFinished(jobParameters, true);
                    }
                } else {
                    Log.d(Utils.TAG, "Kein Internet");
                    jobFinished(jobParameters, true);
                }
            }
        };
        t.start();
        return true;
    }

    private void checkNumber(int intnumber) {

        if (intnumber > intoffline) {

            if (!stundenplan.exists()) {
                try {
                    stundenplan.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            AndroidNetworking.download(stundenplanurl, stundenplan.getAbsolutePath(), "")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .startDownload(new DownloadListener() {
                        @Override
                        public void onDownloadComplete() {
                            saveNewNumber(intnumber);
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.e(Utils.TAG, String.valueOf(anError));
                        }
                    });
        } else {
            Log.i(Utils.TAG, "Kein neuer Stundenplan");
        }
    }

    private void saveNewNumber(int intnumbernew) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("number", intnumbernew);
        editor.apply();
        Log.i(Utils.TAG, "Neuer Stundenplan");
    }
}
