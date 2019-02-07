package com.example.android.news_app;

import android.content.res.Configuration;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabItem;
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
import com.google.android.gms.cast.MediaLoadOptions;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.Session;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.images.WebImage;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.gson.Gson;
import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.ChromecastYouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.ChromecastYouTubePlayerContext;
import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.io.infrastructure.ChromecastConnectionListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.YouTubePlayerInitListener;

import java.io.IOException;
import java.util.List;

import kotlin.Unit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class video_player extends AppCompatActivity implements YouTubePlayer.OnInitializedListener{

    String videoJSON;
    Resource video;
    Gson gson;

    String API_KEY ="AIzaSyBsZ06pyFqX2MjptShZCNWnpBsQWaLiRVo";


    Video[] videosToPlay;
    YouTubePlayerSupportFragment fragment;
    ViewPager pager;
    SectionsPagerAdapter pagerAdapter;
    FragmentManager manager;

    CastContext castContext;
    CastSession session;
    SessionManager sessionManager;
    SessionManagerListener  sessionListener = new SessionManagerListener<CastSession>() {
        @Override
        public void onSessionStarting(CastSession castSession) {
            Log.i("holis", "starting");
        }

        @Override
        public void onSessionStarted(CastSession castSession, String s) {
            Log.i("holis", "started");
            MediaMetadata metadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
            metadata.putString(MediaMetadata.KEY_TITLE,video.snippet.title);


            if(videosToPlay != null){
                Log.i("mistake", "antes del if");
                url = videosToPlay[0].url;
                type = videosToPlay[0].type;
                Log.i("mistake", "despues del if");
            }else{
                Log.i("mistake", "Soy null");
            }

            if(url != null){
                MediaInfo mediaInfo = new MediaInfo.Builder(url).
                        setStreamType(MediaInfo.STREAM_TYPE_BUFFERED).setContentType(type).setMetadata(metadata).setStreamDuration(player.getDurationMillis())
                        .build();

                RemoteMediaClient remoteMediaClient = castSession.getRemoteMediaClient();
                remoteMediaClient.load(mediaInfo);
            }else{
                MediaInfo mediaInfo = new MediaInfo.Builder("").setMetadata(metadata).build();
                RemoteMediaClient remoteMediaClient = castSession.getRemoteMediaClient();
                remoteMediaClient.load(mediaInfo);
            }
        }

        @Override
        public void onSessionStartFailed(CastSession castSession, int i) {
            Log.i("holis", "failed");
        }

        @Override
        public void onSessionEnding(CastSession castSession) {
            Log.i("holis", "ending");
        }

        @Override
        public void onSessionEnded(CastSession castSession, int i) {
            Log.i("holis", "ended");
        }

        @Override
        public void onSessionResuming(CastSession castSession, String s) {
            Log.i("holis", "resuming");
        }

        @Override
        public void onSessionResumed(CastSession castSession, boolean b) {
            Log.i("holis", "resumed");
        }

        @Override
        public void onSessionResumeFailed(CastSession castSession, int i) {
            Log.i("holis", "failed");
        }

        @Override
        public void onSessionSuspended(CastSession castSession, int i) {
            Log.i("holis", "suspended");
        }
    };

    YouTubePlayer player;

    String url;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        manager = getFragmentManager();
        Intent intent = getIntent();
        gson = new Gson();

        //initChromecast();

        if(intent.hasExtra("JSON")){
            videoJSON = intent.getStringExtra("JSON");
            video = gson.fromJson(videoJSON, Resource.class);
        }

        sessionManager = CastContext.getSharedInstance(this).getSessionManager();
        gettingURL();

        initializeYoutube();
        setupBar();
    }

    public void gettingURL(){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://you-link.herokuapp.com/?url=https://www.youtube.com/watch?v=" + video.id.videoId).build();

        Log.i("prueba", "llegue");
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("prueba", "error");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(!response.isSuccessful()){
                    Log.i("prueba", "unexpected code: " + response.body().string());
                }else{
                    videosToPlay = gson.fromJson(response.body().string(), Video[].class);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        if(sessionManager == null){
            sessionManager = CastContext.getSharedInstance(this).getSessionManager();
        }
        session = sessionManager.getCurrentCastSession();
        sessionManager.addSessionManagerListener(sessionListener);
        super.onResume();
    }



        @Override
    protected void onPause() {
        super.onPause();
        sessionManager.removeSessionManagerListener(sessionListener);
        session = null;
    }

    public Unit initChromecast(){
       new ChromecastYouTubePlayerContext(sessionManager, new SimpleChromecastConnectionListener());
       return Unit.INSTANCE;
    }

    private class SimpleChromecastConnectionListener implements ChromecastConnectionListener {
        @Override
        public void onChromecastConnecting() {
            Log.i("holis", "chromecast connecting");
        }
        @Override
        public void onChromecastConnected(
                ChromecastYouTubePlayerContext chromecastYouTubePlayerContext) {
            Log.i("holis","chromecast connected");
            initializeCastPlayer(chromecastYouTubePlayerContext);
        }
        @Override
        public void onChromecastDisconnected() {
            Log.i("holis", "chromecast disconnected");
        }
        private void initializeCastPlayer(
                ChromecastYouTubePlayerContext chromecastYouTubePlayerContext) {
            chromecastYouTubePlayerContext.initialize(new YouTubePlayerInitListener() {
                @Override
                public void onInitSuccess(final com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayer youtubePlayer) {
                    youtubePlayer.addListener(new AbstractYouTubePlayerListener() {
                        @Override
                        public void onReady() {
                            youtubePlayer.loadVideo("6JYIGclVQdw", 0f);
                        }
                    });
                }
            });
        }
    }


    public void initializeYoutube(){
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
        player = youTubePlayer;
        youTubePlayer.loadVideo(video.id.videoId);
        youTubePlayer.play();
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
}