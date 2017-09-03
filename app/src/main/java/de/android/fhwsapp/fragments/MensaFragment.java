package de.android.fhwsapp.fragments;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import de.android.fhwsapp.Database;
import de.android.fhwsapp.R;
import de.android.fhwsapp.adapter.MealListAdapter;
import de.android.fhwsapp.objects.Meal;

public class MensaFragment extends Fragment {

    private View view;

    private Database database;
    private Context mContext;

    private Meal[] allMeals;
    private ArrayList<Meal> todayMeals;

    private ListView mListView;
    private MealListAdapter mAdapter;

    private int mensaId;

    private TextView title;
    private TextView date;
    private ListView mealList;

    public MensaFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_mensa, container, false);

        mContext = getContext();
        database = new Database(mContext);
        mensaId = getMensaId();

        setTitleAndDate();
        setOnClickListener();

//        allMeals = database.getMealsById(9);
//
//        getTodayMeals();
//
        mListView = (ListView) view.findViewById(R.id.mealList);
//        mAdapter = new MealListAdapter(mContext, todayMeals);
//        mListView.setAdapter(mAdapter);

        new DataFetcher(getContext(), mensaId).execute();


        return view;
    }

    private void getTodayMeals() {

        todayMeals = new ArrayList<>();

        for (int i = 0; i < allMeals.length; i++) {

            long meal_date = Long.parseLong(allMeals[i].getDate()) * 1000L;
            Calendar now = Calendar.getInstance(TimeZone.getTimeZone("CET"));
            Calendar timeToCheck = Calendar.getInstance(TimeZone.getTimeZone("CET"));
            timeToCheck.setTimeInMillis(meal_date);

            if (now.get(Calendar.YEAR) == timeToCheck.get(Calendar.YEAR)) {
                if (now.get(Calendar.DAY_OF_YEAR) == timeToCheck.get(Calendar.DAY_OF_YEAR)) {

                    todayMeals.add(allMeals[i]);

                }
            }

        }

    }

    private int getMensaId() {

        //TODO: Get Fav MensaID or start MensaChoice

        return 9;

    }

    private void setTitleAndDate() {

        title = (TextView) view.findViewById(R.id.mensa_title);
        date = (TextView) view.findViewById(R.id.mensa_date);

        switch (mensaId) {

            case 9:
                title.setText("Speiseplan für Burse Würzburg");
                break;
            default:
                title.setText("Speiseplan");

        }

        date.setText(new SimpleDateFormat("EEEE, dd.MM").format(new Date()));

    }

    private void setOnClickListener() {

        mealList = (ListView) view.findViewById(R.id.mealList);
        mealList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                showDetails(todayMeals.get(i));

            }
        });

    }

    private void showDetails(Meal meal) {

        Dialog alertDialog = new Dialog(getContext());
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.meal_details);

        TextView meal_name = (TextView) alertDialog.findViewById(R.id.meal_name);
        meal_name.setText(meal.getName());

        TextView additives = (TextView) alertDialog.findViewById(R.id.additives);

        String additivesList = "Enthält:\n";

        for(int i = 0; i < meal.getAdditives().size(); i++) {

            additivesList += "   - " + meal.getAdditives().get(i) + "\n";

        }

        additives.setText(additivesList);

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

    }


    public class DataFetcher extends AsyncTask<Void, Void, Void> {

        private HttpURLConnection urlConnection;
        private Database dataBaseHelper;
        private Context mContext;

        private String URL_AUSSTELLER = "http://54.93.76.71:8080/FHWS/mensaplan?mensaId=";

        StringBuilder result;

        public DataFetcher(Context context, int mensa_id) {

            mContext = context;
            URL_AUSSTELLER += mensa_id;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dataBaseHelper = new Database(mContext);

        }

        @Override
        protected Void doInBackground(Void... params) {

            result = new StringBuilder();

            try {

                URL url = new URL(URL_AUSSTELLER);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type", "application/json");

                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                } else {

                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                Toast.makeText(getContext(), "Server-Fehler: " + urlConnection.getResponseCode() + "-" + urlConnection.getResponseMessage(), Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            super.onPostExecute(aVoid);

            if (result != null) {

//                dataBaseHelper.deleteOldMeals();

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                allMeals = gson.fromJson(result.toString(), Meal[].class);

                if (allMeals != null) {

                    getTodayMeals();
                    mAdapter = new MealListAdapter(mContext, todayMeals);
                    mListView.setAdapter(mAdapter);

                }



            }
        }
    }

}
