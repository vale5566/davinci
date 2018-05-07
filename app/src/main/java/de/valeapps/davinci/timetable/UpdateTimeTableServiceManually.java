package de.valeapps.davinci.timetable;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.StringRequestListener;

import java.io.File;
import java.io.IOException;

import de.valeapps.davinci.Utils;

public class UpdateTimeTableServiceManually extends Service {

    String stundenplanurl;
    private File stundenplan;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Thread t = new Thread() {
            public void run() {
                if (Utils.isNetworkAvailable(getApplicationContext())) {

                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                    String klasse = sp.getString("klasse", "null");

                    if (!klasse.equals("null")) {
                        stundenplanurl = "https://valeapps.de/davinci/plan/" + klasse + ".jpg";

                        stundenplan = new File(getFilesDir(), "stundenplan.jpg");

                        AndroidNetworking.download(stundenplanurl, stundenplan.getAbsolutePath(), "")
                                .setPriority(Priority.MEDIUM)
                                .build()
                                .startDownload(new DownloadListener() {
                                    @Override
                                    public void onDownloadComplete() {
                                        Log.d(Utils.TAG, "Neuer Stundenplan.");
                                    }

                                    @Override
                                    public void onError(ANError anError) {
                                        Log.e(Utils.TAG, String.valueOf(anError));
                                    }
                                });

                    } else {
                        Toast.makeText(UpdateTimeTableServiceManually.this, "Keine Klasse ausgewählt", Toast.LENGTH_SHORT).show();
                        stopSelf();
                    }
                } else {
                    Toast.makeText(UpdateTimeTableServiceManually.this, "Kein Internet.", Toast.LENGTH_SHORT).show();
                }
            }
        };
        t.start();
        return startId;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
