package de.android.fhwsapp.fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import de.android.fhwsapp.Database;
import de.android.fhwsapp.R;
import de.android.fhwsapp.adapter.MealListAdapter;
import de.android.fhwsapp.adapter.MensaListAdapter;
import de.android.fhwsapp.objects.Meal;
import de.android.fhwsapp.objects.Mensa;

public class MensaFragment extends Fragment {

    private View view;
    private FrameLayout mensa_select_layout;

    private SharedPreferences mPrefs;
    private SharedPreferences.Editor editor;

    private Database database;
    private Context mContext;

    private ArrayList<Meal> allMeals;
    private ArrayList<Meal> todayMeals;

    private ListView mListView;
    private MealListAdapter mAdapter;

    private ListView mensa_list;
    private MensaListAdapter mensaListAdapter;

    private int mensaId;

    private TextView title;
    private TextView date;
    private TextView mensa_no_data;
    private CheckBox saveMensaCheckBox;
    private FloatingActionButton fab;

    private ArrayList<Mensa> mensaItems;

    public MensaFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_mensa, container, false);

        mContext = getContext();
        database = new Database(mContext);
        mensaItems = Mensa.getAllMensas();

        mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor = mPrefs.edit();

        initLayout();

        //TODO: Animation is not working first time - WHY?
        mensa_select_layout.animate()
                .translationY(mensa_select_layout.getHeight())
                .setDuration(300);

        mensa_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                mensaId = mensaItems.get(i).getMensaId();

                initMealsList();

                mensa_select_layout.animate()
                        .translationY(mensa_select_layout.getHeight())
                        .setDuration(300);

                fab.setVisibility(View.VISIBLE);

                if(saveMensaCheckBox.isChecked()) {

                    editor.putInt("MENSAID", mensaId).apply();

                }

            }
        });

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                mensa_select_layout.animate()
                        .translationY(0)
                        .setDuration(300);
                mensa_select_layout.setVisibility(View.VISIBLE);

                fab.setVisibility(View.GONE);

            }
        });

        mensaId = mPrefs.getInt("MENSAID", -1);

        if(mensaId != -1) {

            initMealsList();

        } else {

            mensa_select_layout.setVisibility(View.VISIBLE);
            fab.setVisibility(View.GONE);

        }

        return view;
    }

    private void initLayout() {

        mensa_select_layout = (FrameLayout) view.findViewById(R.id.mensa_select_layout);
        mensa_list = (ListView) view.findViewById(R.id.mensa_item_list);
        mListView = (ListView) view.findViewById(R.id.mealList);
        saveMensaCheckBox = (CheckBox) view.findViewById(R.id.saveMensaCheckBox);

        title = (TextView) view.findViewById(R.id.mensa_title);
        date = (TextView) view.findViewById(R.id.mensa_date);
        mensa_no_data = (TextView) view.findViewById(R.id.mensa_no_data);

        mensaListAdapter = new MensaListAdapter(getContext());
        mensa_list.setAdapter(mensaListAdapter);

    }

    private void getTodayMeals() {

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

    private void setTitleAndDate() {

        for(Mensa mensa : mensaItems) {

            if(mensa.getMensaId() == mensaId) title.setText("Speiseplan für " + mensa.getName());

        }

        date.setText(new SimpleDateFormat("EEEE, dd.MM").format(new Date()));

    }

    private void initMealsList() {

        setTitleAndDate();

        allMeals = database.getMealsById(mensaId);

        getTodayMeals();

        if(todayMeals.size() > 0) {

            if(mensa_no_data.getVisibility() == View.VISIBLE) {

                mListView.setVisibility(View.VISIBLE);
                mensa_no_data.setVisibility(View.GONE);

            }
            mAdapter = new MealListAdapter(mContext, todayMeals);
            mListView.setAdapter(mAdapter);

        } else {

            mListView.setVisibility(View.GONE);
            mensa_no_data.setVisibility(View.VISIBLE);

        }



    }

}
