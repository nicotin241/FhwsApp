package de.android.fhwsapp.Timetable;

import android.content.Context;
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
import android.widget.ProgressBar;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
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
import java.util.Locale;

import de.android.fhwsapp.MainActivity;
import de.android.fhwsapp.R;
import de.android.fhwsapp.Timetable.BTGridPager.BTFragmentGridPager;
import de.android.fhwsapp.Database;
import de.android.fhwsapp.fragments.MainFragment;

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

    private Context context;

    //    private LinearLayout addLayout;
    public static FloatingActionButton floatingActionButton;

    private boolean created;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        context = this;
        created = true;

        if(getIntent().getExtras() != null)
            created = getIntent().getExtras().getBoolean("from_filter");

        TypedValue typedValue = new TypedValue();
        TypedArray a = this.obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorAccent});
        colorAccent = a.getColor(0, 0);

        progressBar = (ProgressBar) findViewById(R.id.timeTableProgress);

    }


    private void markCurrentWeekTab(int week) {
        for (int i = 0; i < weekTabList.size(); i++) {
            if (i == week)
                weekTabList.get(i).setBackgroundColor(colorAccent);
            else
                weekTabList.get(i).setBackgroundColor(Color.GRAY);
        }
    }

    private void addWeekTabs() {
        weekTabs.setWeightSum(0);

        for (int i = 0; i < WEEKS; i++)
            addTab();
    }

    private void addTab() {
        weekTabs.setWeightSum(weekTabs.getWeightSum() + 1);

        ImageButton ib = new ImageButton(this, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, 0, 1);
        params.setMargins(0, 2, 0, 2);
        ib.setLayoutParams(params);
        ib.setBackgroundColor(Color.GRAY);

        //ib.setOnClickListener(this);

        weekTabs.addView(ib);
        weekTabList.add(ib);

    }

    private void markCurrentDayTab(int day) {

        tabMo.setBackgroundColor(Color.GRAY);
        tabDi.setBackgroundColor(Color.GRAY);
        tabMi.setBackgroundColor(Color.GRAY);
        tabDo.setBackgroundColor(Color.GRAY);
        tabFr.setBackgroundColor(Color.GRAY);
        tabSa.setBackgroundColor(Color.GRAY);
        tabSo.setBackgroundColor(Color.GRAY);

        if (day == 0)
            tabMo.setBackgroundColor(colorAccent);
        else if (day == 1)
            tabDi.setBackgroundColor(colorAccent);
        else if (day == 2)
            tabMi.setBackgroundColor(colorAccent);
        else if (day == 3)
            tabDo.setBackgroundColor(colorAccent);
        else if (day == 4)
            tabFr.setBackgroundColor(colorAccent);
        else if (day == 5)
            tabSa.setBackgroundColor(colorAccent);
        else if (day == 6)
            tabSo.setBackgroundColor(colorAccent);


    }

    private void initTabs() {
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

    public void moveToCurrentDay() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
        String currentDay = sdf.format(new Date());

        for (int w = 0; w < WEEKS; w++)
            for (int d = 0; d < DAYS; d++) {
                if (subjects[w][d].size() > 0)
                    if (subjects[w][d].get(0).getDate().contains(currentDay)) {
                        mFragmentGridPager.setStartDay(d, w);
                        this.currentDay = d;
                        currentWeek = w;
                        break;
                    }
            }
    }

    private void getLayoutHeight() {
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

                sharedPreferences.edit().putFloat(SCREEN_HEIGHT, oneHourMargin).commit();

                Intent intent = new Intent(getBaseContext(), TimetableFilter.class);
                startActivity(intent);
                //finish();
            }
        });
    }

    private ArrayList<Subject>[][] loadSubjects() {

        database = new Database(this);

        //dummy data
//        if (database.getWeekCount() == 0) {
//            database.createSubject(new Subject(1, "01.10.17", "10:00", "15:00", "S", "Programmieren 1", "Heinzl", "H.1.5", "", "Gruppe 1", "SS17", "BIN", "1", ""));
//            database.createSubject(new Subject(2, "01.10.17", "8:15", "9:45", "S", "Mathe", "Kuhn test test test", "I.2.15", "Raumänderung", "", "SS17", "BIN", "1", ""));
//            database.createSubject(new Subject(2, "02.10.17", "8:15", "10:15", "S", "Mathe", "Kuhn", "I.2.15", "", "", "SS17", "BIN", "1", ""));
//            database.createSubject(new Subject(3, "03.10.17", "13:30", "15:45", "S", "Programmieren 2", "Heinzl", "H.1.1", "", "", "SS17", "BIN", "1", ""));
//            database.createSubject(new Subject(1, "07.10.17", "10:00", "15:00", "S", "Programmieren 1", "Heinzl", "H.1.5", "", "Gruppe 1", "SS17", "BIN", "1", ""));
//            database.createSubject(new Subject(2, "07.10.17", "8:15", "9:45", "S", "Mathe", "Kuhn", "I.2.15", "", "", "SS17", "BIN", "1", ""));
//            database.createSubject(new Subject(2, "08.10.17", "8:15", "10:15", "S", "Mathe", "Kuhn", "I.2.15", "", "", "SS17", "BIN", "1", ""));
//            database.createSubject(new Subject(3, "09.10.17", "13:30", "15:45", "S", "Programmieren 2", "Heinzl", "H.1.1", "", "", "SS17", "BIN", "1", ""));
//            database.createSubject(new Subject(1, "15.10.17", "10:00", "15:00", "S", "Programmieren 1", "Heinzl", "H.1.5", "", "Gruppe 1", "SS17", "BIN", "1", ""));
//            database.createSubject(new Subject(2, "15.10.17", "8:15", "9:45", "S", "Mathe", "Kuhn", "I.2.15", "", "", "SS17", "BIN", "1", ""));
//            database.createSubject(new Subject(2, "16.10.17", "8:15", "10:15", "S", "Mathe", "Kuhn", "I.2.15", "", "", "SS17", "BIN", "1", ""));
//            database.createSubject(new Subject(2, "17.10.17", "13:30", "15:45", "S", "Programmieren 2", "Heinzl", "H.1.1", "", "", "SS17", "BIN", "1", ""));
//        }


        WEEKS = database.getWeekCount();

        ArrayList<Subject>[][] result = null;

        result = database.getSortedSubjects(DAYS, WEEKS);

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

    public class LoadSemesterFromServer extends AsyncTask<String, Void, String> {
        String server_response;

        @Override
        protected String doInBackground(String... strings) {

            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
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

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            ArrayList<Subject> subs = new ArrayList<>();

            try {
                JSONArray jsonArray = new JSONArray(server_response);
                database = new Database(context);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                    JSONArray events = jsonObject.getJSONArray("events");

                    for (int e = 0; e < events.length(); e++) {
                        Subject subject = new Subject();

                        JSONObject event = (JSONObject) events.get(e);

                        //id
                        int id = jsonObject.getInt("id");
                        subject.setId(id);

                        //look if checked
                        if(database.getSubjectWithID(id).isChecked())
                            subject.setChecked(true);

                        //endTime
                        String endTime = event.getString("endTime");
                        int indexTime = endTime.indexOf("T");
                        String day = endTime.substring(0, indexTime);
                        String time = endTime.substring(indexTime + 1, endTime.length() - 9);
                        subject.setTimeEnd(time);

                        try {
                            Date date = dateFormat.parse(day);
                            dateFormat.applyPattern("dd.MM.yy");
                            String d = dateFormat.format(date);
                            subject.setDate(d);

                        } catch (Exception ex) {
                            String[] dateArray = day.split("-");
                            String newDate = dateArray[2] + "." + dateArray[1] + "." + dateArray[0].substring(2);
                            subject.setDate(newDate);
                        }

                        //teacher
                        JSONArray lecturerView = (JSONArray) event.getJSONArray("lecturerView");
                        StringBuilder lecturers = new StringBuilder();
                        for (int y = 0; y < lecturerView.length(); y++) {
                            JSONObject lecturer = lecturerView.getJSONObject(y);
                            String teacher = null;
                            if (!lecturer.getString("title").equals(""))
                                teacher = lecturer.getString("title") + " " + lecturer.getString("lastName");
                            else
                                teacher = lecturer.getString("lastName");

                            if (y == 0)
                                lecturers.append(teacher);
                            else
                                lecturers.append(", " + teacher);

                        }
                        subject.setTeacher(lecturers.toString());

                        //name
                        String name = jsonObject.getString("name");
                        subject.setSubjectName(name);

                        //room
                        JSONArray roommsView = (JSONArray) event.getJSONArray("roomsView");
                        StringBuilder rooms = new StringBuilder();
                        for (int y = 0; y < roommsView.length(); y++) {
                            JSONObject room = roommsView.getJSONObject(y);

                            String raum = room.getString("name");

                            if (y == 0)
                                rooms.append(raum);
                            else
                                rooms.append(", " + raum);

                        }
                        subject.setRoom(rooms.toString());

                        //startTime
                        String startTime = event.getString("startTime");
                        indexTime = startTime.indexOf("T");
                        time = startTime.substring(indexTime + 1, startTime.length() - 9);
                        subject.setTimeStart(time);


                        //type
                        String type = event.getString("type");
                        subject.setType(type);

                        //semester, studiengang
                        JSONArray studentsView = (JSONArray) event.getJSONArray("studentsView");
                        for (int y = 0; y < studentsView.length(); y++) {
                            JSONObject students = studentsView.getJSONObject(y);

                            String programm = students.getString("program");
                            int semester = students.getInt("semester");

                            if (y == 0) {
                                subject.setStudiengang(programm);
                                subject.setSemester("" + semester);
                            } else {
                                Subject subject2 = new Subject(subject);
                                subject2.setStudiengang(programm);
                                subject2.setSemester("" + semester);
                                //database.createSubject(subject2);
                                subs.add(subject2);
                            }

                        }

                        //database.createSubject(subject);
                        subs.add(subject);

                    }
                }
            } catch (Exception e) {
                Log.e("Lesen der Vorlesungen",e.getMessage());
            }

            Log.e("Response", "" + server_response);

            //delete old subjects
            if(subs.size() > 0)
            database.deleteAllSubjects();

            //add new subjects
            for(Subject su : subs){
                database.createSubject(su);
            }

            init();

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
    public void onResume() {
        super.onResume();

        mFragmentGridPager = (BTFragmentGridPager) findViewById(R.id.pager);

        sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        oneHourMargin = sharedPreferences.getFloat(SCREEN_HEIGHT, 0);

        if (created && MainActivity.isNetworkConnected(this)) {
            //loadFromServer
            created = false;
            new LoadSemesterFromServer().execute("http://54.93.76.71:8080/FHWS/veranstaltungen?program=BIN");
        } else {
            //offline mode
            init();
        }
    }

    private void init(){
        subjects = loadSubjects();

        if(subjects.length == 0){
            Intent intent = new Intent(this, TimetableFilter.class);
            intent.putExtra("nothing checked",true);
            startActivity(intent);
            return;
        }

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

                if (index.getCol() != currentDay) {
                    markCurrentDayTab(mFragmentGridPager.mCurrentIndex.getCol());
                    currentDay = index.getCol();
                }
                if (index.getRow() != currentWeek) {
                    markCurrentWeekTab(mFragmentGridPager.mCurrentIndex.getRow());
                    currentWeek = index.getRow();
                }

                ContentFragment fragment = new ContentFragment();
                try {
                    fragment.loadData(subjects[index.getRow()][index.getCol()]);
                } catch (Exception e) {
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
                if (floatingActionButton.getTag().equals("plus")) {

                    Intent intent = new Intent(Timetable.this, TimetableFilter.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_up, R.anim.slide_down);

                } else {
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
                                    for (String name : names)
                                        if (ContentFragment.markedTv.getText().toString().contains(name)) {
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
        if (oneHourMargin == 0) {
            getLayoutHeight();
        }

        progressBar.setVisibility(View.GONE);
    }
}

