package de.android.fhwsapp.Timetable.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.android.fhwsapp.Timetable.Subject;

public class Database extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "FHWS";

    // Table Names
    private static final String TABLE_SUBJECTS = "subjects";
//    private static final String TABLE_TAG = "tags";
//    private static final String TABLE_TODO_TAG = "todo_tags";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_CREATED_AT = "created_at";

    // NOTES Table - column nmaes
    private static final String KEY_Name = "subject_name";
    private static final String KEY_Teacher = "teacher";
    private static final String KEY_Room = "room";
    private static final String KEY_Info = "info";
    private static final String KEY_Group = "group";
    private static final String KEY_Type = "type";
    private static final String KEY_Date = "date";
    private static final String KEY_Start_Time = "start_time";
    private static final String KEY_End_Time = "end_time";
    private static final String KEY_Selected = "selected";

//    // TAGS Table - column names
//    private static final String KEY_TAG_NAME = "tag_name";
//
//    // NOTE_TAGS Table - column names
//    private static final String KEY_TODO_ID = "todo_id";
//    private static final String KEY_TAG_ID = "tag_id";

    // Table Create Statements
    // Todo table create statement
    private static final String CREATE_TABLE_SUBJECTS = "CREATE TABLE "
            + TABLE_SUBJECTS
            + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_Name + " TEXT,"
            + KEY_Teacher + " TEXT,"
            + KEY_Room + " TEXT,"
            + KEY_Info +" TEXT,"
            + KEY_Group +" TEXT,"
            + KEY_Type +" TEXT,"
            + KEY_Date +" TEXT,"
            + KEY_Start_Time +" TEXT,"
            + KEY_End_Time+" ,TEXT"
            + KEY_Selected+" TEXT,"
            + KEY_CREATED_AT + " DATETIME"
            + ")";

    // Tag table create statement
