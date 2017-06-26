package de.android.fhwsapp.Timetable;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import de.android.fhwsapp.R;
import de.android.fhwsapp.Database;

public class AddSubject extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    private EditText etSubjectName, etTeacher, etDay, etStart, etEnd, etRoom, etGroup;
    private CheckBox cbEveryWeek;
    private Button btnSave;
    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

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
    }

    @Override
    public void onClick(View v) {
        //makes keyboard disappear
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        switch (v.getId()){
            case R.id.btnSave:
                Subject customSubject = createCustomSubject();
                database.createSubject(customSubject);
                Toast.makeText(this, "Ihr Fach wurde gespeichert", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        switch (v.getId()){
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
        return false;
    }

    private void showDatePickerDialog(){
        Calendar mcurrentTime = Calendar.getInstance();
        int year = mcurrentTime.get(Calendar.YEAR);
        int month = mcurrentTime.get(Calendar.MONTH);
        int day = mcurrentTime.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog mDayPicker;
        mDayPicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                etDay.setText( dayOfMonth + "." + month +"."+year);
            }
        },year,month,day );
        mDayPicker.setTitle("Wähle Datum");

        mDayPicker.show();
    }

    private void showTimePickerDialog(final int id){
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                if(id == R.id.etStart)
                    etStart.setText( selectedHour + ":" + selectedMinute);
                else
                    etEnd.setText( selectedHour + ":" + selectedMinute);
            }
        }, hour, minute, true);
        if(id == R.id.etStart)
            mTimePicker.setTitle("Wähle Startzeit");
        else
            mTimePicker.setTitle("Wähle Endzeit");

        mTimePicker.show();
    }

    private Subject createCustomSubject(){
        Subject subject = new Subject();

        return subject;
    }

}
