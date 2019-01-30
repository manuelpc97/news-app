package com.example.android.news_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class createUser extends AppCompatActivity implements View.OnClickListener {

    EditText userTextView;
    EditText emailTextView;
    EditText passwordTextView;
    Button saveUserAccount;

    User user;
    Gson gson;

    DatabaseSupport dbSupport;
    OkHttpClient client;

    String userName;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        userTextView = (EditText) findViewById(R.id.create_user_textview);
        emailTextView = (EditText) findViewById(R.id.create_email_textview);
        passwordTextView = (EditText) findViewById(R.id.create_password_textview);

        saveUserAccount = (Button) findViewById(R.id.save_user_button);
        saveUserAccount.setOnClickListener(this);

        gson = new Gson();
        dbSupport = new DatabaseSupport();
        client = new OkHttpClient();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id == R.id.save_user_button){
            saveUser();
        }
    }

    public void saveUser(){
        final String name = userTextView.getText().toString();
        final String email = emailTextView.getText().toString();
        final String password = passwordTextView.getText().toString();

        user = new User(email,password, name);

        String json = gson.toJson(user);

        Request request = dbSupport.post(json);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(!response.isSuccessful()){
                    throw new IOException("Unexpected code: " + response.toString());
                }else{
                    Log.i("success", response.body().string());
                    userName = name;
                    userEmail = email;
                    logIn();
                }
            }
        });
    }

    public void logIn(){
        Intent intent = new Intent(this, Home.class);
        intent.putExtra("user", userName);
        intent.putExtra("email", userEmail);
        startActivity(intent);
    }
}
