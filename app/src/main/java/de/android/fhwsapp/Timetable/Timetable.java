package de.android.fhwsapp.Timetable;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.android.fhwsapp.R;
import de.android.fhwsapp.Timetable.BTGridPager.BTFragmentGridPager;
import de.android.fhwsapp.Database;

public class Timetable extends FragmentActivity {

    private BTFragmentGridPager.FragmentGridPagerAdapter mFragmentGridPagerAdapter;

    private static int DAYS = 7;
    private static int WEEKS = 0;
    private static final String SCREEN_HEIGHT = "screenHeight";
    public static float oneHourMargin;

    private ImageButton tabMo, tabDi, tabMi, tabDo, tabFr, tabSa, tabSo;
    private ArrayList<ImageButton> weekTabList;

    private LinearLayout linearLayout;
    private ArrayList<Subject>[][] subjects;
    private BTFragmentGridPager mFragmentGridPager;

    private SharedPreferences sharedPreferences;

    private int currentDay = 0;
    private int currentWeek = 0;

    private LinearLayout weekTabs;

    private int colorAccent;

    private Database database;

//    private LinearLayout addLayout;
    public static FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        TypedValue typedValue = new TypedValue();
        TypedArray a = this.obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorAccent });
        colorAccent = a.getColor(0, 0);

    }


    private void markCurrentWeekTab(int week){
        for(int i = 0; i < weekTabList.size(); i++){
            if(i == week)
                weekTabList.get(i).setBackgroundColor(colorAccent);
            else
                weekTabList.get(i).setBackgroundColor(Color.GRAY);
        }
    }

    private void addWeekTabs(){
        weekTabs.setWeightSum(0);

        for(int i = 0; i < WEEKS; i++)
            addTab();
    }

    private void addTab(){
        weekTabs.setWeightSum(weekTabs.getWeightSum()+1);

        ImageButton ib = new ImageButton(this,null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, 0, 1);
        params.setMargins(0,2,0,2);
        ib.setLayoutParams(params);
        ib.setBackgroundColor(Color.GRAY);

        //ib.setOnClickListener(this);

        weekTabs.addView(ib);
        weekTabList.add(ib);

    }

    private void markCurrentDayTab(int day){

        tabMo.setBackgroundColor(Color.GRAY);
        tabDi.setBackgroundColor(Color.GRAY);
        tabMi.setBackgroundColor(Color.GRAY);
        tabDo.setBackgroundColor(Color.GRAY);
        tabFr.setBackgroundColor(Color.GRAY);
        tabSa.setBackgroundColor(Color.GRAY);
        tabSo.setBackgroundColor(Color.GRAY);

        if(day == 0)
            tabMo.setBackgroundColor(colorAccent);
        else if(day == 1)
            tabDi.setBackgroundColor(colorAccent);
        else if(day == 2)
            tabMi.setBackgroundColor(colorAccent);
        else if(day == 3)
            tabDo.setBackgroundColor(colorAccent);
        else if(day == 4)
            tabFr.setBackgroundColor(colorAccent);
        else if(day == 5)
            tabSa.setBackgroundColor(colorAccent);
        else if(day == 6)
            tabSo.setBackgroundColor(colorAccent);


    }

    private void initTabs(){
        tabMo = (ImageButton) findViewById(R.id.ibMo);
//        tabMo.setOnClickListener(this);
        tabDi = (ImageButton) findViewById(R.id.ibDi);
//        tabDi.setOnClickListener(this);
        tabMi = (ImageButton) findViewById(R.id.ibMi);
//        tabMi.setOnClickListener(this);
        tabDo = (ImageButton) findViewById(R.id.ibDo);
//        tabDo.setOnClickListener(this);
        tabFr = (ImageButton) findViewById(R.id.ibFr);
//        tabFr.setOnClickListener(this);
        tabSa = (ImageButton) findViewById(R.id.ibSa);
//        tabSa.setOnClickListener(this);
        tabSo = (ImageButton) findViewById(R.id.ibSo);
    }

    public void moveToCurrentDay(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
        String currentDay = sdf.format(new Date());

        for(int w = 0; w < WEEKS; w++)
            for(int d = 0; d < DAYS; d++){
                if(subjects[w][d].size() >0)
                if(subjects[w][d].get(0).getDate().contains(currentDay)){
                    mFragmentGridPager.setStartDay(d, w);
                    this.currentDay = d;
                    currentWeek = w;
                    break;
                }
            }
    }

    private void getLayoutHeight(){
        linearLayout = (LinearLayout) findViewById(R.id.layoutHeight);

        ViewTreeObserver vto = linearLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    linearLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    linearLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                oneHourMargin = linearLayout.getMeasuredHeight() / 13;

                sharedPreferences.edit().putFloat(SCREEN_HEIGHT,oneHourMargin).commit();

                Intent intent = new Intent(getBaseContext(), TimetableFilter.class);
                startActivity(intent);
                //finish();
            }
        });
    }

    private ArrayList<Subject>[][] loadSubjects(){

        //return loadFromServer();
        new LoadSemesterFromServer().execute("http://backend2.applab.fhws.de:8080/fhwsapi/v1/lectures");

        database = new Database(this);
        if(database.getWeekCount() == 0){
            database.createSubject(new Subject(1,"01.05.17","10:00","15:00","S","Programmieren 1","Heinzl","H.1.5","","Gruppe 1","SS17","BIN","1",""));
            database.createSubject(new Subject(2,"01.05.17","8:15","9:45","S","Mathe","Kuhn","I.2.15","Raumänderung","","SS17","BIN","1",""));
            database.createSubject(new Subject(2,"02.05.17","8:15","10:15","S","Mathe","Kuhn","I.2.15","","","SS17","BIN","1",""));
            database.createSubject(new Subject(3,"03.05.17","13:30","15:45","S","Programmieren 2","Heinzl","H.1.1","","","SS17","BIN","1",""));
            database.createSubject(new Subject(1,"07.05.17","10:00","15:00","S","Programmieren 1","Heinzl","H.1.5","","Gruppe 1","SS17","BIN","1",""));
            database.createSubject(new Subject(2,"07.05.17","8:15","9:45","S","Mathe","Kuhn","I.2.15","","","SS17","BIN","1",""));
            database.createSubject(new Subject(2,"08.05.17","8:15","10:15","S","Mathe","Kuhn","I.2.15","","","SS17","BIN","1",""));
            database.createSubject(new Subject(3,"09.05.17","13:30","15:45","S","Programmieren 2","Heinzl","H.1.1","","","SS17","BIN","1",""));
            database.createSubject(new Subject(1,"15.05.17","10:00","15:00","S","Programmieren 1","Heinzl","H.1.5","","Gruppe 1","SS17","BIN","1",""));
            database.createSubject(new Subject(2,"15.05.17","8:15","9:45","S","Mathe","Kuhn","I.2.15","","","SS17","BIN","1",""));
            database.createSubject(new Subject(2,"16.05.17","8:15","10:15","S","Mathe","Kuhn","I.2.15","","","SS17","BIN","1",""));
            database.createSubject(new Subject(2,"17.05.17","13:30","15:45","S","Programmieren 2","Heinzl","H.1.1","","","SS17","BIN","1",""));
        }


        WEEKS = database.getWeekCount();

        ArrayList<Subject>[][] result = database.getSortedSubjects(DAYS, WEEKS);

        return result;
    }

    //tab Click
