package com.example.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;

public class Splashscreen extends AppCompatActivity {
    public static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!(FirebaseAuth.getInstance().getCurrentUser() == null)) {
                    startActivity(new Intent(Splashscreen.this, MainActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(Splashscreen.this,Login.class));
                    finish();
                }
            }
        },SPLASH_TIME_OUT);
    }
}