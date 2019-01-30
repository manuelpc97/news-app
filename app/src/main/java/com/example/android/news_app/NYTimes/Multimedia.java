package com.example.android.news_app.NYTimes;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Multimedia implements Parcelable {
    public String url;
    public int height;
    public int width;
    public String caption;
    public String copyright;



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
