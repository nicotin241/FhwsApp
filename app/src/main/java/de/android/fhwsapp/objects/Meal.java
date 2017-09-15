package de.android.fhwsapp.objects;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Meal {

    @SerializedName("mensaid")
    private int mensa_id;

    @SerializedName("name")
    private String name;

    @SerializedName("date")
    private String date;

    @SerializedName("pricebed")
    private String price_bed;

    @SerializedName("additives")
    private ArrayList<String> additives;

    @SerializedName("artname")
    private String artname;

    @SerializedName("priceguest")
    private String price_guest;

    @SerializedName("foodtype")
    private String foodtype;

    @SerializedName("price")
    private String price_students;

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

    public String getPrice_bed() {
        return price_bed;
    }

    public void setPrice_bed(String price_bed) {
        this.price_bed = price_bed;
    }

    public ArrayList<String> getAdditives() {
        return additives;
    }

    public void setAdditives(ArrayList<String> additives) {
        this.additives = additives;
    }

    public String getPrice_guest() {
        return price_guest;
    }

    public void setPrice_guest(String price_guest) {
        this.price_guest = price_guest;
    }
}
