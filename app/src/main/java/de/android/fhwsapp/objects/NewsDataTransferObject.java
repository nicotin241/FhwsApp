package de.android.fhwsapp.objects;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class NewsDataTransferObject {

    @SerializedName("newsItems")
    private ArrayList<NewsItem> newsItems;

    @SerializedName("timestamp")
    private String timestamp;

}
