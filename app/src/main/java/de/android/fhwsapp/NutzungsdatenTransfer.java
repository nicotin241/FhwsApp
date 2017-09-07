package de.android.fhwsapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class NutzungsdatenTransfer extends AsyncTask<String, Void, String> {

    private Context context;

    public NutzungsdatenTransfer(Context context){
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        BufferedWriter bufferedWriter = null;
        String id = pref.getString("ID","");
        String feature = params[0];

        if(id.equals(""))
            return null;

        try {
            URL url = new URL("http://54.93.76.71:8080/FHWS/userData");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");

            OutputStream outputStream = httpURLConnection.getOutputStream();
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

            ContentValues values = new ContentValues();
            values.put("userId", id);
            values.put("feature", feature);

            bufferedWriter.write(getQuery(values));
            bufferedWriter.flush();

        } catch (Exception e) {

            e.printStackTrace();
        } finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private String getQuery(ContentValues values) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, Object> entry : values.valueSet())
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(String.valueOf(entry.getValue()), "UTF-8"));
        }

        return result.toString();
    }

}
