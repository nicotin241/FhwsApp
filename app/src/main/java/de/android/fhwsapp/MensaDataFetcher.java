package de.android.fhwsapp;

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

import de.android.fhwsapp.objects.Meal;

public class MensaDataFetcher extends AsyncTask<Void, Void, Void> {

    private HttpURLConnection urlConnection;
    private Database dataBaseHelper;
    private Context mContext;

    private int mensa_id;


    private static String URL_AUSSTELLER = "https://www.studentenwerk-wuerzburg.de/index.php?type=4249&tx_thmensamenu_pi3[controller]=Speiseplan&tx_thmensamenu_pi3[action]=showjson&tx_thmensamenu_pi3[mensen]=";

    public MensaDataFetcher(Context context, int mensa_id) {

        mContext = context;
        URL_AUSSTELLER += mensa_id;
        this.mensa_id = mensa_id;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        dataBaseHelper = new Database(mContext);

    }

    @Override
    protected Void doInBackground(Void... params) {

        StringBuilder result = new StringBuilder();

        try {

            URL url = new URL(URL_AUSSTELLER);
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
        //dataBaseHelper.deleteOldAussteller();

        try {

            JSONArray jsonArray = new JSONArray(serverData);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject meal = jsonArray.getJSONObject(i);

                String name = "";
                if(meal.has("name")) name = meal.getString("name");

                String artname = "";
                if(meal.has("artname")) artname = meal.getString("artname");

                String date = "";
                if(meal.has("date")) date = meal.getString("date");

                String price_students = "";
                if(meal.has("price_students")) price_students = meal.getString("price_students");

                String foodtype = "";
                if(meal.has("foodtype")) foodtype = meal.getString("foodtype");


                Meal temp_meal = new Meal();

                temp_meal.setMensa_id(mensa_id);
                temp_meal.setName(name);
                temp_meal.setArtname(artname);
                temp_meal.setDate(date);
                temp_meal.setPrice_students(price_students);
                temp_meal.setFoodtype(foodtype);

                //dataBaseHelper.addMeal(temp_meal);

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
