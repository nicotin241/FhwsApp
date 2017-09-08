package de.android.fhwsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.joda.time.DateTime;

import de.android.fhwsapp.Timetable.Timetable;
import de.android.fhwsapp.Timetable.TimetableDataFetcher;
import de.android.fhwsapp.busplaene.Busplaene;
import de.android.fhwsapp.fragments.LaufendeVeranstaltungenFragment;
import de.android.fhwsapp.fragments.MainFragment;
import de.android.fhwsapp.fragments.MensaFragment;
import de.android.fhwsapp.fragments.SpoFragment;
import de.android.fhwsapp.fragments.WebViewFragment;

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
        mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor = mPrefs.edit();

        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);

        utils = new NetworkUtils(this);

        initToolbar();

        initNavigationDrawer();

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
    public void onResume() {

        super.onResume();
        setDrawerItemSelected(mFragment);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (mFragment instanceof MainFragment) {

            this.finish();

        } else {

            mFragment = new MainFragment();
            setFragment(mFragment);
            navigationView.setCheckedItem(R.id.nav_home);

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
            this.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

        } else if (id == R.id.nav_grades) {

            if(utils.isConnectingToInternet()) {
                startNotenWebView(null);
            }else showNoInternetSnackBar();


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

            if(utils.isConnectingToInternet()) {
                startNotenverlaufWebView(null);
            }else showNoInternetSnackBar();


        } else if (id == R.id.nav_spo) {

            mFragment = new SpoFragment();
            setFragment(mFragment);

        } else if (id == R.id.nav_imma) {

            if(utils.isConnectingToInternet()) {
                startImmatrikWebView(null);
            }else showNoInternetSnackBar();


        } else if (id == R.id.nav_signout) {

            deleteUserData();

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

        View header = navigationView.getHeaderView(0);

        TextView matrikelnummer = (TextView)header.findViewById(R.id.matrikelnummer);
        TextView email = (TextView)header.findViewById(R.id.email);
        matrikelnummer.setText(mPrefs.getString("matrikelnummer", "123 4567"));
        email.setText(mPrefs.getString("email", "vorname.nachname@student.fhws.de"));

        if(mPrefs.getBoolean("firststart", true)) {

            drawer.openDrawer((int) Gravity.LEFT, true);
            editor.putBoolean("firststart", false).apply();

        }

    }

    private void deleteUserData() {

        editor.putBoolean("signedIn", false);
        editor.putInt("MENSAID", -1);
        editor.putString("matrikelnummer", "");
        editor.putString("email", "");
        editor.putBoolean("firststart", true);
        editor.putString(LoginActivity.K_NUMBER, "");
        editor.putString(LoginActivity.PASSWORD, "");
        editor.apply();

    }

    private void setFragment(Fragment fragment) {

        String backStateName = fragment.getClass().getName();

        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped) { //fragment not in back stack, create it.
            android.support.v4.app.FragmentTransaction ft = manager.beginTransaction();
            ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            ft.replace(R.id.content_frame, fragment);
            if (!(fragment instanceof MainFragment))
                ft.addToBackStack(backStateName);  //do this to avoid white screen after backPressed
            ft.commit();
        }

        setDrawerItemSelected(fragment);

    }

    private void setDrawerItemSelected(Fragment fragment) {

        if(fragment instanceof MainFragment) navigationView.setCheckedItem(R.id.nav_home);
        else if(fragment instanceof MensaFragment) navigationView.setCheckedItem(R.id.nav_mensa);
        else if(fragment instanceof LaufendeVeranstaltungenFragment) navigationView.setCheckedItem(R.id.nav_veranst);
        else if(fragment instanceof Busplaene) navigationView.setCheckedItem(R.id.nav_bus);
        else if(fragment instanceof WebViewFragment && WebViewFragment.URL.equals("https://studentenportal.fhws.de/grades")) navigationView.setCheckedItem(R.id.nav_grades);
        else navigationView.setCheckedItem(R.id.menu_none);

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

            mFragment = new WebViewFragment();
            Bundle bundle = new Bundle();

            final String js = "javascript:" +
                    "document.getElementsByName('password')[0].value = '" + password + "';" +
                    "document.getElementsByName('username')[0].value = '" + username + "';" +
                    "document.getElementsByClassName('btn btn-primary')[0].click()";

//            bundle.putString("url", "https://studentenportal.fhws.de/cert");
//            bundle.putString("js", js);
            WebViewFragment.URL = "https://studentenportal.fhws.de/cert";
            WebViewFragment.JS = js;
            mFragment.setArguments(bundle);
            setFragment(mFragment);

        } else showLoginSnackbar();

    }

    public void startNotenverlaufWebView(View view) {

        if (loginDataFilled()) {

            mFragment = new WebViewFragment();
            Bundle bundle = new Bundle();

            final String js = "javascript:" +
                    "document.getElementsByName('password')[0].value = '" + password + "';" +
                    "document.getElementsByName('username')[0].value = '" + username + "';" +
                    "document.getElementsByClassName('btn btn-primary')[0].click()";

//            bundle.putString("url", "https://studentenportal.fhws.de/history");
//            bundle.putString("js", js);
            WebViewFragment.URL = "https://studentenportal.fhws.de/history";
            WebViewFragment.JS = js;
            mFragment.setArguments(bundle);
            setFragment(mFragment);

        } else showLoginSnackbar();
    }

    public void startNotenWebView(View view) {

        if (loginDataFilled()) {
            mFragment = new WebViewFragment();

            final String js = "javascript:" +
                    "document.getElementsByName('password')[0].value = '" + password + "';" +
                    "document.getElementsByName('username')[0].value = '" + username + "';" +
                    "document.getElementsByClassName('btn btn-primary')[0].click()";

            WebViewFragment.URL = "https://studentenportal.fhws.de/grades";
            WebViewFragment.JS = js;
            setFragment(mFragment);

        } else showLoginSnackbar();
    }

    private void showLoginSnackbar() {

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

        if(utils == null)
            utils = new NetworkUtils(context);

        return utils.isConnectingToInternet();
    }

    private boolean loginDataFilled() {

        if (username.equals("") || password.equals("")) return false;
        else return true;

    }

    private void showNoInternetSnackBar() {

        Snackbar bar = Snackbar.make(drawer_layout, "Netzwerk notwendig", Snackbar.LENGTH_INDEFINITE)
                .setAction("Einstellungen", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivityForResult(new Intent(
                                Settings.ACTION_WIFI_SETTINGS), 0);
                    }
                });

        bar.show();

    }

}
