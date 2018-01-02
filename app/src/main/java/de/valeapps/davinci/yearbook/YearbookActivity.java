package de.valeapps.davinci.yearbook;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.valeapps.davinci.R;
import de.valeapps.davinci.Utils;

public class YearbookActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private List<Yearbooks> jahrbuchs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yearbook);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        RecyclerView rv = findViewById(R.id.rv_jahrbuch);

        rv.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        initializeData();
        initializeAdapter();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().findItem(R.id.jahrbuch).setChecked(true);

        File jahrbuchfolder = new File(getFilesDir() + "/Jahrbücher/");
        if (!jahrbuchfolder.exists()) {
            jahrbuchfolder.mkdirs();
        }
    }

    private void initializeData() {
        jahrbuchs = new ArrayList<>();
        jahrbuchs.add(new Yearbooks("2015_2016"));
        jahrbuchs.add(new Yearbooks("2016_2017"));
    }

    private void initializeAdapter() {
        RecyclerView rv = findViewById(R.id.rv_jahrbuch);
        RVAdapter adapter = new RVAdapter(jahrbuchs);
        rv.setAdapter(adapter);
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
        getMenuInflater().inflate(R.menu.yearbook, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item_teacher_rv clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.delete_yearbook) {
            deleteAllYearbooks();
        } else {
            Utils.OptionsItemSelected(this, id);
        }

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

    public void deleteAllYearbooks() {

        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    File dir = new File(getFilesDir() + "/Jahrbücher");
                    if (dir.exists()) {
                        if (dir.isDirectory()) {
                            String[] children = dir.list();
                            for (int i = 0; i < children.length; i++) {
                                new File(dir, children[i]).delete();
                            }
                        }
                    } else {
                        Toast.makeText(YearbookActivity.this, "Keine Jahrbücher heruntergeladen.", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Wollen Sie wirklich alle Jahrbück löschen ? (Sie können sie natürlich erneut herunterladen)").setPositiveButton("Ja", dialogClickListener)
                .setNegativeButton("Nein", dialogClickListener).show();
    }
}

