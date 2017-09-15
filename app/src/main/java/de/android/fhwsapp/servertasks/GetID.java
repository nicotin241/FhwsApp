package de.android.fhwsapp.servertasks;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetID extends AsyncTask<String, Void, String> {

    String server_response;
    Context context;

    public GetID(Context context) {
        this.context = context;
    }

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

            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("ID", id).apply();
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
