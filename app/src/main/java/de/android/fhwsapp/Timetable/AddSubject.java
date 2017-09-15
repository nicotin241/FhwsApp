package de.android.fhwsapp.Timetable;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Calendar;

import de.android.fhwsapp.Database;
import de.android.fhwsapp.R;

public class AddSubject extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    private EditText etSubjectName, etTeacher, etDay, etStart, etEnd, etRoom, etGroup;
    private CheckBox cbEveryWeek;
    private Button btnSave;
    private Database database;
    private Subject sbj = null;
    private boolean edit = false;
    private String oldName = null;
    private String oldDate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);

        setTitle("Eigene Veranstaltung");

        database = new Database(this);

        etSubjectName = (EditText) findViewById(R.id.etSubjectName);
        etTeacher = (EditText) findViewById(R.id.etTeacher);
        etRoom = (EditText) findViewById(R.id.etRoom);
        etGroup = (EditText) findViewById(R.id.etGroup);


        etStart = (EditText) findViewById(R.id.etStart);
        etStart.setOnTouchListener(this);
        etEnd = (EditText) findViewById(R.id.etEnd);
        etEnd.setOnTouchListener(this);
        etDay = (EditText) findViewById(R.id.etDay);
        etDay.setOnTouchListener(this);

        cbEveryWeek = (CheckBox) findViewById(R.id.cbEveryWeek);

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);

        if (this.getIntent().getExtras() != null) {
            String name = (String) this.getIntent().getExtras().get("Subject");
            String date = (String) this.getIntent().getExtras().get("Date");

            sbj = database.getSubjectWithNameAndDate(name, date);

            etSubjectName.setText(sbj.getSubjectName());
            etTeacher.setText(sbj.getTeacher());
            etRoom.setText(sbj.getRoom());
            etGroup.setText(sbj.getGruppe());
            etStart.setText(sbj.getTimeStart());
            etEnd.setText(sbj.getTimeEnd());
            etDay.setText(sbj.getDate());

            oldName = sbj.getSubjectName();
            oldDate = sbj.getDate();
            edit = true;

            cbEveryWeek.setEnabled(false);
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnSave:
                Subject customSubject = null;

                try {
                    customSubject = createCustomSubject();
                } catch (Exception e) {
                    Toast.makeText(this, "Die Felder Name, Datum, Startzeit und Endzeit müssen ausgefüllt werden",
                            Toast.LENGTH_LONG).show();
                    break;
                }

                if (customSubject.getSubjectName().equals("") || customSubject.getDate().equals("")
                        || customSubject.getTimeStart().equals("") || customSubject.getTimeEnd().equals("")) {
                    Toast.makeText(this, "Die Felder Name, Datum, Startzeit und Endzeit müssen ausgefüllt werden",
                            Toast.LENGTH_LONG).show();
                    break;
                }

                boolean stop = false;

                for (String name : database.getAllSubjectNames()) {
                    if (name.equals(oldName))
                        continue;
                    if (customSubject.getSubjectName().equals(name)) {
                        Toast.makeText(this, "Es dürfen keine gleichen Namen vorkommen", Toast.LENGTH_LONG);
                        stop = true;
                        break;
                    }
                }
                if (stop)
                    break;


                DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm");
                DateTime start = formatter.parseDateTime(customSubject.getTimeStart());
                DateTime end = formatter.parseDateTime(customSubject.getTimeEnd());

                if (start.isAfter(end)) {
                    Toast.makeText(this, "Die Endzeit muss größer sein als die Startzeit", Toast.LENGTH_LONG).show();
                    break;
                }

                if (customSubject.getDateAsDateTime().plusDays(1).isBeforeNow()) {
                    Toast.makeText(this, "Das Datum liegt in der Vergangenheit", Toast.LENGTH_LONG).show();
                    break;
                }

                int diff = ((customSubject.getDateAsDateTime().getYear() * 1000) + customSubject.getDateAsDateTime().getDayOfYear()) - ((DateTime.now().getYear() * 1000) + DateTime.now().getDayOfYear());
                if (diff > 240) {
                    Toast.makeText(this, "Das Datum darf maximal 240 Tage in der Zukunft liegen", Toast.LENGTH_LONG).show();
                    break;
                }


                if (edit) {
                    if (customSubject.getType().equals("Custom"))
                        database.updateSubjectWithNameAndDate(customSubject, oldName, oldDate);
                    else
                        database.updateSubjectWithName(customSubject, oldName);
                } else {
                    if (cbEveryWeek.isChecked())
                        database.createSubjectsForEveryWeek(customSubject);
                    else
                        database.createSubject(customSubject);
                }

                Toast.makeText(this, "Ihr Fach wurde gespeichert", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(etSubjectName.getWindowToken(), 0);
            mgr.hideSoftInputFromWindow(etRoom.getWindowToken(), 0);
            mgr.hideSoftInputFromWindow(etTeacher.getWindowToken(), 0);
            mgr.hideSoftInputFromWindow(etGroup.getWindowToken(), 0);

            switch (v.getId()) {
                case R.id.etDay:
                    showDatePickerDialog();
                    break;
                case R.id.etStart:
                    showTimePickerDialog(R.id.etStart);
                    break;
                case R.id.etEnd:
                    showTimePickerDialog(R.id.etEnd);
                    break;

            }

        }
        return false;
    }

    private void showDatePickerDialog() {
        Calendar mcurrentTime = Calendar.getInstance();
        int year = mcurrentTime.get(Calendar.YEAR);
        int month = mcurrentTime.get(Calendar.MONTH);
        int day = mcurrentTime.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog mDayPicker;
        mDayPicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                etDay.setText(dayOfMonth + "." + ++month + "." + year);
            }
        }, year, month, day);
        mDayPicker.setTitle("Wähle Datum");

        mDayPicker.show();
    }

    private void showTimePickerDialog(final int id) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                if (id == R.id.etStart)
                    etStart.setText(selectedHour + ":" + ((selectedMinute < 10) ? "0" + selectedMinute : selectedMinute));
                else
                    etEnd.setText(selectedHour + ":" + ((selectedMinute < 10) ? "0" + selectedMinute : selectedMinute));
            }
        }, hour, minute, true);
        if (id == R.id.etStart)
            mTimePicker.setTitle("Wähle Startzeit");
        else
            mTimePicker.setTitle("Wähle Endzeit");

        mTimePicker.show();
    }

    private Subject createCustomSubject() {
        Subject subject = new Subject();
        subject.setType("Custom");
        subject.setSubjectName(etSubjectName.getText().toString());
        subject.setDateAsDateTime(DateTime.parse(etDay.getText().toString(), DateTimeFormat.forPattern("dd.MM.yy")));
        subject.setChecked(true);
        subject.setGruppe(etGroup.getText().toString());
        subject.setId(666);
        subject.setRoom(etRoom.getText().toString());
        subject.setTeacher(etTeacher.getText().toString());
        subject.setTimeStart(etStart.getText().toString());
        subject.setTimeEnd(etEnd.getText().toString());
        subject.setYear("Custom");

        return subject;
    }

}
