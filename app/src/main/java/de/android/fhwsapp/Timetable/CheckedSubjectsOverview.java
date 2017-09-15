package de.android.fhwsapp.Timetable;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.android.fhwsapp.Database;
import de.android.fhwsapp.R;

public class CheckedSubjectsOverview extends AppCompatActivity {

    private Database database;
    private ListView listView;
    private Context context;
    private HashMap<Integer, ImageView> selectedViews;
    private List<Subject> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checked_subjects_overview);

        listView = (ListView) findViewById(R.id.lvSubjects);

        database = new Database(this);

        context = this;

        selectedViews = new HashMap<>();
        data = database.getAllCheckedSubjects();

        SubjectOverviewListAdapter arrayAdapter = new SubjectOverviewListAdapter(this, database.getAllCheckedSubjects());
        listView.setAdapter(arrayAdapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                ImageView ivDelete = (ImageView) view.findViewById(R.id.ivDelete);

                selectedViews.clear();
                selectedViews.put(position, ivDelete);

                ivDelete.setVisibility(View.VISIBLE);
                ivDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        database.updateAllCheckedSubjectsWithName(data.get(position).getSubjectName(), false);
                        //reload listview
                        SubjectOverviewListAdapter arrayAdapter = new SubjectOverviewListAdapter(context, database.getAllCheckedSubjects());
                        listView.setAdapter(arrayAdapter);
                    }
                });
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!selectedViews.containsKey(position)) {
                    for (ImageView iv : selectedViews.values())
                        iv.setVisibility(View.GONE);
                }
            }
        });
    }
}
