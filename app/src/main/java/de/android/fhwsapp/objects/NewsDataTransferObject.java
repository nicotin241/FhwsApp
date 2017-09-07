package de.android.fhwsapp.objects;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class NewsDataTransferObject {

    @SerializedName("newsItems")
    private ArrayList<NewsItem> newsItems;

    @SerializedName("timestamp")
    private String timestamp;

    public ArrayList<NewsItem> getNewsItems() {
        return newsItems;
    }

    public void setNewsItems(ArrayList<NewsItem> newsItems) {
        this.newsItems = newsItems;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
