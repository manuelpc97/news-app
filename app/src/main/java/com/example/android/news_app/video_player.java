package com.example.android.news_app;

import android.content.res.Configuration;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.android.news_app.Casting.Video;
import com.example.android.news_app.Youtube.Resource;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.gson.Gson;
import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.ChromecastYouTubePlayerContext;
import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.io.infrastructure.ChromecastConnectionListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.YouTubePlayerInitListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.YouTubePlayerListener;

import java.io.IOException;
import java.util.List;

import kotlin.Unit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class video_player extends AppCompatActivity implements YouTubePlayer.OnInitializedListener, YouTubePlayer.PlaybackEventListener{

    String videoJSON;
    Resource video;
    Gson gson;

    String API_KEY ="AIzaSyA7Q0jbZTE3Vh3wLfSPxTvegWtL8l7NdB8";


    Video[] videosToPlay;
    YouTubePlayerSupportFragment fragment;
    ViewPager pager;
    SectionsPagerAdapter pagerAdapter;
    FragmentManager manager;

    CastSession session;
    SessionManager sessionManager;

    YouTubePlayer player;
    com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayer chromecastPlayer;
    boolean sessionStarted = false;
    ChromecastYouTubePlayerContext playerContext;
    PlayerListener listener;
    YouTubePlayerInitListener initListener;
    SimpleChromecastConnectionListener simpleListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        manager = getFragmentManager();
        Intent intent = getIntent();
        gson = new Gson();

        sessionManager = CastContext.getSharedInstance(this).getSessionManager();

        if(intent.hasExtra("JSON")){
            videoJSON = intent.getStringExtra("JSON");
            video = gson.fromJson(videoJSON, Resource.class);
        }

        Log.i("playing", "createID");
        Log.i("playing", video.id.videoId);
        initializeYoutube();
        initChromecast();
        setupBar();
    }


    @Override
    protected void onResume() {
        Log.i("playing", "resume");
        if(sessionManager == null){
            sessionManager = CastContext.getSharedInstance(this).getSessionManager();
        }
       //session = sessionManager.getCurrentCastSession();
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.i("playing", "paused");
        super.onPause();
        //session = null;
    }

    @Override
    protected void onDestroy() {
        Log.i("playing", "ONDESTROY");
        super.onDestroy();
        player.release();
//        sessionManager.endCurrentSession(true);
        //chromecastPlayer = null;
        session = null;
        playerContext.removeChromecastConnectionListener(simpleListener);
        playerContext = null;
        initListener = null;
    }

    public Unit initChromecast(){
        Log.i("playing", "initialized_chromecast");
        Log.i("playing", video.id.videoId);
        Log.i("playing", sessionManager.toString());
        simpleListener = new SimpleChromecastConnectionListener(video.id.videoId);
       playerContext = new ChromecastYouTubePlayerContext(sessionManager, simpleListener);
       return Unit.INSTANCE;
    }

    @Override
    public void onPlaying() {
        Log.i("playing", "playing");
        if(chromecastPlayer != null){
            chromecastPlayer.play();
        }
    }

    @Override
    public void onPaused() {
        if(chromecastPlayer != null){
            chromecastPlayer.pause();
        }
    }

    @Override
    public void onStopped() {
        Log.i("playing", "stopped");

    }

    @Override
    public void onBuffering(boolean b) {

    }

    @Override
    public void onSeekTo(int i) {
        if(chromecastPlayer != null){
            chromecastPlayer.seekTo(i);
        }
    }

    private class SimpleChromecastConnectionListener implements ChromecastConnectionListener {
        String ID;
        int counter = 0;

        public SimpleChromecastConnectionListener(String id){
            Log.i("playing", "setting " + id);
            ID = id;
        }


        @Override
        public void onChromecastConnecting() {
            Log.i("holis", "chromecast connecting");
        }
        @Override
        public void onChromecastConnected(
                ChromecastYouTubePlayerContext chromecastYouTubePlayerContext) {
            Log.i("playing","chromecast connected");
            Log.i("playing", ID);
            if(counter == 0){
                counter++;
                initListener = new YouTubePlayerInitListener() {
                    @Override
                    public void onInitSuccess(final com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayer youtubePlayer) {
                        listener.videoId = ID;
                        Log.i("playing", "before");
                        Log.i("playing", listener.videoId);
                        Log.i("playing", ID);
                        listener.youtubePlayer = youtubePlayer;
                        chromecastPlayer = youtubePlayer;
                        youtubePlayer.addListener(listener);
                    }
                };
                chromecastYouTubePlayerContext.initialize(initListener);

            }
        }

        @Override
        public void onChromecastDisconnected() {
            Log.i("playing", "chromecast disconnected");
            sessionManager = null;
            chromecastPlayer = null;
            session = null;
            playerContext.removeChromecastConnectionListener(this);

        }
    }


    public void initializeYoutube(){
        Log.i("playing", "initialized_youtube");
        fragment = (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.video_container);
        fragment.initialize(API_KEY, this);
    }

    public void setupBar(){
        String image_url = "https://upload.wikimedia.org/wikipedia/commons/e/e1/Logo_of_YouTube_%282015-2017%29.svg";

        if(video.snippet.thumbnails.medium != null){
            image_url = video.snippet.thumbnails.medium.url;
        }
         pagerAdapter= new SectionsPagerAdapter(getSupportFragmentManager(), video.snippet.title,video.snippet.description, image_url);

        pager = (ViewPager) findViewById(R.id.container2);
        pager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs2);

        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pager));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.log_out){
            goToLogIn();
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.home_menu, menu);
        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(),
                menu,
                R.id.media_route_menu_item);
        return true;
    }


    public void goToLogIn(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        Log.i("playing", "initialized_youtube2");
        Log.i("playing", video.id.videoId);
        player = youTubePlayer;
        player.loadVideo(video.id.videoId);
        player.setPlaybackEventListener(this);
        listener = new PlayerListener(player,video.id.videoId);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.i("full", "llegue: ");
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            fragment.getView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            getSupportActionBar().hide();
        }else{
            fragment.getView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
            getSupportActionBar().show();
        }
        super.onConfigurationChanged(newConfig);
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        String title;
        String description;
        String url;

        public SectionsPagerAdapter(android.support.v4.app.FragmentManager fm,String title, String description, String url) {
            super(fm);
            this.title = title;
            this.description = description;
            this.url = url;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            switch(position){
                case 0:
                    VideoDescrptionFragment fragment = new VideoDescrptionFragment();
                    bundle.putString("TITLE", title);
                    bundle.putString("DESCRIPTION", description);
                    bundle.putString("URL", url);
                    fragment.setArguments(bundle);
                    return fragment;

                case 1:
                    Videos videos = new Videos();
                    bundle.putString("TITLE", title);
                    bundle.putString("JSON", videoJSON);
                    videos.setArguments(bundle);
                    return videos;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch(position){
                case 0:
                    return "Description";

                case 1:
                    return "More Videos";
            }

            return "";
        }
    }

    public class InitPlayerListener implements YouTubePlayerInitListener{
        String ID;

        public InitPlayerListener(String id){
            this.ID = id;
        }

        @Override
        public void onInitSuccess(com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayer youTubePlayer) {

        }
    }

    public class PlayerListener extends AbstractYouTubePlayerListener{
        public String videoId;
        public YouTubePlayer player;
        public com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayer youtubePlayer;

        PlayerListener(YouTubePlayer player, String videoId){
            this.player = player;
            this.videoId = videoId;
        }

        @Override
        public void onReady() {
            super.onReady();

            Log.i("playing", "intialized_chromecast2");
            Log.i("playing", videoId);
            youtubePlayer.loadVideo(videoId, 0f );
            youtubePlayer.play();
            chromecastPlayer = youtubePlayer;
        }
    }
}