package com.example.funnypetfinal;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {
    TextView backFromForgotPassword;
    Button btnResetPassword;
    EditText emailForgotPassword;
    FirebaseAuth mAuth;
    String email;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        backFromForgotPassword = findViewById(R.id.backFromForgotPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        emailForgotPassword = findViewById(R.id.inputEmail);

        mAuth = FirebaseAuth.getInstance();

        backFromForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPassword.this, Login.class);
                startActivity(intent);
            }
        });

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailForgotPassword.getText().toString().trim();
                if (!TextUtils.isEmpty(email)){
                    resetPassword();
                }else {
                    emailForgotPassword.setError("Email can't be empty");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ForgotPassword.this, Login.class);
        startActivity(intent);
        finish();
    }

    private void resetPassword() {
        // btnResetPassword.setVisibility(View.INVISIBLE); // Убираем эту строку, чтобы кнопка не становилась невидимой

        mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(ForgotPassword.this, "Reset Password link has been sent to your Email", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ForgotPassword.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
