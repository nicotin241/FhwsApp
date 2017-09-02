package de.android.fhwsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;

import de.android.fhwsapp.Timetable.Timetable;
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

    //Fragments
    private Fragment mFragment;
    public FragmentManager manager;

    private SharedPreferences mPrefs;
    private SharedPreferences.Editor editor;

    private ScrollView scrollView;

    private Context mContext;

    private String password;
    private String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        scrollView = (ScrollView) findViewById(R.id.svMain);

        initToolbar();

        initNavigationDrawer();

        mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor = mPrefs.edit();

        password = mPrefs.getString(LoginActivity.PASSWORD,"");
        username = mPrefs.getString(LoginActivity.K_NUMBER,"");


        manager = getSupportFragmentManager();
        mFragment = new MainFragment();
        setFragment(mFragment);


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_timetable) {
            Intent intent = new Intent(this, Timetable.class);
            startActivity(intent);
        } else if (id == R.id.nav_grades) {

        } else if (id == R.id.nav_mensa) {

            mFragment = new MensaFragment();
            setFragment(mFragment);

        } else if (id == R.id.nav_bus) {
            mFragment = new Busplaene();
            setFragment(mFragment);

        } else if (id == R.id.nav_veranst) {
//            if(mFragment instanceof MainFragment) {
//                if (scrollView == null)
//                    scrollView = (ScrollView) findViewById(R.id.svMain);
//
//                scrollView.setSmoothScrollingEnabled(true);
//                scrollView.smoothScrollTo(0, 0);
//            }
//            else{
//                mFragment = new MainFragment();
//                setFragment(mFragment);
//            }

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

    private void setFragment (Fragment fragment){
        String backStateName = fragment.getClass().getName();

        boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);

        if (!fragmentPopped){ //fragment not in back stack, create it.
            android.support.v4.app.FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            if(!(fragment instanceof MainFragment)) ft.addToBackStack(backStateName);  //do this to avoid white screen after backPressed
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

    public void startBuslinienFragment(View view){
        mFragment = new Busplaene();
        setFragment(mFragment);
    }

    public void startImmatrikWebView(View view){
        mFragment = new MyWebView();
        Bundle bundle = new Bundle();

        final String js = "javascript:" +
                "document.getElementsByName('password')[0].value = '" + password + "';"  +
                "document.getElementsByName('username')[0].value = '" + username + "';"  +
                "document.getElementsByClassName('btn btn-primary')[0].click()";

        bundle.putString("url","https://studentenportal.fhws.de/cert");
        bundle.putString("js",js);
        mFragment.setArguments(bundle);
        setFragment(mFragment);
    }
    public void startNotenverlaufWebView(View view){
        mFragment = new MyWebView();
        Bundle bundle = new Bundle();

        final String js = "javascript:" +
                "document.getElementsByName('password')[0].value = '" + password + "';"  +
                "document.getElementsByName('username')[0].value = '" + username + "';"  +
                "document.getElementsByClassName('btn btn-primary')[0].click()";

        bundle.putString("url","https://studentenportal.fhws.de/history");
        bundle.putString("js",js);
        mFragment.setArguments(bundle);
        setFragment(mFragment);
    }
    public void startNotenWebView(View view){
        mFragment = new MyWebView();

        final String js = "javascript:" +
                "document.getElementsByName('password')[0].value = '" + password + "';"  +
                "document.getElementsByName('username')[0].value = '" + username + "';"  +
                "document.getElementsByClassName('btn btn-primary')[0].click()";

        Bundle bundle = new Bundle();
        bundle.putString("url","https://studentenportal.fhws.de/grades");
        bundle.putString("js",js);
        mFragment.setArguments(bundle);
        setFragment(mFragment);
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

}
