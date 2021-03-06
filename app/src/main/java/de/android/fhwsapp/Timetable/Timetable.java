package de.android.fhwsapp.Timetable;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.android.fhwsapp.Database;
import de.android.fhwsapp.MainActivity;
import de.android.fhwsapp.R;
import de.android.fhwsapp.Timetable.BTGridPager.BTFragmentGridPager;
import de.android.fhwsapp.connection.Connect;
import de.android.fhwsapp.connection.ConnectionListener;
import de.android.fhwsapp.objects.Subject;
import de.android.fhwsapp.servertasks.NutzungsdatenTransfer;

public class Timetable extends FragmentActivity {

    private BTFragmentGridPager.FragmentGridPagerAdapter mFragmentGridPagerAdapter;

    private static int DAYS = 7;
    private static int WEEKS = 0;
    private static final String SCREEN_HEIGHT = "screenHeight";
    public static float oneHourMargin;

    public static boolean isOpen = false;
    public static boolean isLoading = false;

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

    public static FloatingActionMenu floatingActionButton;
    private FloatingActionButton fabVorlesungsAuswahl, fabMeineVorlesungen;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        isOpen = true;

        TypedValue typedValue = new TypedValue();
        TypedArray a = this.obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorAccent});
        colorAccent = a.getColor(0, 0);

        progressBar = (ProgressBar) findViewById(R.id.timeTableProgress);

        if (!MainActivity.isNetworkConnected(this))
            progressBar.setVisibility(View.GONE);

        new NutzungsdatenTransfer(this).execute("veranstaltungen");

        //wird nach aufgerufen wenn Vorlesunge fertig geladen wurden
        Connect.addListener(new ConnectionListener() {
            @Override
            public void onChanged() {
                if (isOpen) {
                    Log.e("Timetable", "onChanged");
                    init();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        isOpen = false;
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
        tabDi = (ImageButton) findViewById(R.id.ibDi);
        tabMi = (ImageButton) findViewById(R.id.ibMi);
        tabDo = (ImageButton) findViewById(R.id.ibDo);
        tabFr = (ImageButton) findViewById(R.id.ibFr);
        tabSa = (ImageButton) findViewById(R.id.ibSa);
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

                Intent intent = new Intent(getBaseContext(), Timetable.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private ArrayList<Subject>[][] loadSubjects() {

        database = new Database(this);

        WEEKS = database.getWeekCount();

        ArrayList<Subject>[][] result = null;

        result = database.getSortedSubjects(DAYS, WEEKS);

        return result;
    }

    @Override
    public void onResume() {
        super.onResume();

        mFragmentGridPager = (BTFragmentGridPager) findViewById(R.id.pager);

        sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        oneHourMargin = sharedPreferences.getFloat(SCREEN_HEIGHT, 0);

        init();
    }

    private void init() {
        subjects = loadSubjects();

        if (subjects.length == 0) {

            Toast.makeText(this, "Wähle deine Vorlesungen", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(this, TimetableFilter.class);
            intent.putExtra("nothing checked", true);
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
                    Log.e("Timetable", "Fehler bei fragment.loadData");
                    if (isOpen)
                        onBackPressed();
                }
                return fragment;
            }
        };

        try {
            mFragmentGridPager.setGridPagerAdapter(mFragmentGridPagerAdapter);
        } catch (Exception e) {
            Log.e("Timetable", "Fehler bei mFragmentGridPager.setGridPagerAdapter");
            if (isOpen)
                onBackPressed();
        }

        weekTabs = (LinearLayout) findViewById(R.id.llWeeks);
        weekTabs.removeAllViews();
        addWeekTabs();

        fabVorlesungsAuswahl = (FloatingActionButton) findViewById(R.id.fabVorlesungsAuswahl);
        fabVorlesungsAuswahl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Timetable.this, TimetableFilter.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);

            }
        });
        fabMeineVorlesungen = (FloatingActionButton) findViewById(R.id.fabMeineVorlesungen);
        fabMeineVorlesungen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Timetable.this, CheckedSubjectsOverview.class);
                startActivity(intent);
            }
        });

        floatingActionButton = (FloatingActionMenu) findViewById(R.id.floatingActionButton);
        floatingActionButton.setClosedOnTouchOutside(true);

        if (oneHourMargin == 0) {
            getLayoutHeight();
        }

        if (!isLoading)
            progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        this.finish();
        this.overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    public void backToMain(View view) {

        this.finish();
        this.overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);

    }

}

