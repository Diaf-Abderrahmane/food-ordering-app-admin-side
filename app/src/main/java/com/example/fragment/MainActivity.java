package com.example.fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        replace(new Menu());

        LinearLayout qr=findViewById(R.id.nQR_generator);
        qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replace(new Qr_Generator());
                findViewById(R.id.SettingImg).setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.eblack)));
                findViewById(R.id.QrCodeImg).setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorPrimaryDark)));
                findViewById(R.id.MenuImg).setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.eblack)));
                findViewById(R.id.ReviewsImg).setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.eblack)));
            }
        });
        LinearLayout Setting=findViewById(R.id.nSettings);
        Setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replace(new Settings());
                findViewById(R.id.ReviewsImg).setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.eblack)));
                findViewById(R.id.SettingImg).setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorPrimaryDark)));
                findViewById(R.id.QrCodeImg).setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.eblack)));
                findViewById(R.id.MenuImg).setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.eblack)));

            }
        });
        LinearLayout Menu=findViewById(R.id.nMenu);
        Menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replace(new Menu());
                findViewById(R.id.SettingImg).setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.eblack)));
                findViewById(R.id.QrCodeImg).setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.eblack)));
                findViewById(R.id.ReviewsImg).setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.eblack)));
                findViewById(R.id.MenuImg).setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorPrimaryDark)));
            }
        });
       /* LinearLayout reviws=findViewById(R.id.nReviews);
        reviws.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replace(new Reviews());
                findViewById(R.id.SettingImg).setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.eblack)));
                findViewById(R.id.QrCodeImg).setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.eblack)));
                findViewById(R.id.MenuImg).setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.eblack)));
                findViewById(R.id.ReviewsImg).setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorPrimaryDark)));
            }
        });*/


    }
    private void replace(Fragment Fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLO, Fragment).commit();

    }

}