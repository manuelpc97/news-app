package com.example.android.news_app;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.news_app.NYTimes.Article;
import com.example.android.news_app.NYTimes.Multimedia;
import com.example.android.news_app.NYTimes.MultimediaArrayAdapter;
import com.example.android.news_app.Youtube.YoutubeResponse;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ArticleDetail extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    public Article article;
    String json;
    String url;
    Gson gson;
    GsonBuilder builder;

    int HEIGHT = 293;
    int WIDTH = 440;


    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        builder = new GsonBuilder();
        builder.registerTypeAdapter(Multimedia[].class, new MultimediaArrayAdapter());
        gson = builder.create();

        Intent intent = getIntent();
        if(intent.hasExtra("JSON")){
            json = intent.getStringExtra("JSON");
            article = gson.fromJson(json, Article.class);
        }

        if(intent.hasExtra("URL")){
            url = intent.getStringExtra("URL");
        }
        setupTabs();
    }

    public void setupTabs(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), article.title);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.home_menu, menu);
        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(),
                menu,
                R.id.media_route_menu_item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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


    public void goToLogIn(){
        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        String title;

        public SectionsPagerAdapter(FragmentManager fm,String title) {
            super(fm);
            this.title = title;
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            switch(position){
               case 0:
                   Detail detail = new Detail();
                   bundle.putString("URL", article.url);
                   detail.setArguments(bundle);
                   return detail;

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
                    return "Article";

                case 1:
                    return "Videos";
            }

            return "";
        }
    }

}
