package com.example.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {
    private TextView authen,login,desc;
    private TextInputLayout email, password;
    private Button loginBtn,toRegister,forgotPassword;
    private FirebaseAuth fAuth;
    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String emailInput = email.getEditText().getText().toString().trim();
            String passwordInput = password.getEditText().getText().toString().trim();
            loginBtn.setEnabled(!emailInput.isEmpty() && !passwordInput.isEmpty());
//            if (loginBtn.isEnabled())  {
//                loginBtn.setBackgroundColor(Color.BLACK);
//                loginBtn.setTextColor(Color.WHITE);
//            } else {
//                loginBtn.setBackgroundColor(Color.GRAY);
//                loginBtn.setTextColor(Color.BLACK);
//            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getActionBar().hide();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        fAuth = FirebaseAuth.getInstance();
        loginBtn = findViewById(R.id.loginBtn);
        toRegister = findViewById(R.id.toRegister);
        forgotPassword = findViewById(R.id.forgotPassword);
        AwesomeValidation awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);


        awesomeValidation.addValidation(this, R.id.email, Patterns.EMAIL_ADDRESS, R.string.Linvalid_email);
        awesomeValidation.addValidation(this, R.id.password, ".{6,}", R.string.invalid_password);

        email.getEditText().addTextChangedListener(loginTextWatcher);
        password.getEditText().addTextChangedListener(loginTextWatcher);


        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,ResetPassword1.class));

            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (awesomeValidation.validate()) {
                    String vemail = email.getEditText().getText().toString();
                    String vpassword = password.getEditText().getText().toString();


                    fAuth.signInWithEmailAndPassword(vemail, vpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Login.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Login.this, MainActivity.class));
                            } else {
                                Toast.makeText(Login.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                //startActivity(new Intent(LOG.this,REG.class));
                            }
                        }
                    });
                } else {
                    Toast.makeText(Login.this, "Validation failed", Toast.LENGTH_SHORT).show();
                }
            }
        });


        toRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Sign_up.class));
                finish();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, ResetPassword1.class));
            }
        });
    }
}