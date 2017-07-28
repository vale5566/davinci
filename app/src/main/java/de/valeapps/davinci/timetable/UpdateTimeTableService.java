package de.valeapps.davinci.timetable;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import de.valeapps.davinci.Utils;

public class UpdateTimeTableService extends Service {

    private final String numberurl = "http://valeapps.de/davinci/plan/number.txt";
    int intoffline = 0;
    String stundenplanurl;
    private File stundenplan;

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Utils.isNetworkAvailable(this)) {

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

            String klasse = sp.getString("klasse", "null");

            if (!klasse.equals("null")) {
                stundenplanurl = "http://valeapps.de/davinci/plan/" + klasse + ".jpg";

                intoffline = sp.getInt("number", 0);

                final File numbertxt = new File(getFilesDir(), "number.txt");

                stundenplan = new File(getFilesDir(), "stundenplan.jpg");

                if (!numbertxt.exists()) {
                    try {
                        numbertxt.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                AndroidNetworking.download(numberurl, numbertxt.getAbsolutePath(), "")
                        .setPriority(Priority.LOW)
                        .build()
                        .startDownload(new DownloadListener() {
                            @Override
                            public void onDownloadComplete() {
                                String stringnumbertxt = "0";
                                try {
                                    stringnumbertxt = new Scanner(numbertxt).useDelimiter("\\Z").next();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                int intnumbertxt = Integer.parseInt(stringnumbertxt);
                                checkTXTNumber(intnumbertxt);
                            }

                            @Override
                            public void onError(ANError anError) {
                                Log.e("DaVinci", String.valueOf(anError));
                            }
                        });
            }
        }
        return flags;
    }

    private void checkTXTNumber(int intnumbertxt) {

        if (intnumbertxt > intoffline) {

            if (!stundenplan.exists()) {
                try {
                    stundenplan.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            AndroidNetworking.download(stundenplanurl, stundenplan.getAbsolutePath(), "")
                    .setPriority(Priority.LOW)
                    .build()
                    .startDownload(new DownloadListener() {
                        @Override
                        public void onDownloadComplete() {
                            saveNewNumber(intnumbertxt);
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.e("DaVinci", String.valueOf(anError));
                        }
                    });
        } else {
            Log.i("DaVinci", "Kein neuer Stundenplan");
        }
    }

    private void saveNewNumber(int intnumbertxt) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("number", intnumbertxt);
        editor.apply();
        Log.i("DaVinci", "Neuer Stundenplan");

        if (intnumbertxt < intoffline) {
            editor.putInt("number", intnumbertxt);
            editor.apply();
            Log.i("DaVinci", "SubstituteTableActivity UpdateSubstituteTableService Zahl.");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
