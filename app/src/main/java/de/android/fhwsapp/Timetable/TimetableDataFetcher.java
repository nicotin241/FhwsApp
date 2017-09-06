package de.android.fhwsapp.Timetable;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.android.fhwsapp.Connect;
import de.android.fhwsapp.Database;
import de.android.fhwsapp.fragments.MainFragment;

/**
 * Created by admin on 04.09.17.
 */

public class TimetableDataFetcher extends AsyncTask<String, Void, String> {

    private Database database;
    private Context context;
    private SharedPreferences mPrefs;

    public TimetableDataFetcher(Context context){
        this.context = context;
        database = new Database(context);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private String server_response;

        @Override
        protected String doInBackground(String... strings) {

            Timetable.isLoading = true;

            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL("http://54.93.76.71:8080/FHWS/veranstaltungen?program=BIN");
                urlConnection = (HttpURLConnection) url.openConnection();

                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    server_response = readStream(urlConnection.getInputStream());
                    Log.v("CatalogClient", server_response);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }



            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            ArrayList<Subject> subs = new ArrayList<>();

            //sharedPreferences.edit().putLong(Timetable.LAST_UPDATE, DateTime.now().getMillis()).apply();

            try {
                JSONArray jsonArray = new JSONArray(server_response);
                database = new Database(context);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                    JSONArray events = jsonObject.getJSONArray("events");

                    for (int e = 0; e < events.length(); e++) {
                        Subject subject = new Subject();

                        JSONObject event = (JSONObject) events.get(e);

                        //id
                        int id = jsonObject.getInt("id");
                        subject.setId(id);

                        //look if checked
                        if(database.getSubjectWithID(id).isChecked())
                            subject.setChecked(true);

                        //endTime
                        String endTime = event.getString("endTime");
                        int indexTime = endTime.indexOf("T");
                        String day = endTime.substring(0, indexTime);
                        String time = endTime.substring(indexTime + 1, endTime.length() - 9);
                        subject.setTimeEnd(time);

                        try {
                            Date date = dateFormat.parse(day);
                            dateFormat.applyPattern("dd.MM.yy");
                            String d = dateFormat.format(date);
                            subject.setDate(d);

                        } catch (Exception ex) {
                            String[] dateArray = day.split("-");
                            String newDate = dateArray[2] + "." + dateArray[1] + "." + dateArray[0].substring(2);
                            subject.setDate(newDate);
                        }

                        //teacher
                        JSONArray lecturerView = (JSONArray) event.getJSONArray("lecturerView");
                        StringBuilder lecturers = new StringBuilder();
                        for (int y = 0; y < lecturerView.length(); y++) {
                            JSONObject lecturer = lecturerView.getJSONObject(y);
                            String teacher = null;
                            if (!lecturer.getString("title").equals(""))
                                teacher = lecturer.getString("title") + " " + lecturer.getString("lastName");
                            else
                                teacher = lecturer.getString("lastName");

                            if (y == 0)
                                lecturers.append(teacher);
                            else
                                lecturers.append(", " + teacher);

                        }
                        subject.setTeacher(lecturers.toString());

                        //name
                        String name = jsonObject.getString("name");
                        subject.setSubjectName(name);

                        //room
                        JSONArray roommsView = (JSONArray) event.getJSONArray("roomsView");
                        StringBuilder rooms = new StringBuilder();
                        for (int y = 0; y < roommsView.length(); y++) {
                            JSONObject room = roommsView.getJSONObject(y);

                            String raum = room.getString("name");

                            if (y == 0)
                                rooms.append(raum);
                            else
                                rooms.append(", " + raum);

                        }
                        subject.setRoom(rooms.toString());

                        //startTime
                        String startTime = event.getString("startTime");
                        indexTime = startTime.indexOf("T");
                        time = startTime.substring(indexTime + 1, startTime.length() - 9);
                        subject.setTimeStart(time);


                        //type
                        String type = event.getString("type");
                        subject.setType(type);

                        //semester, studiengang
                        JSONArray studentsView = (JSONArray) event.getJSONArray("studentsView");
                        for (int y = 0; y < studentsView.length(); y++) {
                            JSONObject students = studentsView.getJSONObject(y);

                            String programm = students.getString("program");
                            int semester = students.getInt("semester");

                            if (y == 0) {
                                subject.setStudiengang(programm);
                                subject.setSemester("" + semester);
                            } else {
                                Subject subject2 = new Subject(subject);
                                subject2.setStudiengang(programm);
                                subject2.setSemester("" + semester);
                                //database.createSubject(subject2);
                                subs.add(subject2);
                            }

                        }

                        //database.createSubject(subject);
                        subs.add(subject);

                    }
                }
            } catch (Exception e) {
                Log.e("Lesen der Vorlesungen",e.getMessage());
            }

            Log.e("Response", "" + server_response);

            //delete old subjects
            if(subs.size() > 0)
                database.deleteAllSubjects();

            //add new subjects
            for(Subject su : subs){
                database.createSubject(su);
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Timetable.isLoading = false;
            mPrefs.edit().putLong("lastUpdate",DateTime.now().getMillis()).apply();

            //reload
            Connect.sendToAllListeners();

        }


    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }
}
