package de.android.fhwsapp.Timetable;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import de.android.fhwsapp.Database;
import de.android.fhwsapp.R;

public class CheckedSubjectsOverview extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checked_subjects_overview);

        ListView listView = (ListView) findViewById(R.id.lvSubjects);

        Database database = new Database(this);

        SubjectOverviewListAdapter arrayAdapter = new SubjectOverviewListAdapter(this, database.getAllCheckedSubjects());
        listView.setAdapter(arrayAdapter);
    }
}
