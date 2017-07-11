package de.android.fhwsapp.Timetable;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONObject;


public class Subject {


    private String date;
    private String timeStart;
    private String timeEnd;
    private String type;
    private String subjectName;
    private String teacher;
    private String room;
    private String info;
    private String gruppe;
    private String year;
    private String studiengang;
    private int id;
    private String semester;
    private String url;
    private boolean checked = false;

    public Subject(JSONObject jsonObject) {
        //fill fields
    }

    public Subject(){};

    public Subject(int id, String date, String timeStart, String timeEnd,
                   String type, String subjectName, String teacher, String room,
                   String info, String gruppe, String year, String studiengang, String semester, String url){

        this.id = id;
        this.date = date;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.type = type;
        this.subjectName = subjectName;
        this.teacher = teacher;
        this.room = room;
        this.info = info;
        this.gruppe = gruppe;
        this.year = year;
        this.studiengang = studiengang;
        this.semester = semester;
        this.url = url;

    }

//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }

    public String getInfo() {
        if(info != null)
            return info;
        else
            return "";
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

    public DateTime getDateAsDateTime(){
        return DateTime.parse(date, DateTimeFormat.forPattern("dd.MM.yy"));
    }

    public void setDateAsDateTime(DateTime date){
        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd.MM.yy");
        this.date = fmt.print(date);
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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getStudiengang() {
        return studiengang;
    }

    public void setStudiengang(String studiengang) {
        this.studiengang = studiengang;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
