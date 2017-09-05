package de.android.fhwsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;

import org.joda.time.DateTime;

import de.android.fhwsapp.Timetable.Timetable;
import de.android.fhwsapp.Timetable.TimetableDataFetcher;
import de.android.fhwsapp.busplaene.Busplaene;
import de.android.fhwsapp.fragments.LaufendeVeranstaltungenFragment;
import de.android.fhwsapp.fragments.MainFragment;
import de.android.fhwsapp.fragments.MensaFragment;
import de.android.fhwsapp.fragments.SpoFragment;
import de.android.fhwsapp.webView.MyWebView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //Navigation Drawer
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private static NetworkUtils utils;

    //Fragments
    private Fragment mFragment;
    public FragmentManager manager;

    private SharedPreferences mPrefs;
    private SharedPreferences.Editor editor;

    private DrawerLayout drawer_layout;

    private Context mContext;

    private String password;
    private String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);

        utils = new NetworkUtils(this);

        initToolbar();

        initNavigationDrawer();

        mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor = mPrefs.edit();

        password = mPrefs.getString(LoginActivity.PASSWORD, "");
        username = mPrefs.getString(LoginActivity.K_NUMBER, "");


        manager = getSupportFragmentManager();
        mFragment = new MainFragment();
        setFragment(mFragment);

        if(utils.isConnectingToInternet()) {
            new NutzungsdatenTransfer(this).execute("app");

            long lastUpdate = mPrefs.getLong("lastUpdate", 300001);
            long timeDiff = DateTime.now().getMillis() - lastUpdate;

            //update nur alle 5 Minuten
            if (timeDiff > 300000) {
                if(!Timetable.isLoading)
                    new TimetableDataFetcher(mContext).execute();
            }
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_home) {

            mFragment = new MainFragment();
            setFragment(mFragment);

        } else if (id == R.id.nav_timetable) {

            Intent intent = new Intent(this, Timetable.class);
            startActivity(intent);

        } else if (id == R.id.nav_grades) {

            startNotenWebView(null);

        } else if (id == R.id.nav_mensa) {

            mFragment = new MensaFragment();
            setFragment(mFragment);

        } else if (id == R.id.nav_bus) {
            mFragment = new Busplaene();
            setFragment(mFragment);

        } else if (id == R.id.nav_veranst) {

            mFragment = new LaufendeVeranstaltungenFragment();
            setFragment(mFragment);

        } else if (id == R.id.nav_progress) {

            startNotenverlaufWebView(null);

        } else if (id == R.id.nav_spo) {

            mFragment = new SpoFragment();
            setFragment(mFragment);

        } else if (id == R.id.nav_imma) {

            startImmatrikWebView(null);

        } else if (id == R.id.nav_signout) {

            editor.putBoolean("signedIn", false);
            editor.putInt("MENSAID", -1);
            editor.putString(LoginActivity.K_NUMBER, "");
            editor.putString(LoginActivity.PASSWORD, "");
            editor.apply();

            startActivity(new Intent(mContext, SplashScreen.class));
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initToolbar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("F H | W - S");

    }

    private void initNavigationDrawer() {

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void setFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();

        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped) { //fragment not in back stack, create it.
            android.support.v4.app.FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            if (!(fragment instanceof MainFragment))
                ft.addToBackStack(backStateName);  //do this to avoid white screen after backPressed
            ft.commit();
        }
    }

    public void startMensaFragment(View view) {

        mFragment = new MensaFragment();
        setFragment(mFragment);

    }

    public void startLVFragment(View view) {

        mFragment = new LaufendeVeranstaltungenFragment();
        setFragment(mFragment);

    }

    public void startBuslinienFragment(View view) {
        mFragment = new Busplaene();
        setFragment(mFragment);
    }

    public void startImmatrikWebView(View view) {

        if (loginDataFilled()) {

            mFragment = new MyWebView();
            Bundle bundle = new Bundle();

            final String js = "javascript:" +
                    "document.getElementsByName('password')[0].value = '" + password + "';" +
                    "document.getElementsByName('username')[0].value = '" + username + "';" +
                    "document.getElementsByClassName('btn btn-primary')[0].click()";

//            bundle.putString("url", "https://studentenportal.fhws.de/cert");
//            bundle.putString("js", js);
            MyWebView.URL = "https://studentenportal.fhws.de/cert";
            MyWebView.JS = js;
            mFragment.setArguments(bundle);
            setFragment(mFragment);

        } else showSnackbar();

    }

    public void startNotenverlaufWebView(View view) {

        if (loginDataFilled()) {

            mFragment = new MyWebView();
            Bundle bundle = new Bundle();

            final String js = "javascript:" +
                    "document.getElementsByName('password')[0].value = '" + password + "';" +
                    "document.getElementsByName('username')[0].value = '" + username + "';" +
                    "document.getElementsByClassName('btn btn-primary')[0].click()";

//            bundle.putString("url", "https://studentenportal.fhws.de/history");
//            bundle.putString("js", js);
            MyWebView.URL = "https://studentenportal.fhws.de/history";
            MyWebView.JS = js;
            mFragment.setArguments(bundle);
            setFragment(mFragment);

        } else showSnackbar();
    }

    public void startNotenWebView(View view) {

        if (loginDataFilled()) {
            mFragment = new MyWebView();

            final String js = "javascript:" +
                    "document.getElementsByName('password')[0].value = '" + password + "';" +
                    "document.getElementsByName('username')[0].value = '" + username + "';" +
                    "document.getElementsByClassName('btn btn-primary')[0].click()";

            Bundle bundle = new Bundle();
//            bundle.putString("url", "https://studentenportal.fhws.de/grades");
//            bundle.putString("js", js);
            MyWebView.URL = "https://studentenportal.fhws.de/grades";
            MyWebView.JS = js;
            mFragment.setArguments(bundle);
            setFragment(mFragment);

        } else showSnackbar();
    }

    private void showSnackbar() {

        Snackbar bar = Snackbar.make(drawer_layout, "Anmeldung notwendig", Snackbar.LENGTH_LONG)
                .setAction("LogIn", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplication(), LoginActivity.class));
                        finish();
                    }
                });

        bar.show();

    }

    public static boolean isNetworkConnected(Context context) {
//        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        return cm.getActiveNetworkInfo() != null;

        if(utils == null)
            utils = new NetworkUtils(context);

        return utils.isConnectingToInternet();
    }

    private boolean loginDataFilled() {

        if (username.equals("") || password.equals("")) return false;
        else return true;

    }

}
