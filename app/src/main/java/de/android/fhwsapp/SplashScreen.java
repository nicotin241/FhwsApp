package de.android.fhwsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class SplashScreen extends FragmentActivity {

    private Handler mHandler;
    private Runnable mCallback;
    private Context mContext;

    private boolean signedIn;
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        mHandler = new Handler();
        mContext = this;

        mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        signedIn = mPrefs.getBoolean("signedIn", false);

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
        mHandler.postDelayed(mCallback, 2000);




    }
}
