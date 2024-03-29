package com.example.fragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        int i = intent.getIntExtra("key", 0);
        if (i == 2) {
            replace(new Qr_Generator());
            findViewById(R.id.ReviewsImg).setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.black)));
            findViewById(R.id.SettingImg).setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.black)));
            findViewById(R.id.QrCodeImg).setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorPrimaryDark)));
            findViewById(R.id.MenuImg).setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.black)));
            Main();

        } else if (i == 4) {
            replace(new Settings());
            findViewById(R.id.ReviewsImg).setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.black)));
            findViewById(R.id.SettingImg).setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorPrimaryDark)));
            findViewById(R.id.QrCodeImg).setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.black)));
            findViewById(R.id.MenuImg).setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.black)));
            Main();

        } else {
            replace(new Menu());
            Main();
        }


    }

    private void replace(Fragment Fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLO, Fragment).commit();

    }

    public void Main() {
        LinearLayout qr = findViewById(R.id.nQR_generator);
        qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replace(new Qr_Generator());
                findViewById(R.id.SettingImg).setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.black)));
                findViewById(R.id.QrCodeImg).setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorPrimaryDark)));
                findViewById(R.id.MenuImg).setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.black)));
                findViewById(R.id.ReviewsImg).setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.black)));
            }
        });
        LinearLayout Setting = findViewById(R.id.nSettings);
        Setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replace(new Settings());
                findViewById(R.id.ReviewsImg).setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.black)));
                findViewById(R.id.SettingImg).setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorPrimaryDark)));
                findViewById(R.id.QrCodeImg).setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.black)));
                findViewById(R.id.MenuImg).setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.black)));

            }
        });
        LinearLayout Menu = findViewById(R.id.nMenu);
        Menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replace(new Menu());
                findViewById(R.id.SettingImg).setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.black)));
                findViewById(R.id.QrCodeImg).setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.black)));
                findViewById(R.id.ReviewsImg).setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.black)));
                findViewById(R.id.MenuImg).setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorPrimaryDark)));
            }
        });
        LinearLayout reveiws = findViewById(R.id.nReviews);
        reveiws.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replace(new Reviews());
                findViewById(R.id.SettingImg).setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.black)));
                findViewById(R.id.QrCodeImg).setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.black)));
                findViewById(R.id.MenuImg).setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.black)));
                findViewById(R.id.ReviewsImg).setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorPrimaryDark)));
            }
        });
    }

}