package de.android.fhwsapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Weeks;
import org.joda.time.format.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import de.android.fhwsapp.Timetable.Subject;
import de.android.fhwsapp.objects.Meal;
import de.android.fhwsapp.objects.NewsItem;

public class Database extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "FHWS";

    /*
    *
    * Timetable Strings
    *
    * */

    // Table Names
    private static final String TABLE_SUBJECTS = "subjects";
//    private static final String TABLE_TAG = "tags";
//    private static final String TABLE_TODO_TAG = "todo_tags";

    // Common column names
    //private static final String KEY_ID = "id";
    private static final String KEY_CREATED_AT = "created_at";

    // NOTES Table - column nmaes
    private static final String KEY_ID = "id";
    private static final String KEY_YEAR = "year";
    private static final String KEY_STUDIENGANG = "studiengang";
    private static final String KEY_Name = "subject_name";
    private static final String KEY_Teacher = "teacher";
    private static final String KEY_Room = "room";
    private static final String KEY_Info = "info";
    private static final String KEY_Group = "_group";
    private static final String KEY_Type = "type";
    private static final String KEY_Date = "date";
    private static final String KEY_Start_Time = "start_time";
    private static final String KEY_End_Time = "end_time";
    private static final String KEY_Selected = "selected";
    private static final String KEY_Semester = "semester";
    private static final String KEY_Url = "url";

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
            + KEY_ID + " INTEGER,"
            + KEY_Name + " TEXT,"
            + KEY_Teacher + " TEXT,"
            + KEY_Room + " TEXT,"
            + KEY_Info + " TEXT,"
            + KEY_Group + " TEXT,"
            + KEY_Type + " TEXT,"
            + KEY_Date + " DATETIME,"
            + KEY_Start_Time + " TEXT,"
            + KEY_End_Time + " TEXT,"
            + KEY_Selected + " TEXT,"
            + KEY_CREATED_AT + " DATETIME,"
            + KEY_YEAR + " TEXT,"
            + KEY_STUDIENGANG + " TEXT,"
            + KEY_Semester + " TEXT,"
            + KEY_Url + " TEXT"
            //+ ", PRIMARY KEY(" + KEY_Name + "," + KEY_Date + "," + KEY_Group + ")"
            + ")";

    private static DateTime lastMonday = null;

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

    /*
    *
    * Mensa meals - Strings
    *
    * */

    private static final String MEALS_TABLE = "meals_table";
    private static final String MEALS_MENSA_ID = "_mensaid";
    private static final String MEALS_NAME = "_name";
    private static final String MEALS_ARTNAME = "_artname";
    private static final String MEALS_DATE = "_date";
    private static final String MEALS_PRICE_STUDENTS = "_pricestudents";
    private static final String MEALS_PRICE_BED = "_pricebed";
    private static final String MEALS_PRICE_GUEST = "_priceguest";
    private static final String MEALS_ADDITIVES = "_additives";
    private static final String MEALS_FOODTYPE = "_foodtype";

    String CREATE_MEALS_TABLE = "CREATE TABLE " + MEALS_TABLE + " ("
            + MEALS_MENSA_ID + " INTEGER,"
            + MEALS_NAME + " TEXT,"
            + MEALS_ARTNAME + " TEXT,"
            + MEALS_DATE + " TEXT,"
            + MEALS_PRICE_STUDENTS + " TEXT,"
            + MEALS_PRICE_BED + " TEXT,"
            + MEALS_PRICE_GUEST + " TEXT,"
            + MEALS_ADDITIVES + " TEXT,"
            + MEALS_FOODTYPE + " TEXT);";

    String DROP_MEALS_TABLE = "DROP TABLE IF EXISTS " + MEALS_TABLE;

    /*
    *
    * Newsitems - Strings
    *
    * */

    private static final String NEWS_TABLE = "news_table";
    private static final String NEWS_ID = "_id";
    private static final String NEWS_TITLE = "_title";
    private static final String NEWS_TEXT = "_text";
    private static final String NEWS_CATEGORY = "_category";
    private static final String NEWS_TAGS = "_tags";
    private static final String NEWS_TIMESTAMP = "_timestamp";


    String CREATE_NEWS_TABLE = "CREATE TABLE " + NEWS_TABLE + " ("
            + NEWS_ID + " INTEGER PRIMARY KEY,"
            + NEWS_TITLE + " TEXT,"
            + NEWS_TEXT + " TEXT,"
            + NEWS_CATEGORY + " TEXT,"
            + NEWS_TAGS + " TEXT,"
            + NEWS_TIMESTAMP + " REAL);";

    String DROP_NEWS_TABLE = "DROP TABLE IF EXISTS " + NEWS_TABLE;


    /*
    *
    * Busplan - Strings
    *
    * */

    private static final String BUS_TABLE = "bus_table";
    private static final String BUS_LINIE = "bus_linie";
    private static final String BUS_URL = "bus_url";


    String CREATE_BUS_TABLE = "CREATE TABLE " + BUS_TABLE + " ("
            + BUS_LINIE + " TEXT,"
            + BUS_URL + " TEXT);";

    String DROP_BUS_TABLE = "DROP TABLE IF EXISTS " + BUS_TABLE;


    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_SUBJECTS);
