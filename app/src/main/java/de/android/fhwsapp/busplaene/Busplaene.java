package de.android.fhwsapp.busplaene;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import de.android.fhwsapp.Database;
import de.android.fhwsapp.R;
import de.android.fhwsapp.adapter.BuslinienListAdapter;
import de.android.fhwsapp.pdfDownloaderViewer.FileDownloader;
import de.android.fhwsapp.pdfDownloaderViewer.PdfViewer;

public class Busplaene extends Fragment {

    private HashMap<String, String> map = new HashMap<>();
    private View view;
    private Context context;
    private String[] lineNames;
    private int tempPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();

        view = inflater.inflate(R.layout.activity_busplaene, container, false);
        ListView listView = (ListView) view.findViewById(R.id.lvBus);


        BusplanDataFetcher data = new BusplanDataFetcher(context, map, listView);
        data.execute();

        Database database = new Database(context);
        map = database.getBusLinien();

        lineNames = map.keySet().toArray(new String[map.keySet().size()]);
        BuslinienListAdapter adapter = new BuslinienListAdapter(getContext(), lineNames);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {

                    if (!checkIfAlreadyhavePermission()) {

                        tempPosition = position;
                        requestForSpecificPermission();

                    } else startDownload(position);

                } else startDownload(position);

            }
        });

        return view;
    }

    private void startDownload(int position) {

        String name = lineNames[position];
        String url = map.get(name);

        download(url, name.replace(" ", "_"));

    }

    public void download(String url, String name) {
        new PdfViewer(getContext(), getActivity()).viewPdf(url, "FHWS_Buslinien", name);
    }

    private boolean checkIfAlreadyhavePermission() {

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestForSpecificPermission() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }


    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 1: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startDownload(tempPosition);

                } else {

                    Toast.makeText(getContext(), "Berechtigungen werden benötigt, um die Buspläne zu speichern.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}

