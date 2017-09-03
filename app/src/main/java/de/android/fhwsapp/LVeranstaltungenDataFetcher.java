package de.android.fhwsapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

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
import java.util.ArrayList;
import java.util.List;

import de.android.fhwsapp.Timetable.MyListAdapter;
import de.android.fhwsapp.Timetable.Subject;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by admin on 01.09.17.
 */

public class LVeranstaltungenDataFetcher extends AsyncTask<String, Void, String> {

    private String server_response;
    //todays Events
    private String todaysEvents = "";
    private ListView listView;
    private ProgressBar pbEvents;

    private List<Subject> subjectList;

    private Context context;
    private SharedPreferences preferences;

    public LVeranstaltungenDataFetcher(Context context, ProgressBar pbEvents, ListView listView, String todaysEvents) {

        this.context = context;
        preferences = context.getSharedPreferences(context.getPackageName(),MODE_PRIVATE);
        subjectList = new ArrayList<>();
        this.pbEvents = pbEvents;
        this.listView = listView;
        this.todaysEvents = todaysEvents;

    }


    @Override
    protected String doInBackground(String... strings) {

        URL url;
        HttpURLConnection urlConnection = null;

        try {
            url = new URL(strings[0]);
            urlConnection = (HttpURLConnection) url.openConnection();

            int responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                server_response = readStream(urlConnection.getInputStream());
                Log.v("TodaysEvents", server_response);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        pbEvents.setVisibility(View.GONE);

        //nur zu testzwecken
        //TODO für release löschen
        if (server_response == null || server_response.equals("[]"))
            server_response = "[\n" +
                    "  {\n" +
                    "    \"endTime\":\"2017-07-14T11:30:00+02:00\",\n" +
                    "    \"lecturerView\":[\n" +
                    "    ],\n" +
                    "    \"name\":\"Rechnerarchitektur\",\n" +
                    "    \"roomsView\":[\n" +
                    "      {\n" +
                    "        \"name\":\"I.3.24\"\n" +
                    "      }\n" +
                    "    ],\n" +
                    "    \"startTime\":\"2017-07-14T10:00:00+02:00\",\n" +
                    "    \"studentsView\":[\n" +
                    "      {\n" +
                    "        \"program\":\"ANY\",\n" +
                    "        \"semester\":0\n" +
                    "      }\n" +
                    "    ],\n" +
                    "    \"type\":\"Tutorial\"\n" +
                    "  }\n" +
                    "]";

        try {
            JSONArray jsonArray = new JSONArray(server_response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                addEvent(jsonObject);
            }

            MyListAdapter arrayAdapter = new MyListAdapter(context, subjectList);

            listView.setAdapter(arrayAdapter);

            preferences.edit().putString("todaysEvents", server_response).apply();
        } catch (Exception e) {
            Log.e("Response error", e.getMessage());
        }

        Log.e("Response", "" + server_response);

    }


// Converting InputStream to String

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


    private void addEvent(JSONObject jsonObject) {
        Subject subject = new Subject();
        try {
            subject.setSubjectName(jsonObject.getString("name"));
            subject.setTimeStart(new DateTime(jsonObject.getString("startTime")).toString("kk:mm"));
            subject.setTimeEnd(new DateTime(jsonObject.getString("endTime")).toString("kk:mm"));
            subject.setType(jsonObject.getString("type"));

            JSONArray jsonArrayTeacher = jsonObject.getJSONArray("lecturerView");
            JSONArray jsonArrayRooms = jsonObject.getJSONArray("roomsView");
            JSONArray jsonArrayStudents = jsonObject.getJSONArray("studentsView");

            //add rooms
            for (int i = 0; i < jsonArrayRooms.length(); i++) {
                if (subject.getRoom() == "")
                    subject.setRoom(jsonArrayRooms.getJSONObject(i).getString("name"));
                else
                    subject.setRoom(subject.getRoom() + ", " + jsonArrayRooms.getJSONObject(i).getString("name"));
            }

            //add teachers
            for (int i = 0; i < jsonArrayTeacher.length(); i++) {
                if (subject.getTeacher() == "")
                    subject.setTeacher(jsonArrayTeacher.getJSONObject(i).getString("name"));
                else
                    subject.setTeacher(subject.getTeacher() + ", " + jsonArrayTeacher.getJSONObject(i).getString("name"));
            }

            //add studiengang
            for (int i = 0; i < jsonArrayStudents.length(); i++) {
                if (jsonArrayStudents.getJSONObject(i).getString("program").equals("ANY"))
                    continue;

                if (subject.getStudiengang() == "")
                    subject.setStudiengang(jsonArrayStudents.getJSONObject(i).getString("program"));
                else
                    subject.setStudiengang(subject.getStudiengang() + ", " + jsonArrayStudents.getJSONObject(i).getString("program"));
            }

            subjectList.add(subject);

        } catch (Exception e) {
        }

    }


    public void offlineUse(){
        pbEvents.setVisibility(View.GONE);

        try {
            JSONArray jsonArray = new JSONArray(todaysEvents);
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                addEvent(jsonObject);
            }

            MyListAdapter arrayAdapter = new MyListAdapter(context, subjectList);

            listView.setAdapter(arrayAdapter);

        }catch (Exception e){
            Log.e("Response error",e.getMessage());
        }
    }

}

