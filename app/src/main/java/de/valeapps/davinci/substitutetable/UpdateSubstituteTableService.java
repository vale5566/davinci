package de.valeapps.davinci.substitutetable;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import de.valeapps.davinci.R;
import de.valeapps.davinci.Utils;

public class UpdateSubstituteTableService extends Service {

    String ContentTitle;
    String ContentText;
    String TAG = "DaVinci";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        final String url = "http://valeapps.de/davinci/vertretung.html";

        File substitute = new File(getFilesDir(), "substitute.html");
        File check = new File(getFilesDir(), "check.html");

        new Thread(() -> {

            if (Utils.isNetworkAvailable(this)) {
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
                                    long lengthcheck = check.length();

                                    long lengthvertretung = substitute.length();

                                    boolean notification = sp.getBoolean("notification", true);

                                    if (lengthcheck == lengthvertretung) {
                                        Log.i(TAG, "Vertretungsplan aktuell");
                                        check.delete();
                                    } else {
                                        substitute.delete();
                                        check.renameTo(substitute);
                                        if (notification) {
                                            String vertretungstring = null;
                                            try {
                                                vertretungstring = getStringFromFile(substitute.getAbsolutePath());
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            String klasse = sp.getString("klasse", "keine");
                                            if (vertretungstring != null) {
                                                if (vertretungstring.contains(klasse)) {
                                                    ContentTitle = "Da-Vinci " + klasse;
                                                    ContentText = "Guck auf den Vertretungsplan.";
                                                    notification();
                                                } else {
                                                    Log.i(TAG, "Nichts enthalten..");
                                                }
                                            } else {
                                                Log.e(TAG, "Error Null on Vertretungs String");
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Log.e(TAG, anError.getErrorDetail());
                                }
                            });
                } else {
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
                                        Log.i("DaVinci", "Vertretungsplan erstes mal heruntergeladen.");
                                    }

                                    @Override
                                    public void onError(ANError anError) {
                                        Log.e(TAG, anError.getErrorDetail());
                                    }
                                });
                    }
                }
            } else {
                Log.i(TAG, "Kein Internet also kein Vertretungsplan.");
            }
        }).start();
        return startId;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void notification() {
        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, SubstituteTableActivity.class), 0);
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

    private String getStringFromFile(String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        fin.close();
        return ret;
    }

    private String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }
}
