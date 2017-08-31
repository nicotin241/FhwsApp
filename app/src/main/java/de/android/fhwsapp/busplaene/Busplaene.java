package de.android.fhwsapp.busplaene;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.FileUriExposedException;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import de.android.fhwsapp.Database;
import de.android.fhwsapp.R;

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


        //dummy daten
        //map.put("Linie 10","http://www.zoo2.biozentrum.uni-wuerzburg.de/fileadmin/07020200/zoo2/Eingebundene_Dateien/Konferenzen/Bus_No_10_to_Biocenter.pdf");


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, map.keySet().toArray(new String[map.keySet().size()]));
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(map.get(((TextView)view).getText())));
//                startActivity(browserIntent);

                String name = ((TextView)view).getText().toString();
                String url = map.get(name);

                download(url, name.replace(" ","_"));
                //view(name, url);
            }
        });

        return view;
    }

    public void download(String url, String name)
    {
        new DownloadFile().execute(url, name);
    }

    public void view(String uri)
    {
        File pdfFile = new File(uri);
        Uri path = Uri.fromFile(pdfFile);
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        //pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try{
            startActivity(pdfIntent);
        }catch(ActivityNotFoundException e){
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getContext(), "Sie benötigen eine Applikation, welche PDFs darstellen kann", Toast.LENGTH_LONG).show();
                }
            });
        }catch (Exception ex){
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getContext(), "Es ist leider ein Fehler aufgetreten", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private class DownloadFile extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String fileUrl = strings[0];
            String fileName = strings[1];

            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "FHWS_Buslinien");
            if(!folder.exists())
                folder.mkdir();

            File pdfFile = new File(folder, fileName+".pdf");

            if(pdfFile.exists()){
                view(pdfFile.getAbsolutePath());
                return null;
            }


            try{
                if(!isNetworkConnected(context)){
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getContext(), "Es besteht keine Internetverbindung. Der Busplan konnte nicht heruntergeladen werden!", Toast.LENGTH_LONG).show();
                        }
                    });
                    return null;
                }

                pdfFile.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }
            FileDownloader.downloadFile(fileUrl, pdfFile);

            view(pdfFile.getAbsolutePath());
            return null;
        }
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

}

