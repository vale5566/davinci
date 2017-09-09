package de.valeapps.davinci.substitutetable;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.File;

import de.valeapps.davinci.R;
import de.valeapps.davinci.Utils;

public class SubstituteTableActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_substitutetable);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        assert drawer != null;
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().findItem(R.id.vertretungsplan).setChecked(true);

        WebView webView = findViewById(R.id.webView);

        WebSettings settings = webView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");

        if (Utils.isNetworkAvailable(this)) {
//                webView.loadDataWithBaseURL("http://davincib.de/vertretung.html", null, "text/html", "utf-8", "http://davincib.de/vertretung.html");
//                webView.loadDataWithBaseURL(null, "http://google.com","text/html","UTF-8",null);
            webView.loadUrl("http://valeapps.de/davinci/vertretung.html");
        } else {
            {
                File vertretung = new File(getFilesDir(), "substitute.html");
                if (vertretung.exists()) {

                    webView.loadUrl("file:///" + vertretung.getAbsolutePath());

                    Toast.makeText(getApplicationContext(), "Offline Vertretungsplan.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Offline Vertretungsplan nicht verfügbar.", Toast.LENGTH_SHORT).show();
                }
            }
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
        // Inflate the menu; this adds items to the action bar if it is present.
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
}
