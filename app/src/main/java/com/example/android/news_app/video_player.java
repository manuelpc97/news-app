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
import android.widget.PopupMenu;

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

import static com.example.android.news_app.MainActivity.simpleListener;

public class video_player extends AppCompatActivity implements YouTubePlayer.OnInitializedListener, YouTubePlayer.PlaybackEventListener, YouTubePlayer.PlayerStateChangeListener{

    String videoJSON;
    Resource video;
    Gson gson;

    String API_KEY ="AIzaSyDj_hWlpf2RnIoOTQ0jJ0EnX8DRPG0A1Hk";


    Video[] videosToPlay;
    YouTubePlayerSupportFragment fragment;
    ViewPager pager;
    SectionsPagerAdapter pagerAdapter;
    FragmentManager manager;



    YouTubePlayer player;
    com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayer chromecastPlayer;
    boolean settedSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        manager = getFragmentManager();
        Intent intent = getIntent();
        gson = new Gson();

        if(intent.hasExtra("JSON")){
            videoJSON = intent.getStringExtra("JSON");
            video = gson.fromJson(videoJSON, Resource.class);
        }

        initializeYoutube();
        setupBar();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(simpleListener.player != null){
            simpleListener.player.play();
        }
        //playerContext.endCurrentSession();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outPersistentState.putBoolean("BOOLEAN", settedSession);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        settedSession = savedInstanceState.getBoolean("BOOLEAN");
    }

    public Unit initChromecast(){
       simpleListener.setId(video.id.videoId);
       simpleListener.setYouTubePlayer(player);
       //simpleListener.setContext(getApplicationContext());
       return Unit.INSTANCE;
    }

    @Override
    public void onPlaying() {
        Log.i("playing", "playing");
        if(simpleListener.player != null){
            if(simpleListener.id.equals(video.id.videoId)){
                simpleListener.player.play();
            }else{
                simpleListener.setId(video.id.videoId);
                simpleListener.player.loadVideo(video.id.videoId,0f);
            }
        }
    }

    @Override
    public void onPaused() {
        if(simpleListener.player != null){
            simpleListener.player.pause();
        }
    }

    @Override
    public void onStopped() {
        Log.i("playing", "stopped");

        //playerContext.endCurrentSession();
    }

    @Override
    public void onBuffering(boolean b) {

    }

    @Override
    public void onSeekTo(int i) {
        if(simpleListener.player != null){
            simpleListener.player.seekTo(i);
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
            PopupMenu popupMenu = new PopupMenu(this, findViewById(id));
            popupMenu.getMenuInflater().inflate(R.menu.pop_up, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int itemID =item.getItemId();
                    if(itemID == R.id.logout_item){
                        goToLogIn();
                    }
                    return true;
                }
            });
            popupMenu.show();
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
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        player = youTubePlayer;
        player.setPlaybackEventListener(this);
        player.setPlayerStateChangeListener(this);
        player.loadVideo(video.id.videoId);
        initChromecast();
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

    @Override
    public void onLoading() {

    }

    @Override
    public void onLoaded(String s) {

    }

    @Override
    public void onAdStarted() {

    }

    @Override
    public void onVideoStarted() {
        Log.i("playing", "starting");
        if(simpleListener.player != null){
            simpleListener.setId(video.id.videoId);
            simpleListener.player.loadVideo(video.id.videoId,0f);
        }
    }

    @Override
    public void onVideoEnded() {
        Log.i("playing", "finalice");
    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {

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


}