//    @Override
//    public void onClick(View v) {
//        switch(v.getId()){
//            case R.id.ibMo:
//                mFragmentGridPager.setCurrentItem(0);
//                break;
//            case R.id.ibDi:
//                mFragmentGridPager.setCurrentItem(1);
//                break;
//            case R.id.ibMi:
//                mFragmentGridPager.setCurrentItem(2);
//                break;
//            case R.id.ibDo:
//                mFragmentGridPager.setCurrentItem(3);
//                break;
//            case R.id.ibFr:
//                mFragmentGridPager.setCurrentItem(4);
//                break;
//            case R.id.ibSa:
//                mFragmentGridPager.setCurrentItem(5);
//                break;
//        }
//    }

    public class LoadSemesterFromServer extends AsyncTask<String , Void ,String> {
        String server_response;

        @Override
        protected String doInBackground(String... strings) {

            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                int responseCode = urlConnection.getResponseCode();

                if(responseCode == HttpURLConnection.HTTP_OK){
                    server_response = readStream(urlConnection.getInputStream());
                    Log.v("CatalogClient", server_response);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONArray jsonArray = new JSONArray(server_response);
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    String semester = jsonObject.getString("label");
                    String url = jsonObject.getString("url");
                }
            }catch (Exception e){}

            Log.e("Response", "" + server_response);


        }
    }

