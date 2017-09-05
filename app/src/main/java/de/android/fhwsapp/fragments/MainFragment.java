package de.android.fhwsapp.fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import de.android.fhwsapp.LVeranstaltungenDataFetcher;
import de.android.fhwsapp.MainActivity;
import de.android.fhwsapp.R;
import de.android.fhwsapp.Timetable.Subject;
import de.android.fhwsapp.Timetable.Timetable;

import static android.content.Context.MODE_PRIVATE;

public class MainFragment extends Fragment implements View.OnClickListener {

    private CardView timeTable;

    private SharedPreferences preferences;

    //todays Events
    private String todaysEvents = "";
    private ListView listView;
    private ProgressBar pbEvents;

    private List<Subject> subjectList;



    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout =  inflater.inflate(R.layout.fragment_main, container, false);

        timeTable = (CardView) layout.findViewById(R.id.timeTable);
        timeTable.setOnClickListener(this);

        preferences = getContext().getSharedPreferences(getContext().getPackageName(),MODE_PRIVATE);

        //todays Events
        subjectList = new ArrayList<>();
        pbEvents = (ProgressBar) layout.findViewById(R.id.pbEvents);
        listView = (ListView) layout.findViewById(R.id.listView);
        todaysEvents = preferences.getString("todaysEvents", "");

        LVeranstaltungenDataFetcher lvDataFetcher = new LVeranstaltungenDataFetcher(getContext(), pbEvents, listView, todaysEvents);

        if(MainActivity.isNetworkConnected(getContext())) {

            lvDataFetcher.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "https://apistaging.fiw.fhws.de/mo/api/events/today");

        }else{

           lvDataFetcher.offlineUse();
        }

        return layout;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.timeTable:
                Intent intent = new Intent(this.getActivity(), Timetable.class);
                startActivity(intent);
                break;
        }
    }

}
