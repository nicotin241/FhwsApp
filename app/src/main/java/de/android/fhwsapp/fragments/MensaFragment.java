package de.android.fhwsapp.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import de.android.fhwsapp.Database;
import de.android.fhwsapp.R;
import de.android.fhwsapp.adapter.MealListAdapter;
import de.android.fhwsapp.objects.Meal;

/**
 * A simple {@link Fragment} subclass.
 */
public class MensaFragment extends Fragment {

    private View view;
    private Database database;
    private ArrayList<Meal> allMeals;
    private ArrayList<Meal> todayMeals;
    private Context mContext;
    private ListView mListView;
    private MealListAdapter mAdapter;

    public MensaFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_mensa, container, false);

        mContext = getContext();
        database = new Database(mContext);
        allMeals = database.getMealsById(9);

        getTodayMeals();

        mListView = (ListView) view.findViewById(R.id.mealList);
        mAdapter = new MealListAdapter(mContext, todayMeals);
        mListView.setAdapter(mAdapter);


        return view;
    }

    private void getTodayMeals() {

        todayMeals = new ArrayList<>();

        for(int i = 0; i < allMeals.size(); i++) {

            long meal_date = Long.parseLong(allMeals.get(i).getDate()) * 1000L;
            Calendar now = Calendar.getInstance(TimeZone.getTimeZone("CET"));
            Calendar timeToCheck = Calendar.getInstance(TimeZone.getTimeZone("CET"));
            timeToCheck.setTimeInMillis(meal_date);

            if(now.get(Calendar.YEAR) == timeToCheck.get(Calendar.YEAR)) {
                if(now.get(Calendar.DAY_OF_YEAR) == timeToCheck.get(Calendar.DAY_OF_YEAR)) {

                    todayMeals.add(allMeals.get(i));

                }
            }

        }

    }

}
