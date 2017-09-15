package de.android.fhwsapp.Timetable;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.android.fhwsapp.Database;
import de.android.fhwsapp.MainActivity;
import de.android.fhwsapp.R;
import de.android.fhwsapp.Timetable.nLevelExpandableListview.NLevelAdapter;
import de.android.fhwsapp.Timetable.nLevelExpandableListview.NLevelItem;
import de.android.fhwsapp.Timetable.nLevelExpandableListview.NLevelView;
import de.android.fhwsapp.connection.Connect;
import de.android.fhwsapp.connection.ConnectionListener;
import de.android.fhwsapp.objects.Subject;

public class TimetableFilter extends AppCompatActivity {

    private List<NLevelItem> list;
    private ListView listView;
    private Database database;
    private Button addSubject;
    private Button done;
    private Button checkedSubjects;
    private ProgressBar progressBar;

    public static boolean isOpen = false;

    private boolean nothingChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable_filter);

        isOpen = true;

        setTitle("Wähle deine Vorlesungen");

        listView = (ListView) findViewById(R.id.lvNExp);
        progressBar = (ProgressBar) findViewById(R.id.pbTimetableFilter);

        if (Timetable.isLoading)
            progressBar.setVisibility(View.VISIBLE);

        database = new Database(this);

        if (getIntent().getExtras() != null)
            nothingChecked = getIntent().getExtras().getBoolean("nothing checked");

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
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                finish();
            }
        });

        checkedSubjects = (Button) findViewById(R.id.btnCheckedSubjects);
        checkedSubjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TimetableFilter.this, CheckedSubjectsOverview.class);
                startActivity(intent);
            }
        });


        //wird nach aufgerufen wenn Vorlesunge fertig geladen wurden
        Connect.addListener(new ConnectionListener() {
            @Override
            public void onChanged() {
                if (isOpen) {
                    Log.e("TimetableFilter", "onChanged");
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

                    if (name.equals(""))
                        name = "Vorlesungen";

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

                            final TextView tv = (TextView) view.findViewById(R.id.tvSubjectName);
                            String name = subject.getSubjectName();
                            tv.setText(name);


                            final TextView tvInfo = (TextView) view.findViewById(R.id.tvInfos);
                            String info = subject.getDate();
                            if (!subject.getGruppe().equals(""))
                                info = info + ", " + subject.getGruppe();
                            if (!subject.getTeacher().equals(""))
                                info = info + ", " + subject.getTeacher();
                            tvInfo.setText(info);

                            tv.setSelected(true);
                            tvInfo.setSelected(true);

                            final CheckBox cb = (CheckBox) view.findViewById(R.id.cbAbo);
                            cb.setVisibility(View.GONE);

                            if (subject.isChecked()) {
                                cb.setChecked(true);
                                tv.setBackgroundColor(Color.parseColor("#0078ff"));
                                tvInfo.setBackgroundColor(Color.parseColor("#0078ff"));
                            } else {
                                cb.setChecked(false);
                                tv.setBackgroundColor(Color.parseColor("#ffffff"));
                                tvInfo.setBackgroundColor(Color.parseColor("#ffffff"));
                            }

                            tv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    cb.toggle();

                                    if (cb.isChecked()) {
                                        tv.setBackgroundColor(Color.parseColor("#0078ff"));
                                        tvInfo.setBackgroundColor(Color.parseColor("#0078ff"));
                                    } else {
                                        cb.setChecked(false);
                                        tv.setBackgroundColor(Color.parseColor("#ffffff"));
                                        tvInfo.setBackgroundColor(Color.parseColor("#ffffff"));
                                    }

                                    database.updateCheckedSubjects(subject, cb.isChecked());
                                    subject.setChecked(cb.isChecked());
                                }
                            });

                            tvInfo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    cb.toggle();

                                    if (cb.isChecked()) {
                                        tv.setBackgroundColor(Color.parseColor("#0078ff"));
                                        tvInfo.setBackgroundColor(Color.parseColor("#0078ff"));
                                    } else {
                                        cb.setChecked(false);
                                        tv.setBackgroundColor(Color.parseColor("#ffffff"));
                                        tvInfo.setBackgroundColor(Color.parseColor("#ffffff"));
                                    }

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
                    Collections.sort(childList);

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
                        List<Subject> grandChildList = database.getDistinctSubjectsOfYaSaS(gParentList.get(i), parentList.get(j), childList.get(k));

                        for (int l = 0; l < grandChildList.size(); l++) {
                            NLevelItem grandChild = new NLevelItem(grandChildList.get(l), child, new NLevelView() {

                                @Override
                                public View getView(final NLevelItem item) {
                                    final Subject subject = ((Subject) item.getWrappedObject());

                                    View view = inflater.inflate(R.layout.list_item, null);
                                    final TextView tv = (TextView) view.findViewById(R.id.tvSubjectName);
                                    String name = subject.getSubjectName();
                                    tv.setText(name);

                                    final TextView tvInfo = (TextView) view.findViewById(R.id.tvInfos);
                                    String info = "";
                                    if (!subject.getTeacher().equals(""))
                                        info = info + subject.getTeacher();
                                    if (!subject.getGruppe().equals(""))
                                        info = info + ", " + subject.getGruppe();
                                    tvInfo.setText(info);

                                    tv.setSelected(true);
                                    tvInfo.setSelected(true);

                                    final CheckBox cb = (CheckBox) view.findViewById(R.id.cbAbo);
                                    cb.setVisibility(View.GONE);

                                    if (subject.isChecked()) {
                                        cb.setChecked(true);
                                        tv.setBackgroundColor(Color.parseColor("#0078ff"));
                                        tvInfo.setBackgroundColor(Color.parseColor("#0078ff"));
                                    } else {
                                        cb.setChecked(false);
                                        tv.setBackgroundColor(Color.parseColor("#ffffff"));
                                        tvInfo.setBackgroundColor(Color.parseColor("#ffffff"));
                                    }
                                    tv.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            cb.toggle();

                                            if (cb.isChecked()) {
                                                tv.setBackgroundColor(Color.parseColor("#0078ff"));
                                                tvInfo.setBackgroundColor(Color.parseColor("#0078ff"));
                                            } else {
                                                cb.setChecked(false);
                                                tv.setBackgroundColor(Color.parseColor("#ffffff"));
                                                tvInfo.setBackgroundColor(Color.parseColor("#ffffff"));
                                            }

                                            database.updateCheckedSubjects(subject.getId(), cb.isChecked());
                                            subject.setChecked(cb.isChecked());
                                        }
                                    });

                                    tvInfo.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            cb.toggle();

                                            if (cb.isChecked()) {
                                                tv.setBackgroundColor(Color.parseColor("#0078ff"));
                                                tvInfo.setBackgroundColor(Color.parseColor("#0078ff"));
                                            } else {
                                                cb.setChecked(false);
                                                tv.setBackgroundColor(Color.parseColor("#ffffff"));
                                                tvInfo.setBackgroundColor(Color.parseColor("#ffffff"));
                                            }

                                            database.updateCheckedSubjects(subject, cb.isChecked());
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
    public void onBackPressed() {

        if (nothingChecked) {
            Intent intent = new Intent(TimetableFilter.this, MainActivity.class);
            startActivity(intent);
            this.finish();
            overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
        }

        super.onBackPressed();
        overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            init();
        } catch (Exception e) {
        }
    }

    public void init() {
        prepareListData();

        NLevelAdapter adapter = new NLevelAdapter(list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                ((NLevelAdapter) listView.getAdapter()).toggle(arg2);
                ((NLevelAdapter) listView.getAdapter()).getFilter().filter();

            }
        });
    }

}
