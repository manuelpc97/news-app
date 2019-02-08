package com.example.android.news_app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.ChromecastYouTubePlayerContext;
import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.io.infrastructure.ChromecastConnectionListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.YouTubePlayerInitListener;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static int sum;
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleClient;
    SignInButton signInButton;
    GoogleSignInAccount account;

    Button createAccountButton;
    Button logInButton;
    EditText userTextView;
    EditText passwordTextView;

    DatabaseSupport support;
    OkHttpClient client;
    Gson gson;
    User user;

    String userName;
    String userEmail;
    String userId;

    int googleRequestCode = 1;
    Toast toast;

    ProgressBar loading;

    public static CastSession session;
    public static SessionManager sessionManager;

    public static SimpleChromecastConnectionListener simpleListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        account = GoogleSignIn.getLastSignedInAccount(this);

        if(account != null){
            userName = account.getDisplayName();
            userEmail = account.getEmail();
            userId = account.getId();
            logIn();
        }

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleClient = GoogleSignIn.getClient(this,googleSignInOptions);

        signInButton = (SignInButton) findViewById(R.id.sign_in_google_button);
        signInButton.setOnClickListener(this);

        createAccountButton = (Button)findViewById(R.id.create_account_button);
        createAccountButton.setOnClickListener(this);

        logInButton = (Button) findViewById(R.id.sign_in_button);
        logInButton.setOnClickListener(this);

        userTextView = (EditText) findViewById(R.id.user_textview);
        passwordTextView = (EditText) findViewById(R.id.password_textview);

        loading = findViewById(R.id.pb4);

        support = new DatabaseSupport();
        client = new OkHttpClient();
        gson = new Gson();
        simpleListener = new SimpleChromecastConnectionListener("");
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id == R.id.sign_in_google_button){
            signIn();
        }else if(id == R.id.create_account_button){
            createAccount();
        }else if(id == R.id.sign_in_button){
            String name = userTextView.getText().toString();
            String password = passwordTextView.getText().toString();

            verifyCredentials(name, password);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == googleRequestCode){
            Task <GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task){
        try{
            account = task.getResult(ApiException.class);
            userName = account.getDisplayName();
            userEmail = account.getEmail();
            userId = account.getId();
            logIn();
        }catch(ApiException e){
            e.printStackTrace();
        }
    }

    private void signIn(){
        Intent signInIntent = googleClient.getSignInIntent();
        startActivityForResult(signInIntent, googleRequestCode);
    }

    private void logIn(){
        Intent intent = new Intent(this,Home.class);
        intent.putExtra("user", userName);
        intent.putExtra("email", userEmail);

        if(userId != null){
            intent.putExtra("id", userId);
        }

        if(googleClient != null){

        }
        startActivity(intent);
    }

    private void createAccount(){
        Intent intent = new Intent(this, createUser.class);
        startActivity(intent);
    }

    private void verifyCredentials(final String user, final String password){
        Request request = support.get();

        loading.setVisibility(View.VISIBLE);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        showMessage("ERROR, CHECK NETWORK CONNECTION");
                    }
                });
                showMessage("ERROR, CHECK NETWORK CONNECTION");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(!response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showMessage("ERROR, CHECK NETWORK CONNECTION");
                        }
                    });
                    throw new IOException("Unexpected code: " + response);
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loading.setVisibility(View.INVISIBLE);
                        }
                    });
                    boolean isValiduser = isValidUser(response.body().string(), user, password);

                    if(isValiduser){
                        userName = user;
                        userId = null;
                        logIn();
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showMessage("Usuario Incorrecto");
                            }
                        });
                    }

                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(sessionManager == null){
                Log.i("playing", "resumeNULL");
                sessionManager = CastContext.getSharedInstance(this).getSessionManager();
        }
        session = sessionManager.getCurrentCastSession();
    }

    private void showMessage(final String message){
        //loading.setVisibility(View.INVISIBLE);
        new Handler(getMainLooper()).post(new Runnable(){
            @Override
            public void run() {
                toast = Toast.makeText(getApplicationContext(), message,Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    private boolean isValidUser(String json, String name, String password){
       User[] users = gson.fromJson(json, User[].class);

       for(int i = 0;i < users.length; i++){
           if(users[i].getUser().equals(name) && users[i].getPassword().equals(password)){
               userEmail = users[i].getEmail();
               return true;
           }
       }
      return false;
    }
}
