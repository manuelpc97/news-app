package com.example.android.news_app;

import android.util.Log;

import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.ChromecastYouTubePlayerContext;
import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.io.infrastructure.ChromecastConnectionListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.YouTubePlayerInitListener;

public class SimpleChromecastConnectionListener implements ChromecastConnectionListener {

    public String id;
    int time;
    public com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayer player;
    public com.google.android.youtube.player.YouTubePlayer youTubePlayer;



    SimpleChromecastConnectionListener(String id){
        this.id = id;
    }

    public void setYouTubePlayer(com.google.android.youtube.player.YouTubePlayer player){
        this.youTubePlayer = player;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public void onChromecastConnecting() {
        Log.d(getClass().getSimpleName(), "onChromecastConnecting");
    }
    @Override
    public void onChromecastConnected(
            ChromecastYouTubePlayerContext chromecastYouTubePlayerContext) {
        Log.i("playing", "onChromecastConnected");
        Log.i("playing", id);
        initializeCastPlayer(chromecastYouTubePlayerContext);
    }
    @Override
    public void onChromecastDisconnected() {
        Log.d(getClass().getSimpleName(), "onChromecastDisconnected");
        player = null;
    }
    private void initializeCastPlayer(
            ChromecastYouTubePlayerContext chromecastYouTubePlayerContext) {
        Log.i("playing", "initializedCastPlayer");
        Log.i("playing", id);
        chromecastYouTubePlayerContext.initialize(new YouTubePlayerInitListener() {
            @Override
            public void onInitSuccess(final com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayer youtubePlayer) {
                Log.i("playing", "onInitSuccess");
                Log.i("playing", id);
                youtubePlayer.addListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady() {
                        Log.i("playing", "onReady");
                        Log.i("playing", id);
                        player = youtubePlayer;
                        time = youTubePlayer.getCurrentTimeMillis() + 10;
                        player.loadVideo(id, 0f);
                    }
                });
            }
        });
    }
}
