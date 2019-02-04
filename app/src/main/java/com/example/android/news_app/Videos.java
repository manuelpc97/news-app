package com.example.android.news_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.news_app.Youtube.Resource;
import com.example.android.news_app.Youtube.YoutubeResponse;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Videos extends Fragment implements VideosAdapter.VideoClickListener{

    String videosJSON;
    YoutubeResponse videos;
    Gson gson;

    String YOUTUBE_GET_URL = "https://www.googleapis.com/youtube/v3/search";
    String API_KEY = "AIzaSyC0R8Ozogl1GjP08rDNtILESXz_sNDj8sQ";
    String YOUTUBE_VIDEO_URL;

    OkHttpClient client;
    Request request;
    HttpUrl.Builder httpBuilder;
    Bundle bundle;

    String title;
    Handler handler;
    Runnable handlerTask;

    VideosAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.video_grid_fragment , container, false);
        title = getArguments().getString("TITLE");
        gson = new Gson();



        client = new OkHttpClient();
        httpBuilder = HttpUrl.parse(YOUTUBE_GET_URL).newBuilder();
        httpBuilder.addQueryParameter("key", API_KEY);
        httpBuilder.addQueryParameter("q", title);
        httpBuilder.addQueryParameter("part", "snippet");
        httpBuilder.addQueryParameter("maxResults", "20");
        //httpBuilder.addQueryParameter("videoEmbedable", "true");

        request = new Request.Builder().url(httpBuilder.build()).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(!response.isSuccessful()){
                    Log.i("success", response.body().string());
                }else{
                    videosJSON = response.body().string();
                    videos = gson.fromJson(videosJSON, YoutubeResponse.class);
                    Log.i("success", videosJSON);

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            startRecyclerView(rootView);
                        }
                    });
                }
            }
        });

        return rootView;
    }

    public void startRecyclerView(View rootView){
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
        adapter = new VideosAdapter(videos.items, this);
        recyclerView.setAdapter(adapter);
        int largePadding = getResources().getDimensionPixelSize(R.dimen.shr_product_grid_spacing);
        int smallPadding = getResources().getDimensionPixelSize(R.dimen.shr_product_grid_spacing_small);
        recyclerView.addItemDecoration(new ProductGridItemDecoration(largePadding, smallPadding));
    }

    @Override
    public void onVideoClickListener(Resource clickedVideo) {
        String videoJSON =gson.toJson(clickedVideo);
        Intent intent = new Intent(this.getActivity(), video_player.class);
        intent.putExtra("JSON", videoJSON);
        startActivity(intent);
    }
}
