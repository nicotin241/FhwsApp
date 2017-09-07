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

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import de.android.fhwsapp.Connect;
import de.android.fhwsapp.ConnectionListener;
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

    //next lecture
    private TextView tvDay;
    private TextView tvName;
    private TextView tvTime;
    private TextView tvType;
    private TextView tvRoom;
    private TextView tvTeacher;
    private ProgressBar pbNextLecture;
    private Database database;
    public static boolean isOpen = false;

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

        //next Lecture
        isOpen = true;
        database = new Database(getContext());
        Subject nextLecture = database.getComingLecture();
        initNextLectureViews();

        if(!MainActivity.isNetworkConnected(getContext()) || !Timetable.isLoading)
            pbNextLecture.setVisibility(View.GONE);

        if(nextLecture == null)
            timeTable.setVisibility(View.GONE);
        else{

            fillNextLectureViews(nextLecture);

            Connect.addListener(new ConnectionListener() {
                @Override
                public void onChanged() {
                    if(isOpen) {
                        fillNextLectureViews(database.getComingLecture());
                        pbNextLecture.setVisibility(View.GONE);
                    }
                }
            });

        }

        //todays Fav-Mensa meals
        mensaId = PreferenceManager.getDefaultSharedPreferences(getContext()).getInt("MENSAID", -1);

        if (mensaId != -1) {

            getTodayMeals(db.getMealsById(mensaId));
            if(todayMeals.size() > 0) showMensaCard();

        }

        return layout;
    }


    private void fillNextLectureViews(Subject nextLecture){
        int today = DateTime.now().getDayOfYear();
        int lectureDay = nextLecture.getDateAsDateTime().getDayOfYear();

        if(today == lectureDay)
            tvDay.setText("Heute");
        else
            tvDay.setText(nextLecture.getDate());

        tvName.setText(nextLecture.getSubjectName());
        tvTime.setText(nextLecture.getTimeStart()+" - "+nextLecture.getTimeEnd()+ " Uhr");
        tvType.setText(nextLecture.getType());
        tvRoom.setText(nextLecture.getRoom());
        tvTeacher.setText(nextLecture.getTeacher());

    }

    private void initNextLectureViews() {
        tvDay = (TextView) layout.findViewById(R.id.tvDay);
        tvName = (TextView) layout.findViewById(R.id.tvName);
        tvTime = (TextView) layout.findViewById(R.id.tvTime);
        tvType = (TextView) layout.findViewById(R.id.tvType);
        tvRoom = (TextView) layout.findViewById(R.id.tvRoom);
        tvTeacher = (TextView) layout.findViewById(R.id.tvTeacher);
        pbNextLecture = (ProgressBar) layout.findViewById(R.id.pbNextLecture);
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

        overview_mensa_pic.setImageResource(Mensa.getMensaPic(mensaId));

        overview_mensa_meal.setText(todayMeals.get(0).getName());

        if (todayMeals.size() > 1) {

            meals_counter = 1;

            meal_thread = new Thread() {

                @Override
                public void run() {

                    if(getActivity() == null) {
                        return;
                    } else {
                        try {
                            while (!isInterrupted()) {
                                Thread.sleep(6000);
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

                        } catch (NullPointerException e) {

                            //Bad solution - for Debugging!
                            Log.d("THREAD", "Exception: + " + e.toString());
                            meal_thread.interrupt();

                        }
                    }
                }
            };

            meal_thread.start();

        }
    }

    private void stopThread() {
        if (meal_thread != null)
            meal_thread.interrupt();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(meal_thread != null && meal_thread.isInterrupted()) meal_thread.run();
    }

    @Override
    public void onPause() {
        super.onPause();

        isOpen = false;
        stopThread();

    }

    @Override
    public void onStop() {
        super.onStop();
        stopThread();
    }

}
