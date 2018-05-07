package de.valeapps.davinci.teacher;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.valeapps.davinci.R;
import de.valeapps.davinci.Utils;

public class Team extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private List<Teachers> teacherses;

    private RecyclerView rv;
    public SearchView search;
    RVAdapter mAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_teacher);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().findItem(R.id.team).setChecked(true);

        rv = findViewById(R.id.recyclerview);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);

        initializeData();
        initializeAdapter();

        search = findViewById(R.id.search);
        search.setOnQueryTextListener(listener);
    }

    private void initializeData() {
        teacherses = new ArrayList<>();
        teacherses.add(new Teachers("K. Schmollack", "schulleitung", "Kunst, Psychologie"));
        teacherses.add(new Teachers("A. Ulbrich", "ulb", "Deutsch, Geschichte, PB"));
        teacherses.add(new Teachers("F. Gallien", "gln", "Mathe, Informatik"));
        teacherses.add(new Teachers("H. Andersson", "and", "Mathe, Erdkunde"));
        teacherses.add(new Teachers("K. Albert", "alb", "Geschichte, Sonderpädagogik"));
        teacherses.add(new Teachers("H. Barkmann", "bar", "Bio, Chemie"));
        teacherses.add(new Teachers("P. Baumgart", "bau", "Bio, Französisch, Chemie"));
        teacherses.add(new Teachers("K. Becker", "bec", "Sport, Bio"));
        teacherses.add(new Teachers("S. Bippus", "bip", "PB, Spanisch"));
        teacherses.add(new Teachers("M. Breuer", "bre", "Erdkunde, Physik, Sport"));
        teacherses.add(new Teachers("J. Devers", "dev", "Religion"));
        teacherses.add(new Teachers("A. Fischer", "fis", "PB, Englisch"));
        teacherses.add(new Teachers("J. Freitag", "fre", "Sport, Englisch"));
        teacherses.add(new Teachers("L. Fröhlich", "fro", "Französisch, LER, Philosophie"));
        teacherses.add(new Teachers("K. Hantscher", "han", "Sport, Deutsch"));
        teacherses.add(new Teachers("N. Herborn", "heb", "Deutsch, Musik, Darstellendes Spiel"));
        teacherses.add(new Teachers("J. Hirsch", "hir", "Deutsch, Spanisch"));
        teacherses.add(new Teachers("F. Herold", "heo", "Französisch, Sport"));
        teacherses.add(new Teachers("K. Holling", "hol", "Mathematik, Erdkunde, Sport"));
        teacherses.add(new Teachers("S. Hösch", "hos", "Englisch, Spanisch"));
        teacherses.add(new Teachers("S. Karakus", "kar", "Deutsch, Kunst"));
        teacherses.add(new Teachers("J. Kiolbassa", "kio", "Französisch, Deutsch"));
        teacherses.add(new Teachers("F. Kirchesch", "kir", "Englisch, Musik, LER"));
        teacherses.add(new Teachers("E. Knake", "kna", "Deutsch, PB"));
        teacherses.add(new Teachers("S. Knoche", "kno", "Spanisch, Erdkunde"));
        teacherses.add(new Teachers("S. Krämer", "kra", "Englisch, Deutsch"));
        teacherses.add(new Teachers("A. Krückels", "kru", "Deutsch, PB, Darstellendes Spiel"));
        teacherses.add(new Teachers("R. Küfner Chang", "kue", "Spanisch, Deutsch"));
        teacherses.add(new Teachers("J. Laufhäger", "lau", "Englisch, Sport"));
        teacherses.add(new Teachers("Dr. A. Lembert-Heidenreich", "lem", "Englisch, Geschichte"));
        teacherses.add(new Teachers("C. Lichtenfeld", "lic", "Deutsch, Biologie"));
        teacherses.add(new Teachers("S. Lekow", "lek", "Deutsch, Biologie"));
        teacherses.add(new Teachers("S. Moritz", "mor", "Sport, Geschichte, PB"));
        teacherses.add(new Teachers("A. Munck", "mun", "Englisch, Deutsch, DaZ"));
        teacherses.add(new Teachers("D. Niemann", "nie", "Englisch, Spanisch"));
        teacherses.add(new Teachers("K. Noack", "noa", "Chemie, Biologie"));
        teacherses.add(new Teachers("M. Ortmanns", "ort", "Mathematik, Deutsch, LER"));
        teacherses.add(new Teachers("F. Päper", "pae", "Mathematik, Physik"));
        teacherses.add(new Teachers("L. Panasik", "pan", "Deutsch, Erdkunde"));
        teacherses.add(new Teachers("J. Paust", "pau", "Kunst, Deutsch"));
        teacherses.add(new Teachers("L. Puder", "pud", "Mathematik, Physik"));
        teacherses.add(new Teachers("S. Raphael", "rap", "Musik, Biologie"));
        teacherses.add(new Teachers("S. Reichelt", "rei", "Spanisch, Deutsch"));
        teacherses.add(new Teachers("B. Rettig", "ret", "Latein, Geschichte"));
        teacherses.add(new Teachers("C. Richter", "ric", "Chemie, Biologie"));
        teacherses.add(new Teachers("K. Richter", "kri", "Englisch, Russisch"));
        teacherses.add(new Teachers("S. Rietz", "riz", "Kunst, Sport"));
        teacherses.add(new Teachers("J. Rievers", "rie", "Mathematik, Kunst"));
        teacherses.add(new Teachers("C. Rump", "rum", "LER, Deutsch"));
        teacherses.add(new Teachers("S. Schiwietz", "siw", "Mathematik, Physik"));
        teacherses.add(new Teachers("K. Schiedung", "sie", "Geschichte, Latein"));
        teacherses.add(new Teachers("K. Schöne", "soe", "Mathematik, Physik"));
        teacherses.add(new Teachers("F. Schubert", "sub", "Deutsch, Sonderpädagogik"));
        teacherses.add(new Teachers("R. Schwanbeck", "swa", "Mathematik, Physik"));
        teacherses.add(new Teachers("T. Schwarze", "swz", "WAT, Mathematik, Informatik"));
        teacherses.add(new Teachers("I. Seeger", "see", "Biologie, Geschichte"));
        teacherses.add(new Teachers("M. Stamm", "sta", "Sport, Geschichte"));
        teacherses.add(new Teachers("S. Steinborn", "ste", "Mathematik, Informatik"));
        teacherses.add(new Teachers("T. Stolz", "sto", "Mathematik, PB"));
        teacherses.add(new Teachers("R. Suhrbier", "suh", "Chemie, Physik, WAT"));
        teacherses.add(new Teachers("D. Suske", "sus", "Biologie, WAT"));
        teacherses.add(new Teachers("M. Teschendorf", "tes", "Englisch, Geschichte"));
        teacherses.add(new Teachers("W. Timm", "tim", "Englisch, Französisch"));
        teacherses.add(new Teachers("B. Watz", "wat", "Englisch, Sport"));
        teacherses.add(new Teachers("S. Wegehaupt", "weg", "Mathematik, LER"));
        teacherses.add(new Teachers("K. Wittram", "wit", "Deutsch, Musik"));
        teacherses.add(new Teachers("M. Börrnert", "boe", "Mathematik, Englisch"));
        teacherses.add(new Teachers("T. Moulla Mohamed", "mou", "PB, Sport"));
        teacherses.add(new Teachers("M. Richter", "mri", "Chemie, Physik"));
        teacherses.add(new Teachers("A. Wegner", "awe", "Deutsch, Sport"));
    }

    private void initializeAdapter() {
        RecyclerView rv = findViewById(R.id.recyclerview);
        RVAdapter adapter = new RVAdapter(teacherses);
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

    SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextChange(String query) {
            query = query.toLowerCase();

            final List<Teachers> filteredList = new ArrayList<>();

            for (int i = 0; i < teacherses.size(); i++) {

                final String text = teacherses.get(i).name.toLowerCase();
                if (text.contains(query)) {

                    filteredList.add((teacherses.get(i)));
                }
            }

            rv.setLayoutManager(new LinearLayoutManager(Team.this));
            mAdapter = new RVAdapter(filteredList);
            rv.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            return true;

        }

        public boolean onQueryTextSubmit(String query) {
            return false;
        }
    };

    public static void sendMailtoTeacher(String to, Context context) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
        emailIntent.setType("message/rfc822");
        try {
            context.startActivity(Intent.createChooser(emailIntent, "Email Client auswählen:"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "Keine Email Clients gefunden.", Toast.LENGTH_SHORT).show();
        }
    }
}
