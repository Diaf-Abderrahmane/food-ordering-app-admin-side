package com.example.fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;

public class Admin {
    private String Id;
    private String Email;
    private String UserName;

    public Admin() {
    }

    public Admin(String email, String userName) {
        Email = email;
        UserName = userName;
    }

    public Admin(String id, String email, String userName) {
        Id = id;
        Email = email;
        UserName = userName;
    }

    public String getId() {
        return Id;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public static void AddAdmin(Admin admin, String password) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(admin.getEmail(), password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //task.getResult().getUser().sendEmailVerification();
                    String UId = task.getResult().getUser().getUid().toString();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Admins").child(UId);
                    ref.setValue(admin);
                }
            }
        });
    }

    public static void EditAdmin(Admin admin, String password) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        user.updateEmail(admin.getEmail());
        user.updatePassword(password);
    }
}
