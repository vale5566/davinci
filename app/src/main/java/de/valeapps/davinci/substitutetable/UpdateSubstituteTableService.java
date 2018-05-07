package de.valeapps.davinci.substitutetable;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import de.valeapps.davinci.R;
import de.valeapps.davinci.Utils;

public class UpdateSubstituteTableService extends JobService {

    final int NOTIFICATION_REQUEST_CODE = 5742;
    final String url = "https://valeapps.de/davinci/vertretung.html";
    String ContentTitle;
    String ContentText;
    File substitute;
    File check;

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(Utils.TAG, "onStop called on SubstituteTable");
        return false;
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        substitute = new File(getFilesDir(), "substitute.html");
        check = new File(getFilesDir(), "check.html");

        Thread t = new Thread() {
            public void run() {
                if (Utils.isNetworkAvailable(getApplicationContext())) {
                    if (substitute.exists()) {
                        if (!check.exists()) {
                            try {
                                check.createNewFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        AndroidNetworking.download(url, check.getAbsolutePath(), "")
                                .setPriority(Priority.MEDIUM)
                                .build()
                                .startDownload(new DownloadListener() {
                                    @Override
                                    public void onDownloadComplete() {
                                        lengthCheck();
                                        stopSelf();
                                    }

                                    @Override
                                    public void onError(ANError anError) {
                                        Log.e(Utils.TAG, anError.getErrorDetail() + "SubstituteTable");
                                        stopSelf();
                                    }
                                });

                    } else {
                        firstDownloadSubstituteTable();
                        stopSelf();
                    }
                } else {
                    Log.i(Utils.TAG, "Kein Internet also kein Vertretungsplan.");
                    stopSelf();
                }
            }
        };
        t.start();
        return true;
    }

    private void firstDownloadSubstituteTable() {

        if (!substitute.exists()) {
            try {
                substitute.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            AndroidNetworking.download(url, substitute.getAbsolutePath(), "")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .startDownload(new DownloadListener() {
                        @Override
                        public void onDownloadComplete() {
                            Log.i(Utils.TAG, "Vertretungsplan erstes mal heruntergeladen.");
                            makeNotification();
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.e(Utils.TAG, anError.getErrorDetail());
                        }
                    });
        }
    }

    private void lengthCheck() {

        if (check.exists()) {
            long lengthcheck = check.length();

            long lengthvertretung = substitute.length();

            if (lengthcheck == lengthvertretung) {
                check.delete();
                Log.i(Utils.TAG, "Vertretungsplan aktuell");
            } else {
                newsubstituteTable();
                makeNotification();
            }
        } else {
            Log.e(Utils.TAG, "Ich denke Fehler beim herunterladen.");
        }
    }

    private void newsubstituteTable() {

        substitute.delete();
        check.renameTo(substitute);
        makeNotification();
    }

    private void makeNotification() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean notification = sp.getBoolean("notification", true);
        if (notification) {
            String vertretungstring;
            vertretungstring = readTextFile(substitute);
            String klasse = sp.getString("klasse", "keine");
            if (vertretungstring != null) {
                if (vertretungstring.contains(klasse)) {
                    ContentTitle = "Da-Vinci " + klasse;
                    ContentText = "Guck auf den Vertretungsplan.";
                    notification();
                } else {
                    Log.i(Utils.TAG, "Nichts enthalten..");
                }
            } else {
                Log.e(Utils.TAG, "Error Null on Vertretungs String");
            }
        }
    }

    private void notification() {
        PendingIntent pi = PendingIntent.getActivity(this, NOTIFICATION_REQUEST_CODE, new Intent(getApplicationContext(), SubstituteTableActivity.class), 0);
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(ContentTitle)
                .setContentText(ContentText)
                .setContentIntent(pi)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setVibrate(new long[]{1000, 1000})
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

    private String readTextFile(File file) {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(Uri.fromFile(file))));
            String line;

            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }
}
