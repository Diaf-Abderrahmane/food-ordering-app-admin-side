package com.example.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
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
import androidx.appcompat.app.AppCompatActivity;

public class PasswordEdit extends AppCompatActivity {
    Button edit;
    EditText newPassword,confiremPassword;
    FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();
    FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_edit);
        confiremPassword = (EditText) findViewById(R.id.CPassword);
        newPassword = (EditText) findViewById(R.id.Password);
        edit = (Button) findViewById(R.id.Edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 String NewPassword = newPassword.getText().toString();
                 String ConfiremPassword = confiremPassword.getText().toString();
                if (isValidData(PasswordEdit.this, NewPassword, ConfiremPassword)) {
                        firebaseUser.updatePassword(NewPassword)
                                .addOnCompleteListener(PasswordEdit.this,new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(PasswordEdit.this, "Password Updated", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(PasswordEdit.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                                        }
                                    }
                                });
            }
            }

    });
    }
        static boolean isValidData(Activity thisActivity,String NewPassword,String ConfiremPassword){
            Boolean isValidNewPassword,isValidConfiremPassword;
            ArrayList<String> errors=new ArrayList<>();

            isValidNewPassword= NewPassword.length()>=6;
            isValidConfiremPassword=(ConfiremPassword.equals(NewPassword));


            if (!isValidNewPassword) {errors.add("The Password is very Short");}
            if (!isValidConfiremPassword) {errors.add("Passwords is not equal"); }
            if(!(isValidNewPassword && isValidConfiremPassword)){show(thisActivity,errors);}

            return isValidNewPassword && isValidConfiremPassword ;
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
