package de.android.fhwsapp.Timetable;

import org.json.JSONObject;


public class Subject {


    private int id;
    private String date;
    private String timeStart;
    private String timeEnd;
    private String type;
    private String subjectName;
    private String teacher;
    private String room;
    private String info;
    private String gruppe;

    private boolean checked = false;

    public Subject(JSONObject jsonObject) {
        //fill fields
    }

    public Subject(){};

    //gruppe muss noch hinzugefügt werden
    public Subject(int id, String date, String timeStart, String timeEnd, String type, String subjectName, String teacher, String room, String info){
        this.date = date;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.type = type;
        this.subjectName = subjectName;
        this.teacher = teacher;
        this.room = room;
        this.info = info;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getRoom() {

        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getTeacher() {

        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getSubjectName() {

        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getTimeEnd() {

        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getTimeStart() {

        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getType() {

        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {

        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getGruppe() {
        return gruppe;
    }

    public void setGruppe(String gruppe) {
        this.gruppe = gruppe;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