// Converting InputStream to String

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    @Override
    public void onResume(){
        super.onResume();

        mFragmentGridPager = (BTFragmentGridPager) findViewById(R.id.pager);

        sharedPreferences = getSharedPreferences(getPackageName(),MODE_PRIVATE);
        oneHourMargin = sharedPreferences.getFloat(SCREEN_HEIGHT,0);

        subjects = loadSubjects();

        weekTabList = new ArrayList<>();

        initTabs();

        moveToCurrentDay();

        mFragmentGridPagerAdapter = new BTFragmentGridPager.FragmentGridPagerAdapter() {
            @Override
            public int rowCount() {
                return WEEKS;
            }

            @Override
            public int columnCount(int row) {
                return DAYS;
            }

            @Override
            public Fragment getItem(BTFragmentGridPager.GridIndex index) {

                if(index.getCol() != currentDay) {
                    markCurrentDayTab(mFragmentGridPager.mCurrentIndex.getCol());
                    currentDay = index.getCol();
                }
                if(index.getRow() != currentWeek){
                    markCurrentWeekTab(mFragmentGridPager.mCurrentIndex.getRow());
                    currentWeek = index.getRow();
                }

                ContentFragment fragment = new ContentFragment();
                try {
                    fragment.loadData(subjects[index.getRow()][index.getCol()]);
                }catch (Exception e){
                    //bei absoluten Notfall
                    onBackPressed();
                }
                return fragment;
            }
        };

        mFragmentGridPager.setGridPagerAdapter(mFragmentGridPagerAdapter);

        weekTabs = (LinearLayout) findViewById(R.id.llWeeks);
        weekTabs.removeAllViews();
        addWeekTabs();

        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(floatingActionButton.getTag().equals("plus")){

                    Intent intent = new Intent(Timetable.this, TimetableFilter.class );
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_up, R.anim.slide_down);

                }else{
                    new AlertDialog.Builder(Timetable.this)
                            .setTitle("Fach entfernen")
                            .setMessage("Willst du das Fach wirklich aus deinem Stundenplan nehmen")
                            .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    ContentFragment.markedTv.clearAnimation();

                                    if (ContentFragment.markedTv != null)
                                        ContentFragment.markedTv.setBackgroundColor(ContentFragment.markedTv.getHighlightColor());

                                    floatingActionButton.setImageResource(R.drawable.plus);
                                    floatingActionButton.setTag("plus");

                                    String subject = "";

                                    List<String> names = database.getAllSubjectNames();
                                    for(String name : names)
                                        if(ContentFragment.markedTv.getText().toString().contains(name)){
                                            subject = name;
                                            break;
                                        }


                                    Subject update = database.getSubjectWithName(subject);

                                    database.updateCheckedSubjects(update.getId(), false);

                                    ContentFragment.markedTv = null;

                                    subjects = loadSubjects();
                                    mFragmentGridPager.setGridPagerAdapter(mFragmentGridPagerAdapter);
                                }
                            })
                            .setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    ContentFragment.markedTv.clearAnimation();

                                    if (ContentFragment.markedTv != null)
                                        ContentFragment.markedTv.setBackgroundColor(ContentFragment.markedTv.getHighlightColor());

                                    ContentFragment.markedTv = null;

                                    Timetable.floatingActionButton.setImageResource(R.drawable.plus);
                                    Timetable.floatingActionButton.setTag("plus");

                                    dialog.dismiss();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        });
        if(oneHourMargin == 0) {
            getLayoutHeight();
        }

    }
}

