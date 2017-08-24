package de.android.fhwsapp.busplaene;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import de.android.fhwsapp.Database;
import de.android.fhwsapp.objects.Meal;


public class BusplanDataFetcher extends AsyncTask<Void, Void, Void> {

    private HttpURLConnection urlConnection;
    private Database database;
    private Context mContext;


    private String urlString = "http://54.93.76.71:8080/FHWS/busplan";

    public BusplanDataFetcher(Context context) {

        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        database = new Database(mContext);

    }

    @Override
    protected Void doInBackground(Void... params) {

        StringBuilder result = new StringBuilder();

        try {

            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }

        String serverData = result.toString();

        try {

            JSONArray jsonArray = new JSONArray(serverData);

            if(jsonArray != null)
                try {
                    database.deleteOldBusLinien();
                } catch (Exception e) {
                }

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject linien = jsonArray.getJSONObject(i);

                String linie = "";
                linie = linien.keys().next().toString();

                String url = "";
                url = linien.getString(linie);


                try {
                    database.addBusplan(linie, url);
                } catch (Exception e) {
                }

            }

        } catch (JSONException e) {

            e.printStackTrace();

        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);

    }


}


