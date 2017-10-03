package de.android.fhwsapp.servertasks;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import de.android.fhwsapp.Database;
import de.android.fhwsapp.objects.NewsItem;

public class NewsDataFetcher extends AsyncTask<Void, Void, Void> {

    private HttpURLConnection urlConnection;
    private Database dataBaseHelper;
    private Context mContext;
    private StringBuilder result;

    private final String URL_NEWS = "https://fhwsapp.tk:8443/FHWS/news";

    public NewsDataFetcher(Context context) {

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

            URL url = new URL(URL_NEWS);
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

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public void run() {
                        try {
                            Toast.makeText(mContext, "News-Serverfehler: " + urlConnection.getResponseCode() + "-" + urlConnection.getResponseMessage(), Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }

        if (result != null) {

            dataBaseHelper.deleteOldNews();

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            NewsItem[] allNews = gson.fromJson(result.toString(), NewsItem[].class);

            if (allNews != null) {

                for (NewsItem newsItem : allNews) {

                    dataBaseHelper.addNewsItem(newsItem);

                }

            }

        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);


    }

}
