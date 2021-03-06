package de.android.fhwsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.WindowManager;

import de.android.fhwsapp.servertasks.BusplanDataFetcher;
import de.android.fhwsapp.servertasks.GetID;
import de.android.fhwsapp.servertasks.MensaDataFetcher;
import de.android.fhwsapp.servertasks.NewsDataFetcher;

public class SplashScreen extends FragmentActivity {

    private Handler mHandler;
    private Runnable mCallback;
    private Context mContext;

    private boolean signedIn;
    private SharedPreferences mPrefs;

    private NetworkUtils utils;

    boolean firstOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);


        mHandler = new Handler();
        mContext = this;
        utils = new NetworkUtils(this);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        signedIn = mPrefs.getBoolean("signedIn", false);


        if(utils.isConnectingToInternet()) {
            //get id
            firstOpen = mPrefs.getBoolean("firstOpen",true);
            if(firstOpen){
                mPrefs.edit().putBoolean("firstOpen",false).apply();
                new GetID(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

            // Server Sync
            new MensaDataFetcher(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            new BusplanDataFetcher(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            new NewsDataFetcher(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


        }

        mCallback = new Runnable() {
            @Override
            public void run() {

                if(signedIn) {

                    startActivity(new Intent(mContext, MainActivity.class));
                    finish();

                } else {

                    startActivity(new Intent(mContext, LoginActivity.class));
                    finish();

                }

            }
        };
        mHandler.postDelayed(mCallback, 1500);

    }

}
