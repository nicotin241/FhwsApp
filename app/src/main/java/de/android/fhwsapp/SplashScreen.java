package de.android.fhwsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.WindowManager;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.android.fhwsapp.Timetable.Subject;
import de.android.fhwsapp.busplaene.BusplanDataFetcher;

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
                new GetID().execute();
            }

            // Server Sync
             //new MensaDataFetcher(this).execute();
            new MensaDataFetcher(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            new BusplanDataFetcher(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


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

    public class GetID extends AsyncTask<String, Void, String> {

        String server_response;

        @Override
        protected String doInBackground(String... strings) {

            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL("http://54.93.76.71:8080/FHWS/userData");
                urlConnection = (HttpURLConnection) url.openConnection();

                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    server_response = readStream(urlConnection.getInputStream());
                    Log.v("ID", server_response);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(server_response);
                String id = jsonObject.getString("userId");

                mPrefs.edit().putString("ID", id).apply();
            }catch (Exception e){}

        }

        private String readStream(InputStream in) {
            BufferedReader reader = null;
            StringBuffer response = new StringBuffer();
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return response.toString();
        }

    }

}
