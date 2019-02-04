package com.example.android.news_app;

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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.android.news_app.Youtube.Resource;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.gson.Gson;

public class video_player extends AppCompatActivity implements YouTubePlayer.OnInitializedListener {

    String videoJSON;
    Resource video;
    Gson gson;

    String API_KEY ="AIzaSyC0R8Ozogl1GjP08rDNtILESXz_sNDj8sQ";

    YouTubePlayerSupportFragment fragment;
    ViewPager pager;
    SectionsPagerAdapter pagerAdapter;
    FragmentManager manager;

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


        fragment = (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.video_container);
        fragment.initialize(API_KEY, this);
        setupBar();
    }



    public void setupBar(){
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
         pagerAdapter= new SectionsPagerAdapter(getSupportFragmentManager(), video.snippet.title,video.snippet.description, video.snippet.channelTitle);

        // Set up the ViewPager with the sections adapter.
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        //youTubePlayer.loadVideo(video.id.videoId);
        //youTubePlayer.play();
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        String title;
        String description;
        String channel;

        public SectionsPagerAdapter(android.support.v4.app.FragmentManager fm,String title, String description, String channel) {
            super(fm);
            this.title = title;
            this.description = description;
            this.channel = channel;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            switch(position){
                case 0:
                    VideoDescrptionFragment fragment = new VideoDescrptionFragment();
                    bundle.putString("TITLE", title);
                    bundle.putString("DESCRIPTION", description);
                    bundle.putString("CHANNEL", channel);
                    fragment.setArguments(bundle);
                    return fragment;

                case 1:
                    Videos videos = new Videos();
                    bundle.putString("TITLE", title);
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