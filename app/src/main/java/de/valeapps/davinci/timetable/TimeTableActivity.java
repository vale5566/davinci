package de.valeapps.davinci.timetable;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;

import java.io.File;
import java.io.IOException;

import de.valeapps.davinci.MainActivity;
import de.valeapps.davinci.R;
import de.valeapps.davinci.Utils;

public class TimeTableActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().findItem(R.id.stundenplan).setChecked(true);

        TouchImageView iv = findViewById(R.id.imageView);

        TextView tv = findViewById(R.id.textView);

        File stundenplan = new File(getFilesDir(), "stundenplan.jpg");

        if (stundenplan.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(stundenplan.getAbsolutePath());
            assert iv != null;
            iv.setImageBitmap(bitmap);
        } else {
            assert tv != null;
            tv.setText("Erst Klasse in den Einstellungen auswählen.");
            Button button = findViewById(R.id.timeTableRefreshButton);

            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(v -> {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                String klasse = sp.getString("klasse", "null");
                String stundenplanurl = "http://valeapps.de/davinci/plan/" + klasse + ".jpg";
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
                                Log.i(Utils.TAG, "Neuer Stundenplan! Nach Änderung.");
                            }
                            @Override
                            public void onError(ANError anError) {
                                Log.e(Utils.TAG, String.valueOf(anError) + "TimeTable");
                            }
                        });
                startActivity(new Intent(TimeTableActivity.this, MainActivity.class));
            });
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
        getMenuInflater().inflate(R.menu.timetable, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.check_time_table_update) {
            startService(new Intent(getApplicationContext(), UpdateTimeTableServiceManually.class));
        } else {
            Utils.OptionsItemSelected(this, id);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        Utils.NavigationItemSelected(this, id);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
