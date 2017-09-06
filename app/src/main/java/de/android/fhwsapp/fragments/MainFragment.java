package de.android.fhwsapp.fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import de.android.fhwsapp.Database;
import de.android.fhwsapp.LVeranstaltungenDataFetcher;
import de.android.fhwsapp.MainActivity;
import de.android.fhwsapp.R;
import de.android.fhwsapp.Timetable.Subject;
import de.android.fhwsapp.Timetable.Timetable;
import de.android.fhwsapp.objects.Meal;
import de.android.fhwsapp.objects.Mensa;

import static android.content.Context.MODE_PRIVATE;

public class MainFragment extends Fragment implements View.OnClickListener {

    private View layout;

    private CardView timeTable;
    private CardView mensa_card;

    private SharedPreferences preferences;

    //todays Events
    private String todaysEvents = "";
    private ListView listView;
    private ProgressBar pbEvents;

    private List<Subject> subjectList;
    private ArrayList<Meal> todayMeals;
    private Database db;

    private TextView overview_mensa_name;
    private TextView overview_mensa_meal;
    private ImageView overview_mensa_pic;

    private int mensaId;
    private static int meals_counter;
    private Thread meal_thread;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_main, container, false);

        timeTable = (CardView) layout.findViewById(R.id.timeTable);
        timeTable.setOnClickListener(this);
        db = new Database(getContext());

        preferences = getContext().getSharedPreferences(getContext().getPackageName(), MODE_PRIVATE);


        //todays Events
        subjectList = new ArrayList<>();
        pbEvents = (ProgressBar) layout.findViewById(R.id.pbEvents);
        listView = (ListView) layout.findViewById(R.id.listView);
        todaysEvents = preferences.getString("todaysEvents", "");

        LVeranstaltungenDataFetcher lvDataFetcher = new LVeranstaltungenDataFetcher(getContext(), pbEvents, listView, todaysEvents);

        if (MainActivity.isNetworkConnected(getContext())) {

            lvDataFetcher.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "https://apistaging.fiw.fhws.de/mo/api/events/today");

        } else {

            lvDataFetcher.offlineUse();
        }

        mensaId = PreferenceManager.getDefaultSharedPreferences(getContext()).getInt("MENSAID", -1);

        if (mensaId != -1) {

            getTodayMeals(db.getMealsById(mensaId));
            showMensaCard();

        }

        return layout;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.timeTable:
                Intent intent = new Intent(this.getActivity(), Timetable.class);
                startActivity(intent);
                break;
        }
    }

    private void getTodayMeals(ArrayList<Meal> allMeals) {

        todayMeals = new ArrayList<>();

        for (int i = 0; i < allMeals.size(); i++) {

            long meal_date = Long.parseLong(allMeals.get(i).getDate()) * 1000L;
            Calendar now = Calendar.getInstance(TimeZone.getTimeZone("CET"));
            Calendar timeToCheck = Calendar.getInstance(TimeZone.getTimeZone("CET"));
            timeToCheck.setTimeInMillis(meal_date);

            if (now.get(Calendar.YEAR) == timeToCheck.get(Calendar.YEAR)) {
                if (now.get(Calendar.DAY_OF_YEAR) == timeToCheck.get(Calendar.DAY_OF_YEAR)) {

                    todayMeals.add(allMeals.get(i));

                }
            }

        }

    }

    private void showMensaCard() {

        mensa_card = (CardView) layout.findViewById(R.id.mensa_card);
        mensa_card.setVisibility(View.VISIBLE);

        overview_mensa_name = (TextView) layout.findViewById(R.id.overview_mensa_name);
        overview_mensa_meal = (TextView) layout.findViewById(R.id.overview_mensa_meal);
        overview_mensa_pic = (ImageView) layout.findViewById(R.id.overview_mensa_pic);

        ArrayList<Mensa> mensaItems = Mensa.getAllMensas();

        for (Mensa mensa : mensaItems) {

            if (mensa.getMensaId() == mensaId)
                overview_mensa_name.setText("Heute in der " + mensa.getName());

        }

        switch (mensaId) {
            case 11:
                overview_mensa_pic.setImageResource(R.drawable.mensa_campus_nord);
                break;
            case 5:
                overview_mensa_pic.setImageResource(R.drawable.mensa_am_hubland);
                break;
            case 8:
                overview_mensa_pic.setImageResource(R.drawable.burse);
                break;
            case 9:
                overview_mensa_pic.setImageResource(R.drawable.mensa_roentgenring);
                break;
            case 10:
                overview_mensa_pic.setImageResource(R.drawable.mensa_josef_schneider_strasse);
                break;
            case 7:
                overview_mensa_pic.setImageResource(R.drawable.mensa_frankenstube);

        }

        overview_mensa_meal.setText(todayMeals.get(0).getName());

        if (todayMeals.size() > 1) {

            meals_counter = 1;

            meal_thread = new Thread() {

                @Override
                public void run() {
                    try {
                        while (!isInterrupted()) {
                            Thread.sleep(10000);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    overview_mensa_meal.setText(todayMeals.get(meals_counter).getName());
                                    if (meals_counter == todayMeals.size() - 1) meals_counter = 0;
                                    else meals_counter++;

                                }
                            });
                        }
                    } catch (InterruptedException e) {
                    }
                }
            };

            meal_thread.start();

        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (meal_thread != null)
            meal_thread.interrupt();

    }

}
