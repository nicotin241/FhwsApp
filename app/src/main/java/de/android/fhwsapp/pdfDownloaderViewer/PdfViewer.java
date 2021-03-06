package de.android.fhwsapp.pdfDownloaderViewer;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import de.android.fhwsapp.MainActivity;
import de.android.fhwsapp.connection.BusConnect;

public class PdfViewer {

    private Context context;
    private FragmentActivity activity;
    private boolean waitingForListener = false;

    public PdfViewer(Context context, FragmentActivity activity) {
        this.context = context;
        this.activity = activity;
        waitingForListener = false;
    }

    public PdfViewer(Context context, FragmentActivity activity, boolean waitingforListener) {
        this.context = context;
        this.activity = activity;
        this.waitingForListener = waitingforListener;
    }

    public void viewPdf(String url, String folderName, String fileName) {
        new DownloadFile().execute(url, folderName, fileName);
    }

    private void view(String uri) {

        if (waitingForListener)
            BusConnect.sendToAllListeners();

        File pdfFile = new File(uri);
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);

        if (Build.VERSION.SDK_INT < 24) {

            pdfIntent.setDataAndType(Uri.fromFile(pdfFile), "application/pdf");
            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        } else {

            Uri pdfURI = FileProvider.getUriForFile(context, "de.android.fhwsapp.fileprovider", pdfFile);
            pdfIntent.setDataAndType(pdfURI, "application/pdf");
            pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        }

        try {
            context.startActivity(pdfIntent);
        } catch (ActivityNotFoundException e) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(context, "Sie benötigen eine Applikation, welche PDFs darstellen kann", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception ex) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(context, "Es ist leider ein Fehler aufgetreten", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private class DownloadFile extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {

            String fileUrl = strings[0];
            String folderName = strings[1];
            String fileName = strings[2];

            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, folderName);
            if (!folder.exists())
                folder.mkdir();

            File pdfFile = new File(folder, fileName + ".pdf");

            if (pdfFile.exists() && !MainActivity.isNetworkConnected(context)) {
                view(pdfFile.getAbsolutePath());
                return null;
            }

            if (pdfFile.exists())
                pdfFile.delete();


            try {
                if (!MainActivity.isNetworkConnected(context)) {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(context, "Es besteht keine Internetverbindung. Die Datei konnte nicht heruntergeladen werden!", Toast.LENGTH_LONG).show();
                        }
                    });

                    if (waitingForListener)
                        BusConnect.sendToAllListeners();

                    return null;
                }

                pdfFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileDownloader.downloadFile(fileUrl, pdfFile, context);

            view(pdfFile.getAbsolutePath());
            return null;
        }
    }

}
