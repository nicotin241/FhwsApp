package de.android.fhwsapp.objects;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


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

    public Subject(Subject subject) {
        this.id = subject.getId();
        this.date = subject.getDate();
        this.timeStart = subject.getTimeStart();
        this.timeEnd = subject.getTimeEnd();
        this.type = subject.getType();
        this.subjectName = subject.getSubjectName();
        this.teacher = subject.getTeacher();
        this.room = subject.getRoom();
        this.info = subject.getInfo();
        this.gruppe = subject.getGruppe();
        this.year = subject.getYear();
        this.studiengang = subject.getStudiengang();
        this.semester = subject.getSemester();
        this.url = subject.getUrl();
        this.checked = subject.isChecked();
    }

    public Subject() {
    }

    public Subject(int id, String date, String timeStart, String timeEnd,
                   String type, String subjectName, String teacher, String room,
                   String info, String gruppe, String year, String studiengang, String semester, String url) {

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

    public String getInfo() {
        if (info != null)
            return info;
        else
            return "";
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getRoom() {
        if (room == null)
            return "";
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getTeacher() {
        if (teacher == null)
            return "";
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getSubjectName() {
        if (subjectName == null)
            return "";
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getTimeEnd() {
        if (timeEnd == null)
            return "";
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getTimeStart() {
        if (timeStart == null)
            return "";
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getType() {
        if (type == null)
            return "";
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        if (date == null)
            return "";
        return date;
    }

    public DateTime getDateAsDateTime() {
        return DateTime.parse(date, DateTimeFormat.forPattern("dd.MM.yy"));
    }

    public void setDateAsDateTime(DateTime date) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd.MM.yy");
        this.date = fmt.print(date);
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getGruppe() {
        if (gruppe == null)
            return "";
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
        if (year == null)
            return "";
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getStudiengang() {
        if (studiengang == null)
            return "";
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
        if (semester == null)
            return "";
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getUrl() {
        if (url == null)
            return "";
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
