package com.example.android.news_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.MediaRouteButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.android.news_app.NYTimes.Article;
import com.example.android.news_app.NYTimes.Multimedia;
import com.example.android.news_app.NYTimes.Container;
import com.example.android.news_app.NYTimes.MultimediaArrayAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Home extends AppCompatActivity implements ArticleAdapter.ArticleClickListener{

    int number_of_items;
    ArticleAdapter adapter;
    RecyclerView articlesRecyclerview;

    String userName;
    String userEmail;
    String userId;

    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleClient;

    OkHttpClient client;
    HttpUrl.Builder builder;
    GsonBuilder gsonBuilder = new GsonBuilder();
    Gson gson;

    Container container;
    Toast toast;
    String errorMessage = "ERROR, CHECK INTERNET CONNECTION";
    ProgressBar loading;
    CastContext castContext;
    //MediaRouteButton mMediaRouteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        loading = (ProgressBar)findViewById(R.id.pb1);

        //mMediaRouteButton = (MediaRouteButton) findViewById(R.id.media_route_button);
        //CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), mMediaRouteButton);

        //castContext = CastContext.getSharedInstance(this);

        if(savedInstanceState == null){
            Bundle extras = getIntent().getExtras();
            if(extras.containsKey("user")){
                userName = extras.getString("user");
            }

            if(extras.containsKey("email")){
                userEmail = extras.getString("email");
            }

            if(extras.containsKey("id")){
                userId = extras.getString("id");
            }

            Log.i("success", userName);
            Log.i("success", userEmail);
        }

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleClient = GoogleSignIn.getClient(this,googleSignInOptions);

        number_of_items = 20;

        client = new OkHttpClient();
        Request request = new Request.Builder().
               url("https://api.nytimes.com/svc/news/v3/content/all/all.json?api-key=EL6ZjeqVKQp9YT3hZI80b6vTPwI9c73K").
               build();

        loading.setVisibility(View.VISIBLE);
       client.newCall(request).enqueue(new Callback() {
           @Override
           public void onFailure(Call call, IOException e) {
               showErrorMessage();
               e.printStackTrace();
           }

           @Override
           public void onResponse(Call call, Response response) throws IOException {
                if(!response.isSuccessful()){
                    showErrorMessage();
                    throw new IOException("Unexpected code " + response.toString());
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loading.setVisibility(View.INVISIBLE);
                        }
                    });
                    String json = response.body().string();
                    String[] splitted = json.split("multimedia");

                    for(int i = 0; i  < splitted.length; i++){
                        Log.i("arr", splitted[i]);
                    }
                    Log.i("adapter", json);
                    gsonBuilder.registerTypeAdapter(Multimedia[].class, new MultimediaArrayAdapter());
                    gson = gsonBuilder.create();
                    container = gson.fromJson(json, Container.class);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startRecyclerView();
                        }
                    });
                }
           }
       });
    }

    public void showErrorMessage(){
        //loading.setVisibility(View.INVISIBLE);
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                toast = Toast.makeText(getBaseContext(), errorMessage,Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    public void startRecyclerView(){
        articlesRecyclerview = (RecyclerView) findViewById(R.id.articles_rv);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        articlesRecyclerview.setLayoutManager(manager);
        articlesRecyclerview.setHasFixedSize(true);
        adapter = new ArticleAdapter(number_of_items, container.results,findViewById(R.id.articles_rv),this);
        articlesRecyclerview.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        Log.i("menu", "llego");
        //CastButtonFactory.setUpMediaRouteButton(getApplicationContext(),
          //      menu,
            //    R.id.media_route_menu_item);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedId = item.getItemId();

        if(selectedId == R.id.log_out){
            logOut();
        }

        return super.onOptionsItemSelected(item);
    }

    public void logOut(){
        if(userId != null){
            googleClient.revokeAccess().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    goToLogIn();
                }
            });
        }else{
            goToLogIn();
        }
    }

    public void goToLogIn(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onArticleClickListener(Article clickedArticle) {
        Intent intent = new Intent(this, ArticleDetail.class);
        String json = gson.toJson(clickedArticle);
        intent.putExtra("JSON", json);
        //Log.i("success", clickedArticle.multimedia[clickedArticle.multimedia.length-1].height + "");
        //Log.i("success", clickedArticle.multimedia[clickedArticle.multimedia.length-1].width + "");

        if(clickedArticle.multimedia.length > 0){
            intent.putExtra("URL", clickedArticle.multimedia[clickedArticle.multimedia.length-1].url);
        }
        startActivity(intent);
    }
}
