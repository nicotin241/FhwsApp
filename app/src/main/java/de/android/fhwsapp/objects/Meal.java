package de.android.fhwsapp.objects;

/**
 * Created by alex on 24.06.17.
 */

public class Meal {

    /*
    * ID for Mensa Location
    *
    * Burse: 9
    *
    * */

    private int mensa_id;

    private String name;
    private String artname;
    private String date;
    private String price_students;
    private String foodtype;

    public Meal() {

    }

    public int getMensa_id() {
        return mensa_id;
    }

    public void setMensa_id(int mensa_id) {
        this.mensa_id = mensa_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtname() {
        return artname;
    }

    public void setArtname(String artname) {
        this.artname = artname;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPrice_students() {
        return price_students;
    }

    public void setPrice_students(String price_students) {
        this.price_students = price_students;
    }

    public String getFoodtype() {
        return foodtype;
    }

    public void setFoodtype(String foodtype) {
        this.foodtype = foodtype;
    }
}
