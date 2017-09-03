package de.android.fhwsapp.busplaene;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import de.android.fhwsapp.Database;
import de.android.fhwsapp.R;
import de.android.fhwsapp.pdfDownloaderViewer.FileDownloader;
import de.android.fhwsapp.pdfDownloaderViewer.PdfViewer;

public class Busplaene extends Fragment {

    private HashMap<String,String> map = new HashMap<>();
    private View view;
    private Context context;


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



        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, map.keySet().toArray(new String[map.keySet().size()]));
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                String name = ((TextView)view).getText().toString();
                String url = map.get(name);

                download(url, name.replace(" ","_"));
            }
        });

        return view;
    }

    public void download(String url, String name)
    {
        new PdfViewer(getContext(),getActivity()).viewPdf(url,"FHWS_Buslinien", name);
    }


    /*
    *
    * 6 - Kettelerstraße 1 97074 Würzburg
    *
    * 214 Sanderheinrichsleitenweg 20, 97074 Würzburg - 49°46'39.6"N 9°57'47.1"E - 49.777657, 9.963075
    *
    * 114 - Hubland Mensa - 49°46'54.4"N 9°58'01.0"E - 49.781774, 9.966943
    *
    * 10 - 49°46'49.5"N 9°57'57.8"E -49.780404, 9.966058
    *
    * */

}

