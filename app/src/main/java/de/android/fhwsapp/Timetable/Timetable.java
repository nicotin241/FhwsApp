package de.android.fhwsapp.Timetable;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.android.fhwsapp.R;
import de.android.fhwsapp.Timetable.BTGridPager.BTFragmentGridPager;

public class Timetable extends FragmentActivity {

    private BTFragmentGridPager.FragmentGridPagerAdapter mFragmentGridPagerAdapter;

    private static int DAYS = 6;
    private static int WEEKS = 3;
    private static final String SCREEN_HEIGHT = "screenHeight";
    public static float oneHourMargin;

    private ImageButton tabMo, tabDi, tabMi, tabDo, tabFr, tabSa;
    private ArrayList<ImageButton> weekTabList;

    private LinearLayout linearLayout;
//    private ListView listView;
    private ArrayList<Subject>[][] subjects;
    private BTFragmentGridPager mFragmentGridPager;

//    private Animation slideUp;
//    private Animation slideDown;

    private SharedPreferences sharedPreferences;

    private int currentDay = 0;
    private int currentWeek = 0;

    private LinearLayout weekTabs;

    private int colorAccent;

//    private LinearLayout addLayout;
    public static FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        TypedValue typedValue = new TypedValue();
        TypedArray a = this.obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorAccent });
        colorAccent = a.getColor(0, 0);

        sharedPreferences = getSharedPreferences(getPackageName(),MODE_PRIVATE);
        oneHourMargin = sharedPreferences.getFloat(SCREEN_HEIGHT,0);

        mFragmentGridPager = (BTFragmentGridPager) findViewById(R.id.pager);

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
                fragment.loadData(subjects[index.getRow()][index.getCol()]);
                return fragment;
            }
        };

        mFragmentGridPager.setGridPagerAdapter(mFragmentGridPagerAdapter);

        weekTabs = (LinearLayout) findViewById(R.id.llWeeks);
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
                                    Toast.makeText(getApplicationContext(),"kann noch nicht gelöscht werden",Toast.LENGTH_LONG).show();
                                }
                            })
                            .setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
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


    private void markCurrentWeekTab(int week){
        for(int i = 0; i < weekTabList.size(); i++){
            if(i == week)
                weekTabList.get(i).setBackgroundColor(colorAccent);
            else
                weekTabList.get(i).setBackgroundColor(Color.GRAY);
        }
    }

    private void addWeekTabs(){
        for(int i = 0; i < WEEKS; i++)
            addTab();
    }

    private void addTab(){
        weekTabs.setWeightSum(weekTabs.getWeightSum()+1);

        ImageButton ib = new ImageButton(this,null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(5, 0, 1);
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

                Intent intent = new Intent(getBaseContext(), Timetable.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private ArrayList<Subject>[][] loadSubjects(){
        //hier muss vorher die anzahl der tage berechnet werden
        ArrayList<Subject>[][] result = new ArrayList[WEEKS][DAYS];

        for(int w = 0; w < WEEKS; w++)
            for(int d = 0; d < DAYS; d++)
                result[w][d]= new ArrayList<>();

        result[0][0].add(0, new Subject(1,"Mo: 01.05.17","10:00","15:00","S","Programmieren 1","Heinzl","H.1.5",""));
        result[0][0].add(1,new Subject(2,"Mo: 01.05.17","8:15","9:45","S","Mathe","Kuhn","I.2.15","Raumänderung"));

        result[0][1].add(0,new Subject(3,"Di: 02.05.17","8:15","10:15","S","Mathe","Kuhn","I.2.15",""));

        result[0][2].add(0,new Subject(4,"Mi: 03.05.17","13:30","15:45","S","Programmieren 2","Heinzl","H.1.1",""));


        result[1][0].add(0, new Subject(5,"Mo: 04.05.17","10:00","15:00","S","Programmieren 1","Heinzl","H.1.5",""));
        result[1][0].add(1,new Subject(6,"Mo: 04.05.17","8:15","9:45","S","Mathe","Kuhn","I.2.15","Raumänderung"));

        result[1][1].add(0,new Subject(7,"Di: 05.05.17","8:15","10:15","S","Mathe","Kuhn","I.2.15",""));

        result[1][2].add(0,new Subject(8,"Mi: 06.06.17","13:30","15:45","S","Programmieren 2","Heinzl","H.1.1",""));


        result[2][0].add(0, new Subject(9,"Mo: 07.05.17","10:00","15:00","S","Programmieren 1","Heinzl","H.1.5",""));
        result[2][0].add(1,new Subject(10,"Mo: 07.05.17","8:15","9:45","S","Mathe","Kuhn","I.2.15","Raumänderung"));

        result[2][1].add(0,new Subject(11,"Di: 08.05.17","8:15","10:15","S","Mathe","Kuhn","I.2.15",""));

        result[2][2].add(0,new Subject(12,"Mi: 09.05.17","13:30","15:45","S","Programmieren 2","Heinzl","H.1.1",""));



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
}

