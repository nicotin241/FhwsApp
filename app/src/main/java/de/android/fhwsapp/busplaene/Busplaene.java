package de.android.fhwsapp.busplaene;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.HashMap;

import de.android.fhwsapp.ConnectionListener;
import de.android.fhwsapp.Database;
import de.android.fhwsapp.NutzungsdatenTransfer;
import de.android.fhwsapp.R;
import de.android.fhwsapp.adapter.BuslinienListAdapter;
import de.android.fhwsapp.pdfDownloaderViewer.PdfViewer;

public class Busplaene extends Fragment {

    private HashMap<String, String> map = new HashMap<>();
    private View view;
    private Context context;
    private String[] lineNames;
    private int tempPosition;

    private ImageView downloadImage;
    private ProgressBar downloadProgress;
    private boolean clickable = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();

        clickable = true;

        view = inflater.inflate(R.layout.activity_busplaene, container, false);
        ListView listView = (ListView) view.findViewById(R.id.lvBus);

        Database database = new Database(context);
        map = database.getBusLinien();

        lineNames = map.keySet().toArray(new String[map.keySet().size()]);
        BuslinienListAdapter adapter = new BuslinienListAdapter(getContext(), lineNames);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(!clickable)
                    return;

                downloadImage = (ImageView) view.findViewById(R.id.ivBusItem);
                downloadProgress = (ProgressBar) view.findViewById(R.id.pbBusItem);

                BuslinienListAdapter.loadingView = position;

                clickable = false;
                downloadImage.setVisibility(View.GONE);
                downloadProgress.setVisibility(View.VISIBLE);


                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {

                    if (!checkIfAlreadyhavePermission()) {

                        tempPosition = position;
                        requestForSpecificPermission();

                    } else startDownload(position);

                } else startDownload(position);

            }
        });

        new NutzungsdatenTransfer(context).execute("busplan");

        BusConnect.addListener(new ConnectionListener() {
            @Override
            public void onChanged() {
                if(downloadImage != null && downloadProgress != null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            downloadImage.setVisibility(View.VISIBLE);
                            downloadProgress.setVisibility(View.GONE);
                            clickable = true;
                            BuslinienListAdapter.loadingView = -1;
                        }
                    });
                }
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
        new PdfViewer(getContext(), getActivity(), true).viewPdf(url, "FHWS_Buslinien", name);
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

