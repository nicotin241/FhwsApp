package de.android.fhwsapp.pdfDownloaderViewer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.webkit.CookieManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.content.Context.MODE_PRIVATE;

public class FileDownloader {

    private static final int MEGABYTE = 1024 * 1024;

    public static void downloadFile(String fileUrl, File directory, Context context) {

        try {

            URL url = new URL(fileUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Cookie", PreferenceManager.getDefaultSharedPreferences(context).getString("Cookie", ""));
            urlConnection.setRequestProperty("Referer", "https://studentenportal.fhws.de/cert");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                InputStream inputStream = urlConnection.getInputStream();
                FileOutputStream fileOutputStream = new FileOutputStream(directory);
                int totalSize = urlConnection.getContentLength();

                byte[] buffer = new byte[MEGABYTE];
                int bufferLength = 0;
                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, bufferLength);
                }
                fileOutputStream.close();

            } else {

                Toast.makeText(context, "Serverfehler: " + urlConnection.getResponseCode() + " - " + urlConnection.getResponseMessage(), Toast.LENGTH_SHORT).show();

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
