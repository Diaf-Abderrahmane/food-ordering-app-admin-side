package com.example.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;

public class Splashscreen extends AppCompatActivity {
    public static int SPLASH_TIME_OUT = 3000;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        progressBar = findViewById(R.id.progressBar);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        }, 1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!(FirebaseAuth.getInstance().getCurrentUser() == null)) {
                    startActivity(new Intent(Splashscreen.this, MainActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(Splashscreen.this, Login.class));
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }
}