package com.example.android.news_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.android.news_app.NYTimes.Article;
import com.example.android.news_app.NYTimes.Multimedia;
import com.example.android.news_app.NYTimes.MultimediaArrayAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ArticleVideos extends AppCompatActivity {

    String json;
    String url;
    Article article;
    Gson gson;
    GsonBuilder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        builder = new GsonBuilder();
        builder.registerTypeAdapter(Multimedia[].class, new MultimediaArrayAdapter());
        gson = builder.create();

        Intent intent = this.getIntent();

        if(intent.hasExtra("JSON")){
            json =  intent.getStringExtra("JSON");
            article = gson.fromJson(json, Article.class);
        }

        if(intent.hasExtra("URL")){
            url = intent.getStringExtra("URL");
        }

        youtubeQuery();
    }

    public void youtubeQuery(){
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://www.googleapis.com/youtube/v3/search").newBuilder();
        urlBuilder.addQueryParameter("part", "snippet");
        urlBuilder.addQueryParameter("key", "AIzaSyC0R8Ozogl1GjP08rDNtILESXz_sNDj8sQ");
        urlBuilder.addQueryParameter("q", article.title);

        String url = urlBuilder.build().toString();
        Request request = new Request.Builder().url(url).build();

        Log.i("response", "Llegue");
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("success", "Llegue a okay2");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(!response.isSuccessful()){
                    Log.i("success", response.body().string());
                }else{
                    Log.i("success", response.body().string());
                }
            }
        });
    }
}
