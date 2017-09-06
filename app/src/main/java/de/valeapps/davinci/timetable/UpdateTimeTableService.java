package de.valeapps.davinci.timetable;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.StringRequestListener;

import java.io.File;
import java.io.IOException;

import de.valeapps.davinci.Utils;

public class UpdateTimeTableService extends IntentService {

    private final String numberurl = "http://valeapps.de/davinci/plan/number.html";
    int intoffline = 0;
    String stundenplanurl;
    private File stundenplan;

    public UpdateTimeTableService() {
        super("UpdateTimeTableService");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Thread t = new Thread() {
            public void run() {
                if (Utils.isNetworkAvailable(getApplicationContext())) {

                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                    String klasse = sp.getString("klasse", "null");

                    if (!klasse.equals("null")) {
                        stundenplanurl = "http://valeapps.de/davinci/plan/" + klasse + ".jpg";

                        intoffline = sp.getInt("number", 0);

                        stundenplan = new File(getFilesDir(), "stundenplan.jpg");

                        AndroidNetworking.get(numberurl)
                                .setPriority(Priority.MEDIUM)
                                .build()
                                .getAsString(new StringRequestListener() {
                                    @Override
                                    public void onResponse(String s) {
                                        int number = Integer.parseInt(s);
                                        checkNumber(number);
                                    }

                                    @Override
                                    public void onError(ANError anError) {
                                        Log.e(Utils.TAG, anError.getErrorDetail());
                                        stopSelf();
                                    }
                                });

                    } else {
                        stopSelf();
                    }
                }
            }
        };
        t.start();
        return startId;
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
                            Log.e("DaVinci", String.valueOf(anError));
                            stopSelf();
                        }
                    });
        } else {
            Log.i("DaVinci", "Kein neuer Stundenplan");
        }
    }

    private void saveNewNumber(int intnumbernew) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("number", intnumbernew);
        editor.apply();
        Log.i("DaVinci", "Neuer Stundenplan");
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}
