package de.android.fhwsapp.Timetable;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.android.fhwsapp.R;

public class TimetableFilter extends AppCompatActivity {


    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private List<String> listDataHeader;
    private HashMap<String, List<Subject>> listDataChild;
    private Button addSubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable_filter);

        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        prepareListData();

        listAdapter = new de.android.fhwsapp.Timetable.ExpandableListAdapter(this, listDataHeader, listDataChild);

        expListView.setAdapter(listAdapter);

        addSubject = (Button) findViewById(R.id.btnAddCustomSubject);
        addSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TimetableFilter.this, AddSubject.class);
                startActivity(intent);
            }
        });

    }

    private void prepareListData(){
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<Subject>>();

        // Adding child data
        listDataHeader.add("Programmieren");
        listDataHeader.add("Mathe");

        // Adding child data
        List<Subject> programmieren = new ArrayList<Subject>();
        programmieren.add(new Subject(1,"Mo: 07.05.17","10:00","15:00","S","Programmieren 1","Heinzl","H.1.5",""));
        programmieren.add(new Subject(2,"Mo: 07.05.17","10:00","15:00","S","Programmieren 2","Heinzl","H.1.5",""));
        programmieren.add(new Subject(3,"Mo: 07.05.17","10:00","15:00","S","Programmieren 3","Heinzl","H.1.5",""));

        List<Subject> mathe = new ArrayList<Subject>();
        mathe.add(new Subject(4,"Mo: 07.05.17","10:00","15:00","S","Mathe 1","Heinzl","H.1.5",""));
        mathe.add(new Subject(5,"Mo: 07.05.17","10:00","15:00","S","Mathe 2","Heinzl","H.1.5",""));
        mathe.add(new Subject(6,"Mo: 07.05.17","10:00","15:00","S","Mathe 3","Heinzl","H.1.5",""));


        listDataChild.put(listDataHeader.get(0), programmieren); // Header, Child data
        listDataChild.put(listDataHeader.get(1), mathe);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
    }

}