//        db.execSQL(CREATE_TABLE_TAG);
//        db.execSQL(CREATE_TABLE_TODO_TAG);

        db.execSQL(CREATE_MEALS_TABLE);
        db.execSQL(CREATE_BUS_TABLE);
        db.execSQL(CREATE_NEWS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBJECTS);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAG);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO_TAG);

        db.execSQL(DROP_MEALS_TABLE);
        db.execSQL(DROP_BUS_TABLE);
        db.execSQL(DROP_NEWS_TABLE);

        onCreate(db);
    }

    /*
    *
    * Timetable functions
    *
    * */

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
        values.put(KEY_YEAR, subject.getYear());
        values.put(KEY_STUDIENGANG, subject.getStudiengang());
        values.put(KEY_Semester, subject.getSemester());
        //values.put(KEY_Url, subject.getUrl());
        if (subject.isChecked())
            values.put(KEY_Selected, "true");
        else
            values.put(KEY_Selected, "false");

        //values.put(KEY_CREATED_AT, getDateTime());

        // insert row
        long todo_id = db.insert(TABLE_SUBJECTS, null, values);


        return todo_id;
    }

    public void createSubjectsForEveryWeek(Subject subject) {

        int addFaktor = subject.getDateAsDateTime().getDayOfWeek();


        if (lastMonday == null) {
            DateTime lastDay = null;
            List<DateTime> list = getAllDistinctSubjectDates();
            for (DateTime date : list) {
                if (lastDay == null)
                    lastDay = date;
                else if (lastDay.isBefore(date)) {
                    lastDay = date;
                }
            }

            if (lastDay == null) {
                createSubject(subject);
                return;
            }

            lastMonday = lastDay.minusDays(lastDay.getDayOfWeek() - 1);
        }


        DateTime end = lastMonday.plusDays(addFaktor);

        do {
            createSubject(subject);
            subject.setDateAsDateTime(subject.getDateAsDateTime().plusDays(7));
        } while (end.isAfter(subject.getDateAsDateTime()));

    }

    public Subject getSubject(String name, String date, String group) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_SUBJECTS + " WHERE "
                + KEY_Name + " = " + name + " AND " + KEY_Date + " = " + date + " AND " + KEY_Group + " = " + group;

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
        subject.setStudiengang(c.getString(c.getColumnIndex(KEY_STUDIENGANG)));
        subject.setYear(c.getString(c.getColumnIndex(KEY_YEAR)));
        subject.setSemester(c.getString(c.getColumnIndex(KEY_Semester)));
        subject.setUrl(c.getString(c.getColumnIndex(KEY_Url)));
        if (c.getString(c.getColumnIndex(KEY_Selected)).equals("true"))
            subject.setChecked(true);
        else
            subject.setChecked(false);

        return subject;
    }

    public List<String> getDistinctYears() {
        List<String> years = new ArrayList<>();
        String selectQuery = "SELECT DISTINCT " + KEY_YEAR + " FROM " + TABLE_SUBJECTS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                years.add(c.getString(c.getColumnIndex(KEY_YEAR)));
            } while (c.moveToNext());
        }

        return years;

    }

    public List<String> getDistinctStudiengangOfYear(String year) {
        List<String> studiengaenge = new ArrayList<>();
        String selectQuery = "SELECT DISTINCT " + KEY_STUDIENGANG + " FROM "
                + TABLE_SUBJECTS + " WHERE " + KEY_YEAR + " = '" + year + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                studiengaenge.add(c.getString(c.getColumnIndex(KEY_STUDIENGANG)));
            } while (c.moveToNext());
        }

        return studiengaenge;
    }

    public List<String> getDistinctSemesterOfYaS(String year, String studiengang) {
        List<String> semester = new ArrayList<>();
        String selectQuery = "SELECT DISTINCT " + KEY_Semester + " FROM "
                + TABLE_SUBJECTS + " WHERE " + KEY_YEAR + " = '" + year + "' AND "
                + KEY_STUDIENGANG + " = '" + studiengang + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                semester.add(c.getString(c.getColumnIndex(KEY_Semester)));
            } while (c.moveToNext());
        }

        return semester;
    }

    public List<Subject> getDistinctSubjectsOfYaSaS(String year, String studiengang, String semester) {
        List<Subject> subjects = new ArrayList<>();
        String selectQuery = "SELECT DISTINCT " + KEY_ID + " FROM "
                + TABLE_SUBJECTS + " WHERE " + KEY_YEAR + " = '" + year
                + "' AND " + KEY_STUDIENGANG + " = '" + studiengang
                + "' AND " + KEY_Semester + " = " + semester;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Subject subject = getSubjectWithID(c.getInt(c.getColumnIndex(KEY_ID)));

                subjects.add(subject);
            } while (c.moveToNext());
        }

        return subjects;
    }

    public Subject getSubjectWithID(int id) {
        Subject subject = new Subject();
        String selectQuery = "SELECT  * FROM " + TABLE_SUBJECTS + " WHERE " + KEY_ID + " = " + id;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
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
            subject.setStudiengang(c.getString(c.getColumnIndex(KEY_STUDIENGANG)));
            subject.setYear(c.getString(c.getColumnIndex(KEY_YEAR)));
            subject.setSemester(c.getString(c.getColumnIndex(KEY_Semester)));
            subject.setUrl(c.getString(c.getColumnIndex(KEY_Url)));
            if (c.getString(c.getColumnIndex(KEY_Selected)).equals("true"))
                subject.setChecked(true);
            else
                subject.setChecked(false);
        }

        return subject;
    }

    public List<Subject> getSubjectsWithType(String type) {
        List<Subject> subjects = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_SUBJECTS + " WHERE " + KEY_Type + " = '" + type + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

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
                subject.setStudiengang(c.getString(c.getColumnIndex(KEY_STUDIENGANG)));
                subject.setYear(c.getString(c.getColumnIndex(KEY_YEAR)));
                subject.setSemester(c.getString(c.getColumnIndex(KEY_Semester)));
                subject.setUrl(c.getString(c.getColumnIndex(KEY_Url)));
                if (c.getString(c.getColumnIndex(KEY_Selected)).equals("true"))
                    subject.setChecked(true);
                else
                    subject.setChecked(false);

                subjects.add(subject);

            } while (c.moveToNext());
        }

        return subjects;
    }

    public List<String> getAllSubjectNames() {
        List<String> names = new ArrayList<>();
        String selectQuery = "SELECT DISTINCT " + KEY_Name + " FROM "
                + TABLE_SUBJECTS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                String name = c.getString(c.getColumnIndex(KEY_Name));

                names.add(name);
            } while (c.moveToNext());
        }

        return names;
    }

    public List<String> getAllCheckedSubjectNames() {
        List<String> names = new ArrayList<>();
        String selectQuery = "SELECT DISTINCT " + KEY_Name + " FROM "
                + TABLE_SUBJECTS + " WHERE " + KEY_Selected + " = 'true'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                String name = c.getString(c.getColumnIndex(KEY_Name));

                names.add(name);
            } while (c.moveToNext());
        }

        return names;
    }

    public Subject getSubjectWithName(String name) {
        Subject subject = new Subject();
        String selectQuery = "SELECT  * FROM " + TABLE_SUBJECTS + " WHERE " + KEY_Name + " = '" + name + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
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
            subject.setStudiengang(c.getString(c.getColumnIndex(KEY_STUDIENGANG)));
            subject.setYear(c.getString(c.getColumnIndex(KEY_YEAR)));
            subject.setSemester(c.getString(c.getColumnIndex(KEY_Semester)));
            subject.setUrl(c.getString(c.getColumnIndex(KEY_Url)));
            if (c.getString(c.getColumnIndex(KEY_Selected)).equals("true"))
                subject.setChecked(true);
            else
                subject.setChecked(false);
        }

        return subject;
    }

    public Subject getSubjectWithNameAndDate(String name, String date) {
        Subject subject = new Subject();
        String selectQuery = "SELECT  * FROM " + TABLE_SUBJECTS + " WHERE " + KEY_Name + " = '" + name + "'"
                + " AND " + KEY_Date + " = '" + date + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
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
            subject.setStudiengang(c.getString(c.getColumnIndex(KEY_STUDIENGANG)));
            subject.setYear(c.getString(c.getColumnIndex(KEY_YEAR)));
            subject.setSemester(c.getString(c.getColumnIndex(KEY_Semester)));
            subject.setUrl(c.getString(c.getColumnIndex(KEY_Url)));
            if (c.getString(c.getColumnIndex(KEY_Selected)).equals("true"))
                subject.setChecked(true);
            else
                subject.setChecked(false);
        }

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
                subject.setStudiengang(c.getString(c.getColumnIndex(KEY_STUDIENGANG)));
                subject.setYear(c.getString(c.getColumnIndex(KEY_YEAR)));
                subject.setSemester(c.getString(c.getColumnIndex(KEY_Semester)));
                subject.setUrl(c.getString(c.getColumnIndex(KEY_Url)));
                if (c.getString(c.getColumnIndex(KEY_Selected)).equals("true"))
                    subject.setChecked(true);
                else
                    subject.setChecked(false);

                subjects.add(subject);

            } while (c.moveToNext());
        }

        return subjects;
    }

    public List<DateTime> getAllDistinctSubjectDates() {
        String selectQuery = "SELECT DISTINCT " + KEY_Date + " FROM " + TABLE_SUBJECTS;
        List<DateTime> dates = new ArrayList<>();


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                String date = c.getString(c.getColumnIndex(KEY_Date));
                dates.add(DateTime.parse(date, DateTimeFormat.forPattern("dd.MM.yy")));
            } while (c.moveToNext());
        }

        return dates;
    }

    public List<Subject> getAllCheckedSubjects() {

        LinkedList<Subject> subjects = new LinkedList<>();

        List<String> allCheckedSubjects = getAllCheckedSubjectNames();
        for (String s : allCheckedSubjects) {
            subjects.add(getSubjectWithName(s));
        }

        return subjects;
    }

    public int updateSubjectWithName(Subject subject, String oldName) {
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
        values.put(KEY_YEAR, subject.getYear());
        values.put(KEY_STUDIENGANG, subject.getStudiengang());
        values.put(KEY_Semester, subject.getSemester());
        values.put(KEY_Url, subject.getUrl());
        if (subject.isChecked())
            values.put(KEY_Selected, "true");
        else
            values.put(KEY_Selected, "false");

        values.put(KEY_CREATED_AT, getDateTime());

        // updating row
        return db.update(TABLE_SUBJECTS, values, KEY_Name + " = ?",
                new String[]{String.valueOf(oldName)});
    }

    public int updateSubjectWithNameAndDate(Subject subject, String oldName, String oldDate) {
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
        values.put(KEY_YEAR, subject.getYear());
        values.put(KEY_STUDIENGANG, subject.getStudiengang());
        values.put(KEY_Semester, subject.getSemester());
        values.put(KEY_Url, subject.getUrl());
        if (subject.isChecked())
            values.put(KEY_Selected, "true");
        else
            values.put(KEY_Selected, "false");

        values.put(KEY_CREATED_AT, getDateTime());

        // updating row
        return db.update(TABLE_SUBJECTS, values, KEY_Name + " = ?" + " AND " + KEY_Date + " = ?",
                new String[]{String.valueOf(oldName), oldDate});
    }

    public int updateSubjectInfo(Subject subject) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_Info, subject.getInfo());

        // updating row
        return db.update(TABLE_SUBJECTS, values, KEY_Name + " = ?" + " AND " + KEY_Group + " = ?" + " AND " + KEY_Date + " = ?",
                new String[]{String.valueOf(subject.getSubjectName()), subject.getGruppe(), subject.getDate()});
    }

    public int updateCheckedSubjects(int id, boolean checked) {

        Subject s = getSubjectWithID(id);

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        if (checked)
            values.put(KEY_Selected, "true");
        else
            values.put(KEY_Selected, "false");

        // updating row
        return db.update(TABLE_SUBJECTS, values, KEY_ID + "= " + id, null);
    }

    public int updateCheckedSubjects(Subject subject, boolean checked) {

        Subject s = getSubjectWithNameAndDate(subject.getSubjectName(), subject.getDate());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        if (checked)
            values.put(KEY_Selected, "true");
        else
            values.put(KEY_Selected, "false");

        // updating row
        return db.update(TABLE_SUBJECTS, values, KEY_Name + " = ?" + " AND " + KEY_Group + " = ?" + " AND " + KEY_Date + " = ?",
                new String[]{String.valueOf(subject.getSubjectName()), subject.getGruppe(), subject.getDate()});
    }

    public void deleteSingleSubject(Subject subject) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SUBJECTS, KEY_Name + " = ?" + " AND " + KEY_Group + " = ?" + " AND " + KEY_Date + " = ?",
                new String[]{String.valueOf(subject.getSubjectName()), subject.getGruppe(), subject.getDate()});
    }

    public void deleteSubjectsWithName(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SUBJECTS, KEY_Name + " = ?",
                new String[]{String.valueOf(name)});
    }

    public void deleteAllSubjects() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SUBJECTS, KEY_Type + " <> ?",
                new String[]{"Custom"});
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public int getWeekCount() {
        String selectQuery = "SELECT DISTINCT " + KEY_Date + " FROM " + TABLE_SUBJECTS + " WHERE " + KEY_Selected + " = 'true'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        boolean first = true;
        String firstWeek = null;
        String lastWeek = null;
        int weeks = 0;

        if (c.moveToFirst()) {
            do {
                Subject subject = new Subject();

                subject.setDate(c.getString(c.getColumnIndex(KEY_Date)));

                if (first) {
                    firstWeek = subject.getDate();
                    lastWeek = firstWeek;
                    first = false;
                } else if (subject.getDate() != null && !subject.getDate().equals("")) {
                    if (DateTime.parse(lastWeek, DateTimeFormat.forPattern("dd.MM.yy")).getMillis() < (subject.getDateAsDateTime()).getMillis())
                        lastWeek = subject.getDate();
                    if (DateTime.parse(firstWeek, DateTimeFormat.forPattern("dd.MM.yy")).getMillis() > (subject.getDateAsDateTime()).getMillis())
                        firstWeek = subject.getDate();
                }
            } while (c.moveToNext());
        }

        if (firstWeek != null && lastWeek != null) {

            try {

                DateTime date1 = DateTime.parse(firstWeek, DateTimeFormat.forPattern("dd.MM.yy"));
                DateTime date2 = DateTime.parse(lastWeek, DateTimeFormat.forPattern("dd.MM.yy"));

                weeks = Weeks.weeksBetween(date1, date2).getWeeks() + 1;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return weeks;
    }

    public Subject getComingLecture() {

        LinkedList<Subject> list = getOrderedSubjects();

        if (!list.isEmpty())
            return list.getFirst();

        return null;

    }

    private LinkedList<Subject> getOrderedSubjects() {
        String selectQuery = "SELECT  * FROM " + TABLE_SUBJECTS + " WHERE " + KEY_Selected + " = 'true'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        LinkedList<Subject> list = new LinkedList<>();

        Subject lastSubject = null;

        if (c.moveToFirst()) {
            do {
                Subject subject = new Subject();
                subject.setDate(c.getString(c.getColumnIndex(KEY_Date)));

                subject.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                subject.setGruppe(c.getString(c.getColumnIndex(KEY_Group)));
                subject.setInfo(c.getString(c.getColumnIndex(KEY_Info)));
                subject.setRoom(c.getString(c.getColumnIndex(KEY_Room)));
                subject.setSubjectName(c.getString(c.getColumnIndex(KEY_Name)));
                subject.setTeacher(c.getString(c.getColumnIndex(KEY_Teacher)));
                subject.setType(c.getString(c.getColumnIndex(KEY_Type)));
                subject.setTimeStart(c.getString(c.getColumnIndex(KEY_Start_Time)));
                subject.setTimeEnd(c.getString(c.getColumnIndex(KEY_End_Time)));
                subject.setStudiengang(c.getString(c.getColumnIndex(KEY_STUDIENGANG)));
                subject.setYear(c.getString(c.getColumnIndex(KEY_YEAR)));
                subject.setSemester(c.getString(c.getColumnIndex(KEY_Semester)));
                subject.setUrl(c.getString(c.getColumnIndex(KEY_Url)));
                subject.setChecked(true);

                //delete out of date entries
                if (subject.getDateAsDateTime().plusDays(1).isBeforeNow()) {
                    deleteSingleSubject(subject);
                    continue;
                }

                //löscht kopien von anderen semestern, studiengaengen
                if (lastSubject != null)
                    if (lastSubject.getSubjectName().equals(subject.getSubjectName())
                            && lastSubject.getDate().equals(subject.getDate())
                            && lastSubject.getRoom().equals(subject.getRoom())
                            && lastSubject.getTimeStart().equals(subject.getTimeStart())
                            && lastSubject.getTimeEnd().equals(subject.getTimeEnd())
                            && lastSubject.getGruppe().equals(subject.getGruppe())
                            && lastSubject.getTeacher().equals(subject.getTeacher()))
                        continue;

                lastSubject = subject;

                list.add(subject);

//                if (list.size() == 0)
//                    list.add(subject);
//                else {
//                    if (subject.getDateAsDateTime().isBefore(list.getFirst().getDateAsDateTime()))
//                        list.addFirst(subject);
//                    else if (subject.getDateAsDateTime().isAfter(list.getLast().getDateAsDateTime()))
//                        list.addLast(subject);
//                    else{
//                        for(int i = 0; i < list.size(); i++){
//                            if(subject.getDateAsDateTime().getMillis() >= list.get(i).getDateAsDateTime().getMillis()){
//                                list.add(i,subject);
//                                break;
//                            }
//                            if(subject.getDateAsDateTime().isAfter(list.get(i).getDateAsDateTime())){
//                                continue;
//                            }
//
//                        }
//                    }
//                }
            } while (c.moveToNext());
        }

        Collections.sort(list, new Comparator<Subject>() {
            @Override
            public int compare(Subject o1, Subject o2) {
                if (o1.getDateAsDateTime().isBefore(o2.getDateAsDateTime()))
                    return -1;
                if (o1.getDateAsDateTime().isAfter(o2.getDateAsDateTime()))
                    return 1;

                return 0;
            }
        });

        return list;
    }

    public ArrayList<Subject>[][] getSortedSubjects(int days, int weeks) {
        ArrayList<Subject>[][] subjects = new ArrayList[weeks][days];

        LinkedList<Subject> list = getOrderedSubjects();

        for (int w = 0; w < weeks; w++)
            for (int d = 0; d < days; d++)
                subjects[w][d] = new ArrayList<>();

        int w = 0, d = 0, index = 0;
        DateTime lastDate = null;

        for (Subject subject : list) {

            //erster Durchlauf
            if (lastDate == null) {

                int length = subject.getDateAsDateTime().dayOfWeek().get() - 1;
                for (int i = 0; i < length; i++) {
                    DateTime currentDay = subject.getDateAsDateTime().minusDays(length - i);

                    Subject emptySubject = new Subject();
                    emptySubject.setDateAsDateTime(currentDay);
                    subjects[w][d].add(index, emptySubject);

                    d++;
                }


                lastDate = subject.getDateAsDateTime();
                subjects[w][d].add(index, subject);

            } else {
                int diff = Days.daysBetween(subject.getDateAsDateTime().toLocalDate(), lastDate.toLocalDate()).getDays() * (-1);

                switch (diff) {
                    //gleicher Tag
                    case 0:
                        index++;
                        break;
                    //nächster Tag
                    case 1:
                        index = 0;
                        d++;
                        break;
                    //rest
                    default:
                        //add empty Subjects

                        index = 0;

                        for (int i = 0; i < diff - 1; i++) {

                            DateTime currentDay = subject.getDateAsDateTime().minusDays((diff - 1) - i);

                            d++;

                            Subject emptySubject = new Subject();

                            emptySubject.setDateAsDateTime(currentDay);

                            try {
                                subjects[w][d].add(index, emptySubject);
                            } catch (Exception e) {
                                w++;
                                d = 0;
                                try {
                                    subjects[w][d].add(index, emptySubject);
                                } catch (Exception ex) {
                                }
                            }

                        }

                        d++;

                        break;
                }

                lastDate = subject.getDateAsDateTime();
                try {
                    subjects[w][d].add(index, subject);
                } catch (Exception e) {
                    w++;
                    d = 0;
                    try {
                        subjects[w][d].add(index, subject);
                    } catch (Exception ex) {
                    }
                }

            }


            //} while (c.moveToNext());
        }

        //fill rest days
        if (d != 0)
            for (int i = d + 1; i < 7; i++) {
                DateTime lastDay = subjects[w][i - 1].get(0).getDateAsDateTime();
                Subject emptySubject = new Subject();
                emptySubject.setDateAsDateTime(lastDay.plusDays(1));
                try {
                    subjects[w][i].add(index, emptySubject);
                } catch (Exception e) {
                }
            }

        try {
            lastMonday = subjects[w][0].get(0).getDateAsDateTime();
        } catch (Exception e) {
        }

        return subjects;
    }

    /*
    *
    * Mensa meals functions
    *
    * */

    public void addMeal(Meal meal) {

        SQLiteDatabase db = this.getWritableDatabase();

        try {

            ContentValues values = new ContentValues();
            values.put(MEALS_MENSA_ID, meal.getMensa_id());
            values.put(MEALS_NAME, meal.getName());
            values.put(MEALS_ARTNAME, meal.getArtname());
            values.put(MEALS_DATE, meal.getDate());
            values.put(MEALS_PRICE_STUDENTS, meal.getPrice_students());
            values.put(MEALS_PRICE_BED, meal.getPrice_bed());
            values.put(MEALS_PRICE_GUEST, meal.getPrice_guest());
            values.put(MEALS_ADDITIVES, getArrayListAsString(meal.getAdditives()));
            values.put(MEALS_FOODTYPE, meal.getFoodtype());

            db.insertWithOnConflict(MEALS_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            db.close();

        } catch (Exception e) {
            Log.e("DB_MEAL_ERROR: ", e + "");
        }
    }

    public ArrayList<Meal> getMealsById(int mensa_id) {

        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<Meal> allMeals = null;

        try {

            allMeals = new ArrayList<Meal>();
            String QUERY = "SELECT * FROM " + MEALS_TABLE + " WHERE " + MEALS_MENSA_ID + " LIKE ?";

            Cursor cursor = db.rawQuery(QUERY, new String[]{"" + mensa_id});

            if (!cursor.isLast()) {

                while (cursor.moveToNext()) {

                    Meal tempMeal = new Meal();

                    tempMeal.setMensa_id(cursor.getInt(0));
                    tempMeal.setName(cursor.getString(1));
                    tempMeal.setArtname(cursor.getString(2));
                    tempMeal.setDate(cursor.getString(3));
                    tempMeal.setPrice_students(cursor.getString(4));
                    tempMeal.setPrice_bed(cursor.getString(5));
                    tempMeal.setPrice_guest(cursor.getString(6));
                    tempMeal.setAdditives(getStringAsArrayList(cursor.getString(7)));
                    tempMeal.setFoodtype(cursor.getString(8));

                    allMeals.add(tempMeal);

                }

            }

            db.close();


        } catch (Exception e) {
            Log.e("ERROR", e + "");
        }

        return allMeals;

    }

    public void deleteOldMeals() {

        this.getWritableDatabase().delete(MEALS_TABLE, null, null);

    }

    /*
    *
    * NewsItems functions
    *
    * */

    public void addNewsItem(NewsItem newsItem) {

        SQLiteDatabase db = this.getWritableDatabase();

        try {

            ContentValues values = new ContentValues();
            values.put(NEWS_ID, newsItem.getId());
            values.put(NEWS_TITLE, newsItem.getTitle());
            values.put(NEWS_TEXT, newsItem.getText());
            values.put(NEWS_CATEGORY, newsItem.getCategory());
            values.put(NEWS_TAGS, getArrayListAsString(newsItem.getTags()));
            values.put(NEWS_TIMESTAMP, newsItem.getTimestamp());

            db.insertWithOnConflict(NEWS_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            db.close();

        } catch (Exception e) {
            Log.e("DB_NEWS_PROBLEM", e + "");
        }
    }

    public ArrayList<NewsItem> getAllNews() {

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<NewsItem> allNews  = null;

        try {

            allNews = new ArrayList<NewsItem>();
            String QUERY = "SELECT * FROM " + NEWS_TABLE; // + " ORDER BY "+ NEWS_ID + " DESC";

            Cursor cursor = db.rawQuery(QUERY, null);

            if(!cursor.isLast()) {

                while (cursor.moveToNext()) {

                    NewsItem newsItem = new NewsItem();

                    newsItem.setId(cursor.getString(0));
                    newsItem.setTitle(cursor.getString(1));
                    newsItem.setText(cursor.getString(2));
                    newsItem.setCategory(cursor.getString(3));
                    newsItem.setTags(getStringAsArrayList(cursor.getString(4)));
                    newsItem.setTimestamp(cursor.getLong(5));

                    allNews.add(newsItem);

                }

            }

            db.close();


        } catch (Exception e) {
            Log.e("DB_NEWS_PROBLEM", e+"");
        }

        return allNews;

    }

    public void deleteOldNews() {

        this.getWritableDatabase().delete(NEWS_TABLE, null, null);

    }

    /*
    *
    * busplan functions
    *
    * */

    public void addBusplan(String linie, String url) {

        SQLiteDatabase db = this.getWritableDatabase();

        try {

            ContentValues values = new ContentValues();
            values.put(BUS_LINIE, linie);
            values.put(BUS_URL, url);

            db.insert(BUS_TABLE, null, values);
            db.close();

        } catch (Exception e) {
        }
    }

    public HashMap<String, String> getBusLinien() {

        HashMap<String, String> map = new HashMap<>();

        String selectQuery = "SELECT  * FROM " + BUS_TABLE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);


        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                map.put(c.getString(c.getColumnIndex(BUS_LINIE)), c.getString(c.getColumnIndex(BUS_URL)));

            } while (c.moveToNext());
        }

        return map;

    }

    public void deleteOldBusLinien() {

        this.getWritableDatabase().delete(BUS_TABLE, null, null);

    }

    /*
    *
    * helpful functions
    *
    * */

    private String getArrayListAsString(ArrayList<String> array) {

        String stringList = "";

        for (int i = 0; i < array.size(); i++) {

            if (i == array.size() - 1) stringList += array.get(i);
            else stringList += array.get(i) + ";";

        }

        return stringList;

    }

    private ArrayList<String> getStringAsArrayList(String stringList) {

        return new ArrayList<String>(Arrays.asList(stringList.split(";")));

    }

}