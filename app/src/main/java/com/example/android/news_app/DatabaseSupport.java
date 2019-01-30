package com.example.android.news_app;

import android.provider.MediaStore;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class DatabaseSupport {
    MediaType mediaType;
    String apiKey;
    String postURL;
    String getURL;
    String database;
    String collection;

    DatabaseSupport(){
        mediaType = MediaType.get("application/json; charset=utf-8");
        apiKey = "MBWoNkzClxMu1PqHPhnXPQ0McFaP33cQ";
        database = "news_app";
        collection = "User";
        postURL = "https://api.mlab.com/api/1/databases/" + database + "/collections/" + collection + "?apiKey=" + apiKey;
        getURL =  "https://api.mlab.com/api/1/databases/news_app/collections/User?apiKey=" + apiKey;
    }

    public Request post(String json){
           RequestBody body = RequestBody.create(mediaType, json);
           Request request = new Request.Builder().url(postURL).post(body).build();

           return request;
    }

    public Request get(){
        Request request = new Request.Builder().url(getURL).build();
        return request;
    }
}
