package de.android.fhwsapp.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import static android.content.Context.MODE_PRIVATE;


public class LaufendeVeranstaltungenFragment extends Fragment {

    private View view;
    private SharedPreferences preferences;

    //todays Events
    private String todaysEvents = "";
    private ListView listView;
    private ProgressBar pbEvents;

    private List<Subject> subjectList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_laufende_veranstaltungen, container, false);

        preferences = getContext().getSharedPreferences(getContext().getPackageName(),MODE_PRIVATE);

        //todays Events
        subjectList = new ArrayList<>();
        pbEvents = (ProgressBar) view.findViewById(R.id.pbEvents);
        listView = (ListView) view.findViewById(R.id.listView);
        todaysEvents = preferences.getString("todaysEvents", "");

        LVeranstaltungenDataFetcher lvDataFetcher = new LVeranstaltungenDataFetcher(getContext(), pbEvents, listView, todaysEvents);

        if(MainActivity.isNetworkConnected(getContext())) {
            //new LoadEventsFromServer().execute("https://apistaging.fiw.fhws.de/mo/api/events/today");
            lvDataFetcher.execute("https://apistaging.fiw.fhws.de/mo/api/events/today");
        }else{

            lvDataFetcher.offlineUse();
        }

        return view;
    }


}
