package de.android.fhwsapp.Timetable;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import de.android.fhwsapp.Database;
import de.android.fhwsapp.R;
import de.android.fhwsapp.Timetable.nLevelExpandableListview.NLevelAdapter;
import de.android.fhwsapp.Timetable.nLevelExpandableListview.NLevelItem;
import de.android.fhwsapp.Timetable.nLevelExpandableListview.NLevelView;

public class TimetableFilter extends AppCompatActivity {

    private List<NLevelItem> list;
    private ListView listView;
    private Database database;
    private Button addSubject;
    private Button done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable_filter);

        setTitle("Wähle deine Fächer");

        listView = (ListView) findViewById(R.id.lvNExp);
        database = new Database(this);

        addSubject = (Button) findViewById(R.id.btnAddCustomSubject);
        addSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TimetableFilter.this, AddSubject.class);
                startActivity(intent);
            }
        });

        done = (Button) findViewById(R.id.btnDone);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    private void prepareListData() {

        final LayoutInflater inflater = LayoutInflater.from(this);

        list = new ArrayList<NLevelItem>();

        //Semester
        List<String> gParentList = database.getDistinctYears();

        for (int i = 0; i < gParentList.size(); i++) {

            final NLevelItem grandParent = new NLevelItem(gParentList.get(i), null, new NLevelView() {

                @Override
                public View getView(NLevelItem item) {
                    View view = inflater.inflate(R.layout.list_group, null);
                    view.findViewById(R.id.level1).setVisibility(View.GONE);
                    view.findViewById(R.id.level2).setVisibility(View.GONE);
                    TextView tv = (TextView) view.findViewById(R.id.lblListHeader);
                    String name = item.getWrappedObject().toString();
                    tv.setText(name);
                    return view;
                }
            });
            list.add(grandParent);


            //custom Fächer
            if (gParentList.get(i).equals("Custom")) {
                final List<Subject> customList = database.getSubjectsWithType("Custom");

                for (int j = 0; j < customList.size(); j++) {
                    NLevelItem parent = new NLevelItem(customList.get(j), grandParent, new NLevelView() {

                        @Override
                        public View getView(NLevelItem item) {

                            final Subject subject = ((Subject) item.getWrappedObject());


                            View view = inflater.inflate(R.layout.custom_list_item, null);

                            TextView tv = (TextView) view.findViewById(R.id.tvSubjectName);
                            String name = subject.getSubjectName();
                            tv.setText(name);

                            final CheckBox cb = (CheckBox) view.findViewById(R.id.cbAbo);
                            if (subject.isChecked())
                                cb.setChecked(true);
                            else
                                cb.setChecked(false);

                            tv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    cb.toggle();
                                    database.updateCheckedSubjects(subject, cb.isChecked());
                                    subject.setChecked(cb.isChecked());
                                }
                            });

                            cb.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    database.updateCheckedSubjects(subject, cb.isChecked());
                                    subject.setChecked(cb.isChecked());
                                }
                            });

                            view.findViewById(R.id.btnEdit).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(TimetableFilter.this, AddSubject.class);
                                    intent.putExtra("Subject", subject.getSubjectName());
                                    intent.putExtra("Date", subject.getDate());
                                    startActivity(intent);
                                }
                            });

                            view.findViewById(R.id.btnDelete).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new AlertDialog.Builder(TimetableFilter.this)
                                            .setTitle("Fach Löschen")
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    database.deleteSingleSubject(subject);
                                                    dialog.dismiss();
                                                    init();
                                                }
                                            })
                                            .setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .show();
                                }
                            });

                            return view;
                        }
                    });
                    list.add(parent);
                }

            } else {
                //Studiengaenge
                final List<String> parentList = database.getDistinctStudiengangOfYear(gParentList.get(i));

                for (int j = 0; j < parentList.size(); j++) {
                    NLevelItem parent = new NLevelItem(parentList.get(j), grandParent, new NLevelView() {

                        @Override
                        public View getView(NLevelItem item) {
                            View view = inflater.inflate(R.layout.list_group, null);
                            view.findViewById(R.id.level1).setVisibility(View.VISIBLE);
                            view.findViewById(R.id.level2).setVisibility(View.GONE);
                            TextView tv = (TextView) view.findViewById(R.id.lblListHeader);
                            String name = item.getWrappedObject().toString();
                            tv.setText(name);
                            return view;
                        }
                    });
                    list.add(parent);

                    //Semester
                    List<String> childList = database.getDistinctSemesterOfYaS(gParentList.get(i), parentList.get(j));

                    for (int k = 0; k < childList.size(); k++) {
                        NLevelItem child = new NLevelItem(childList.get(k), parent, new NLevelView() {

                            @Override
                            public View getView(NLevelItem item) {
                                View view = inflater.inflate(R.layout.list_group, null);
                                TextView tv = (TextView) view.findViewById(R.id.lblListHeader);
                                view.findViewById(R.id.level1).setVisibility(View.VISIBLE);
                                view.findViewById(R.id.level2).setVisibility(View.VISIBLE);
                                String name = "Semester: " + item.getWrappedObject().toString();
                                tv.setText(name);
                                return view;
                            }
                        });
                        list.add(child);

                        //Fächer
                        List<Subject> grandChildList = database.getDistinctSubjectsOfYaSaS(gParentList.get(i), parentList.get(j), childList.get(j));

                        for (int l = 0; l < grandChildList.size(); l++) {
                            NLevelItem grandChild = new NLevelItem(grandChildList.get(l), child, new NLevelView() {

                                @Override
                                public View getView(final NLevelItem item) {
                                    final Subject subject = ((Subject) item.getWrappedObject());

                                    View view = inflater.inflate(R.layout.list_item, null);
                                    TextView tv = (TextView) view.findViewById(R.id.tvSubjectName);
                                    String name = subject.getSubjectName();
                                    tv.setText(name);

                                    final CheckBox cb = (CheckBox) view.findViewById(R.id.cbAbo);
                                    if (subject.isChecked())
                                        cb.setChecked(true);
                                    else
                                        cb.setChecked(false);

                                    tv.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            cb.toggle();
                                            database.updateCheckedSubjects(subject.getId(), cb.isChecked());
                                            subject.setChecked(cb.isChecked());
                                        }
                                    });

                                    cb.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            database.updateCheckedSubjects(subject.getId(), cb.isChecked());
                                            subject.setChecked(cb.isChecked());
                                        }
                                    });
                                    return view;
                                }
                            });
                            list.add(grandChild);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
    }

    @Override
    public void onResume(){
        super.onResume();

        init();
    }

    public void init(){
        prepareListData();

        NLevelAdapter adapter = new NLevelAdapter(list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                ((NLevelAdapter)listView.getAdapter()).toggle(arg2);
                ((NLevelAdapter)listView.getAdapter()).getFilter().filter();

            }
        });
    }

}