//    private static final String CREATE_TABLE_TAG = "CREATE TABLE " + TABLE_TAG
//            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TAG_NAME + " TEXT,"
//            + KEY_CREATED_AT + " DATETIME" + ")";
//
//    // todo_tag table create statement
//    private static final String CREATE_TABLE_TODO_TAG = "CREATE TABLE "
//            + TABLE_TODO_TAG + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
//            + KEY_TODO_ID + " INTEGER," + KEY_TAG_ID + " INTEGER,"
//            + KEY_CREATED_AT + " DATETIME" + ")";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_SUBJECTS);
//        db.execSQL(CREATE_TABLE_TAG);
//        db.execSQL(CREATE_TABLE_TODO_TAG);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBJECTS);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAG);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO_TAG);

        // create new tables
        onCreate(db);
    }

    public long createSubject(Subject subject) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, subject.getId());
        values.put(KEY_Name, subject.getSubjectName());
        values.put(KEY_Teacher, subject.getTeacher());
        values.put(KEY_Date, subject.getDate());
        values.put(KEY_Group, subject.getGruppe());
        values.put(KEY_Info, subject.getInfo());
        values.put(KEY_Room, subject.getRoom());
        values.put(KEY_Type, subject.getType());
        values.put(KEY_Start_Time, subject.getTimeStart());
        values.put(KEY_End_Time, subject.getTimeEnd());
        if(subject.isChecked())
            values.put(KEY_Selected, "true");
        else
            values.put(KEY_Selected, "false");

        values.put(KEY_CREATED_AT, getDateTime());

        // insert row
        long todo_id = db.insert(TABLE_SUBJECTS, null, values);


        return todo_id;
    }

    public Subject getSubject(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_SUBJECTS + " WHERE "
                + KEY_ID + " = " + id;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Subject subject = new Subject();
        subject.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        subject.setDate(c.getString(c.getColumnIndex(KEY_Date)));
        subject.setGruppe(c.getString(c.getColumnIndex(KEY_Group)));
        subject.setInfo(c.getString(c.getColumnIndex(KEY_Info)));
        subject.setRoom(c.getString(c.getColumnIndex(KEY_Room)));
        subject.setSubjectName(c.getString(c.getColumnIndex(KEY_Name)));
        subject.setTeacher(c.getString(c.getColumnIndex(KEY_Teacher)));
        subject.setType(c.getString(c.getColumnIndex(KEY_Type)));
        subject.setTimeStart(c.getString(c.getColumnIndex(KEY_Start_Time)));
        subject.setTimeEnd(c.getString(c.getColumnIndex(KEY_End_Time)));
        if(c.getString(c.getColumnIndex(KEY_Selected)).equals("true"))
            subject.setChecked(true);
        else
            subject.setChecked(false);

        return subject;
    }

    public List<Subject> getAllSubjects() {
        List<Subject> subjects = new ArrayList<Subject>();
        String selectQuery = "SELECT  * FROM " + TABLE_SUBJECTS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Subject subject = new Subject();
                subject.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                subject.setDate(c.getString(c.getColumnIndex(KEY_Date)));
                subject.setGruppe(c.getString(c.getColumnIndex(KEY_Group)));
                subject.setInfo(c.getString(c.getColumnIndex(KEY_Info)));
                subject.setRoom(c.getString(c.getColumnIndex(KEY_Room)));
                subject.setSubjectName(c.getString(c.getColumnIndex(KEY_Name)));
                subject.setTeacher(c.getString(c.getColumnIndex(KEY_Teacher)));
                subject.setType(c.getString(c.getColumnIndex(KEY_Type)));
                subject.setTimeStart(c.getString(c.getColumnIndex(KEY_Start_Time)));
                subject.setTimeEnd(c.getString(c.getColumnIndex(KEY_End_Time)));
                if(c.getString(c.getColumnIndex(KEY_Selected)).equals("true"))
                    subject.setChecked(true);
                else
                    subject.setChecked(false);

                subjects.add(subject);
            } while (c.moveToNext());
        }

        return subjects;
    }

    public List<Subject> getAllCheckedSubjects() {
        List<Subject> subjects = new ArrayList<Subject>();
        String selectQuery = "SELECT  * FROM " + TABLE_SUBJECTS +" WHERE "+KEY_Selected + " = true";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Subject subject = new Subject();
                subject.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                subject.setDate(c.getString(c.getColumnIndex(KEY_Date)));
                subject.setGruppe(c.getString(c.getColumnIndex(KEY_Group)));
                subject.setInfo(c.getString(c.getColumnIndex(KEY_Info)));
                subject.setRoom(c.getString(c.getColumnIndex(KEY_Room)));
                subject.setSubjectName(c.getString(c.getColumnIndex(KEY_Name)));
                subject.setTeacher(c.getString(c.getColumnIndex(KEY_Teacher)));
                subject.setType(c.getString(c.getColumnIndex(KEY_Type)));
                subject.setTimeStart(c.getString(c.getColumnIndex(KEY_Start_Time)));
                subject.setTimeEnd(c.getString(c.getColumnIndex(KEY_End_Time)));
                if(c.getString(c.getColumnIndex(KEY_Selected)).equals("true"))
                    subject.setChecked(true);
                else
                    subject.setChecked(false);

                subjects.add(subject);
            } while (c.moveToNext());
        }

        return subjects;
    }

    public int updateSubject(Subject subject) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, subject.getId());
        values.put(KEY_Name, subject.getSubjectName());
        values.put(KEY_Teacher, subject.getTeacher());
        values.put(KEY_Date, subject.getDate());
        values.put(KEY_Group, subject.getGruppe());
        values.put(KEY_Info, subject.getInfo());
        values.put(KEY_Room, subject.getRoom());
        values.put(KEY_Type, subject.getType());
        values.put(KEY_Start_Time, subject.getTimeStart());
        values.put(KEY_End_Time, subject.getTimeEnd());
        if(subject.isChecked())
            values.put(KEY_Selected, "true");
        else
            values.put(KEY_Selected, "false");

        values.put(KEY_CREATED_AT, getDateTime());

        // updating row
        return db.update(TABLE_SUBJECTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(subject.getId()) });
    }

    public int updateSubjectInfo(Subject subject) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_Info, subject.getInfo());

        // updating row
        return db.update(TABLE_SUBJECTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(subject.getId()) });
    }

    public int updateCheckSubjects(Subject subject) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_Selected, "true");

        // updating row
        return db.update(TABLE_SUBJECTS, values, KEY_Name + " = ?"+" AND " + KEY_Group + " = ?",
                new String[] { String.valueOf(subject.getSubjectName()), subject.getGruppe() });
    }

    public int updateUncheckSubjects(Subject subject) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_Selected, "false");

        // updating row
        return db.update(TABLE_SUBJECTS, values, KEY_Name + " = ?"+" AND " + KEY_Group + " = ?",
                new String[] { String.valueOf(subject.getSubjectName()), subject.getGruppe() });
    }

    public void deleteToDo(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SUBJECTS, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
    }


    private String getDateTime(){
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = new Date();
            return dateFormat.format(date);
    }
}