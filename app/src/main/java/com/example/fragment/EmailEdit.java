package com.example.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class EmailEdit extends AppCompatActivity {
    Button edit;
    EditText newEmail,confiremEmail;
    FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();
    FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_edit);
        confiremEmail = (EditText) findViewById(R.id.CEmail);
        newEmail = (EditText) findViewById(R.id.Email);
        edit = (Button) findViewById(R.id.Edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                final String NewEmail = newEmail.getText().toString();
                final String ConfirmNewEmail = confiremEmail.getText().toString();
                if (isValidData(EmailEdit.this, NewEmail, ConfirmNewEmail)) {
                        firebaseUser.updateEmail(NewEmail)
                                .addOnCompleteListener(EmailEdit.this, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(EmailEdit.this, new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(EmailEdit.this, "please check your email for verification before go out", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } else {
                                            Toast.makeText(EmailEdit.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    } else {
                        confiremEmail.setTextColor(getColor(R.color.colorRed));
                }
            }
        });
    }
    static boolean isValidData(Activity thisActivity,String NewEmail,String ConfirmNewEmail){
        Boolean isValidNewEmail,isValidConfirmNewEmail;
        ArrayList<String> errors=new ArrayList<>();

        isValidNewEmail= Patterns.EMAIL_ADDRESS.matcher(NewEmail).matches();
        isValidConfirmNewEmail=ConfirmNewEmail.length()>=6;


        if (!isValidNewEmail) {errors.add("أدخل بريد الكتروني صالح");}
        if (!isValidConfirmNewEmail) {errors.add("أدخل بريد الكتروني صالح"); }
        if(!(isValidNewEmail && isValidConfirmNewEmail)){show(thisActivity,errors);}

        return isValidNewEmail && isValidConfirmNewEmail ;
    }
    static void show(Activity thisActivity, ArrayList<String> errors){
        AlertDialog.Builder adb = new AlertDialog.Builder(thisActivity);
        LinearLayout L = new LinearLayout(thisActivity);
        L.setOrientation(LinearLayout.VERTICAL);

        for (int i=0;i<errors.size();i++) {
            TextView Tv = new TextView(thisActivity);
            Tv.setText(errors.get(i));
            L.addView(Tv);
        }
        adb.setView(L);
        Dialog d = adb.create();
        d.show();
    }
}