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
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.android.fhwsapp.Timetable.Subject;
import de.android.fhwsapp.objects.Meal;

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
    private static final String KEY_SEMESTER ="semester";
    private static final String KEY_STUDIENGANG ="studiengang";
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
            + KEY_SEMESTER + " TEXT,"
            + KEY_STUDIENGANG + " TEXT,"
            + " PRIMARY KEY(" + KEY_Name + "," + KEY_Date + "," + KEY_Group + ")"
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
    private static final String MEALS_FOODTYPE = "_foodtype";

    String CREATE_MEALS_TABLE = "CREATE TABLE " + MEALS_TABLE + " ("
            + MEALS_MENSA_ID + " INTEGER,"
            + MEALS_NAME + " TEXT,"
            + MEALS_ARTNAME + " TEXT,"
            + MEALS_DATE + " TEXT,"
            + MEALS_PRICE_STUDENTS + " TEXT,"
            + MEALS_FOODTYPE + " TEXT);";

    String DROP_MEALS_TABLE = "DROP TABLE IF EXISTS " + MEALS_TABLE;



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

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBJECTS);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAG);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO_TAG);

        db.execSQL(DROP_MEALS_TABLE);

        // create new tables
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
        values.put(KEY_SEMESTER, subject.getSemester());
        values.put(KEY_STUDIENGANG, subject.getStudiengang());
        if (subject.isChecked())
            values.put(KEY_Selected, "true");
        else
            values.put(KEY_Selected, "false");

        values.put(KEY_CREATED_AT, getDateTime());

        // insert row
        long todo_id = db.insert(TABLE_SUBJECTS, null, values);


        return todo_id;
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
        subject.setSemester(c.getString(c.getColumnIndex(KEY_SEMESTER)));
        if (c.getString(c.getColumnIndex(KEY_Selected)).equals("true"))
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
                subject.setStudiengang(c.getString(c.getColumnIndex(KEY_STUDIENGANG)));
                subject.setSemester(c.getString(c.getColumnIndex(KEY_SEMESTER)));
                if (c.getString(c.getColumnIndex(KEY_Selected)).equals("true"))
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
        String selectQuery = "SELECT  * FROM " + TABLE_SUBJECTS + " WHERE " + KEY_Selected + " = true";

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
                subject.setSemester(c.getString(c.getColumnIndex(KEY_SEMESTER)));
                if (c.getString(c.getColumnIndex(KEY_Selected)).equals("true"))
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
        values.put(KEY_SEMESTER, subject.getSemester());
        values.put(KEY_STUDIENGANG, subject.getStudiengang());
        if (subject.isChecked())
            values.put(KEY_Selected, "true");
        else
            values.put(KEY_Selected, "false");

        values.put(KEY_CREATED_AT, getDateTime());

        // updating row
        return db.update(TABLE_SUBJECTS, values, KEY_Name + " = ?" + " AND " + KEY_Group + " = ?" + " AND " + KEY_Date + " = ?",
                new String[]{String.valueOf(subject.getSubjectName()), subject.getGruppe(), subject.getDate()});
    }

    public int updateSubjectInfo(Subject subject) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_Info, subject.getInfo());

        // updating row
        return db.update(TABLE_SUBJECTS, values, KEY_Name + " = ?" + " AND " + KEY_Group + " = ?" + " AND " + KEY_Date + " = ?",
                new String[]{String.valueOf(subject.getSubjectName()), subject.getGruppe(), subject.getDate()});
    }

    public int updateCheckSubjects(Subject subject) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_Selected, "true");

        // updating row
        return db.update(TABLE_SUBJECTS, values, KEY_Name + " = ?" + " AND " + KEY_Group + " = ?" + " AND " + KEY_Date + " = ?",
                new String[]{String.valueOf(subject.getSubjectName()), subject.getGruppe(), subject.getDate()});
    }

    public int updateUncheckSubjects(Subject subject) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_Selected, "false");

        // updating row
        return db.update(TABLE_SUBJECTS, values, KEY_Name + " = ?" + " AND " + KEY_Group + " = ?" + " AND " + KEY_Date + " = ?",
                new String[]{String.valueOf(subject.getSubjectName()), subject.getGruppe(), subject.getDate()});
    }

    public void delete(String name, String group, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SUBJECTS, KEY_Name + " = ?" + " AND " + KEY_Group + " = ?" + " AND " + KEY_Date + " = ?",
                new String[]{String.valueOf(name), group, date});
    }


    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public int getWeekCount() {
        String selectQuery = "SELECT  * FROM " + TABLE_SUBJECTS;

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
                    first = false;
                }

                lastWeek = subject.getDate();

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

    public ArrayList<Subject>[][] getSortedSubjects(int days, int weeks) {
        ArrayList<Subject>[][] subjects = new ArrayList[weeks][days];
        String selectQuery = "SELECT  * FROM " + TABLE_SUBJECTS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        for (int w = 0; w < weeks; w++)
            for (int d = 0; d < days; d++)
                subjects[w][d] = new ArrayList<>();

        int w = 0, d = 0, index = 0;
        DateTime lastDate = null;

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
                subject.setSemester(c.getString(c.getColumnIndex(KEY_SEMESTER)));
                if (c.getString(c.getColumnIndex(KEY_Selected)).equals("true"))
                    subject.setChecked(true);
                else
                    subject.setChecked(false);

                //erster Durchlauf
                if (lastDate == null) {

                    int length = subject.getDateAsDateTime().dayOfWeek().get() - 1;
                    for (int i = 0; i < length; i++) {
                        DateTime currentDay = subject.getDateAsDateTime().minusDays(length - i);

                        d++;

                        Subject emptySubject = new Subject();
                        emptySubject.setDateAsDateTime(currentDay);
                        subjects[w][d].add(index, emptySubject);
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
                                    subjects[w][d].add(index, emptySubject);
                                }

                            }

                            d++;

                            break;
                    }

                    lastDate = subject.getDateAsDateTime();
                    try {
                        subjects[w][d].add(index, subject);
                    }catch (Exception e){
                        w++;
                        d = 0;
                        subjects[w][d].add(index, subject);
                    }

                }


            } while (c.moveToNext());
        }

        //fill rest days
        if(d != 0)
        for(int i = d+1; i < 7; i++){
            DateTime lastDay = subjects[w][i-1].get(0).getDateAsDateTime();
            Subject emptySubject = new Subject();
            emptySubject.setDateAsDateTime(lastDay.plusDays(1));
            subjects[w][i].add(index, emptySubject);
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
            values.put(MEALS_FOODTYPE, meal.getFoodtype());

            db.insert(MEALS_TABLE, null, values);
            db.close();

        } catch (Exception e) {
            Log.e("DB_HOTEL_PROBLEM", e+"");
        }
    }

    public ArrayList<Meal> getMealsById(int mensa_id) {

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Meal> allMeals  = null;

        try {

            allMeals = new ArrayList<Meal>();
            String QUERY = "SELECT * FROM " + MEALS_TABLE + " WHERE " + MEALS_MENSA_ID + " LIKE ?";

            Cursor cursor = db.rawQuery(QUERY, new String[] {"" + mensa_id});

            if(!cursor.isLast()) {

                while (cursor.moveToNext()) {

                    Meal tempMeal = new Meal();

                    tempMeal.setMensa_id(cursor.getInt(0));
                    tempMeal.setName(cursor.getString(1));
                    tempMeal.setArtname(cursor.getString(2));
                    tempMeal.setDate(cursor.getString(3));
                    tempMeal.setPrice_students(cursor.getString(4));
                    tempMeal.setFoodtype(cursor.getString(5));

                    allMeals.add(tempMeal);

                }

            }

            db.close();


        } catch (Exception e) {
            Log.e("ERROR", e+"");
        }

        return allMeals;

    }

    public void deleteOldMeals() {

        this.getWritableDatabase().delete(MEALS_TABLE, null, null);

    }








}