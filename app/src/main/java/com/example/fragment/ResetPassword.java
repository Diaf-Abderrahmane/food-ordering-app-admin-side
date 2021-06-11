package com.example.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Pattern;


public class ResetPassword extends Fragment {
    private TextView resetPassword,resetInst;
    private Button resetPasswordBtn;
    private TextInputLayout email;
    FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.reset_password, container, false);
        resetPassword = view.findViewById(R.id.resetPassword);
        resetInst = view.findViewById(R.id.resetInst);
        email = view.findViewById(R.id.email);
        resetPasswordBtn = view.findViewById(R.id.resetPasswordBtn);
        auth = FirebaseAuth.getInstance();
        resetPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });




        return view;
    }
    private void resetPassword(){
        String emailInput = email.getEditText().getText().toString().trim();
        if (emailInput.isEmpty()) {
            email.setError("Email required");
            email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            email.setError("Please enter a valid email");
            email.requestFocus();
        }
        auth.sendPasswordResetEmail(emailInput).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Check your email to reset your password", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Try again! something went wrong.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

}
