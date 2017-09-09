package de.valeapps.davinci;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.StringRequestListener;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SharedPreferences.OnSharedPreferenceChangeListener spListener;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        sp = PreferenceManager.getDefaultSharedPreferences(this);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().findItem(R.id.startseite).setChecked(true);

        showMessagesonMainScreen();
        spListener = this::settingChange;
        sp.registerOnSharedPreferenceChangeListener(spListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sp.registerOnSharedPreferenceChangeListener(spListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sp.unregisterOnSharedPreferenceChangeListener(spListener);
    }

//    private void startUpdateTimeTable() {
//        AlarmManager am = (AlarmManager) MainActivity.this.getSystemService(ALARM_SERVICE);
//
//        Intent startServiceIntent = new Intent(MainActivity.this, UpdateTimeTableService.class);
//
//        PendingIntent startServicePendingIntent = PendingIntent.getService(this, 0, startServiceIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//
//        Calendar calendar = Calendar.getInstance();
//
//        calendar.setTimeInMillis(System.currentTimeMillis());
//
//        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), UPDATE_TIME_TABLE_IN_MILLISECONDS, startServicePendingIntent);
//    }
//
//    private void startOfflineSubstituteTable() {
//        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
//
//        if (sp.getBoolean("offline", true)) {
//            AlarmManager am = (AlarmManager) MainActivity.this.getSystemService(ALARM_SERVICE);
//
//            Intent startServiceIntent = new Intent(MainActivity.this, UpdateSubstituteTableService.class);
//
//            PendingIntent startServicePendingIntent = PendingIntent.getService(this, 0, startServiceIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//
//            Calendar calendar = Calendar.getInstance();
//
//            calendar.setTimeInMillis(System.currentTimeMillis());
//
//            String updatestring = sp.getString("time", "30");
//            int update = Integer.parseInt(updatestring);
//
//            if (update > 1) {
//                am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 60 * update, startServicePendingIntent);
//            } else {
//                Log.i(Utils.TAG, "Ist auf Never");
//            }
//        } else {
//            Log.i(Utils.TAG, "Offline Vertretungsplan ist aus");
//
//        }
//    }

    private void settingChange(SharedPreferences sp, String key) {
        switch (key) {
            case "klasse":
                String klasse = sp.getString("klasse", "null");
                String stundenplanurl = "http://valeapps.de/davinci/plan/" + klasse + ".jpg";
                File stundenplan = new File(getFilesDir(), "stundenplan.jpg");
                AndroidNetworking.download(stundenplanurl, stundenplan.getAbsolutePath(), "")
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .startDownload(new DownloadListener() {
                            @Override
                            public void onDownloadComplete() {
                                Log.i(Utils.TAG, "Neuer Stundenplan! Nach Änderung.");
                            }

                            @Override
                            public void onError(ANError anError) {
                                Log.e(Utils.TAG, String.valueOf(anError));
                            }
                        });
                break;
            case "time":
                Utils.scheduleSubstituteTableJob(this);
                break;
        }
    }

    private void showMessagesonMainScreen() {
        TextView nachrichten = findViewById(R.id.tv_nachrichten);
        TextView lastedit = findViewById(R.id.tv_lastedit);

        if (Utils.isNetworkAvailable(this)) {

            try {
                setTextViewHTML("http://valeapps.de/davinci/nachrichten.txt", nachrichten);
                setTextViewHTML("http://valeapps.de/davinci/lastedit.txt", lastedit);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            nachrichten.setText("Kein Internet..");
            lastedit.setText("");
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item_teacher_rv clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Utils.OptionsItemSelected(this, id);

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        Utils.NavigationItemSelected(this, id);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setTextViewHTML(String url, TextView tv) throws IOException {
        String TAG = Utils.TAG;

        AndroidNetworking.get(url)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String s) {
                        if (tv.getId() == R.id.tv_nachrichten) {


                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                tv.setText(Html.fromHtml(s, Html.FROM_HTML_MODE_LEGACY));
                            } else {
                                tv.setText(Html.fromHtml(s));
                            }
                        }
                        if (tv.getId() == R.id.tv_lastedit) {
                            tv.setText("Vertretungsplan zuletzt geändert: " + Html.fromHtml(s) + " Uhr");
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e(TAG, "onError errorCode : " + error.getErrorCode());
                        Log.e(TAG, "onError errorBody : " + error.getErrorBody());
                        Log.e(TAG, "onError errorDetail : " + error.getErrorDetail());
                    }
                });
    }
}
