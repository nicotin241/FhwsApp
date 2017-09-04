package de.android.fhwsapp;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import de.android.fhwsapp.adapter.MealListAdapter;
import de.android.fhwsapp.objects.Meal;

public class MensaDataFetcher extends AsyncTask<Void, Void, Void> {

    private HttpURLConnection urlConnection;
    private Database dataBaseHelper;
    private Context mContext;
    private StringBuilder result;


    private String URL_MENSA = "http://54.93.76.71:8080/FHWS/mensaplan";

    public MensaDataFetcher(Context context) {

        mContext = context;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        dataBaseHelper = new Database(mContext);

    }

    @Override
    protected Void doInBackground(Void... params) {

        result = new StringBuilder();

        try {

            URL url = new URL(URL_MENSA);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type", "application/json");

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            } else {

                Toast.makeText(mContext, "Mensa-Serverfehler: " + urlConnection.getResponseCode() + "-" + urlConnection.getResponseMessage(), Toast.LENGTH_SHORT).show();

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);

        if (result != null) {

            dataBaseHelper.deleteOldMeals();

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Meal[][] allMeals = gson.fromJson(result.toString(), Meal[][].class);

            if (allMeals != null) {

                for (Meal meal[] : allMeals) {

                    for (Meal meal2 : meal) {

                        dataBaseHelper.addMeal(meal2);

                    }

                }

            }

        }

    }


}
