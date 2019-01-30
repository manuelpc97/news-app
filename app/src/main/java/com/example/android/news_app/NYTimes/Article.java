package com.example.android.news_app.NYTimes;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

public class Article implements Parcelable {
    public String created_date;
    public String headline;
    public String source;
    public String section;
    public String subsection;
    public String title;
    public String url;
    public String thumbnail_standard;
    public Multimedia[] multimedia;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
