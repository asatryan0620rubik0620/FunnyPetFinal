package com.example.funnypetfinal;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    TextView backFromRegister;
    EditText mPersonName, mEmail, mPassword, mConfirmPassword;
    Button mRegisterBtn;
    FirebaseAuth mAuth;

    CollectionReference usersCollection;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //findViewById
        backFromRegister = findViewById(R.id.backFromRegister);
        mPersonName = findViewById(R.id.inputPersonName);
        mEmail = findViewById(R.id.inputEmail);
        mPassword = findViewById(R.id.inputPassword);
        mConfirmPassword = findViewById(R.id.inputConfirmPassword);
        mRegisterBtn = findViewById(R.id.btnRegister);
        //----------------------------------------------------------

        //Firebase___________________________________________________________________________________
        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        usersCollection = db.collection("users");
        //---------------------------------

        backFromRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }
        });


        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateDataAndDoRegister();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Register.this, Login.class);
        startActivity(intent);
        finish();
    }

    private void validateDataAndDoRegister() {
        String personName = mPersonName.getText().toString().trim();
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        String confirmPassword = mConfirmPassword.getText().toString().trim();

        if (personName.isEmpty()) {
            mPersonName.setError("Enter Username");
            mPersonName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            mEmail.setError("Enter Email");
            mEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            mPassword.setError("Enter Password");
            mPassword.requestFocus();
            return;
        }

        if (password.length() < 8) {
            mPassword.setError("Password must be greater than 7 symbols");
            mPassword.requestFocus();
            return;
        }

        if (confirmPassword.isEmpty()) {
            mConfirmPassword.setError("Enter Confirm Password");
            mConfirmPassword.requestFocus();
            return;
        }

        if (confirmPassword.length() < 7) {
            mConfirmPassword.setError("Password must be greater than 7 symbols");
            mConfirmPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            mPassword.setError("Passwords not matched");
            mPassword.requestFocus();
            mConfirmPassword.setError("Passwords not matched");
            mConfirmPassword.requestFocus();
            mPassword.setText("");
            mConfirmPassword.setText("");
            return;
        }

        doRegister(email, password);
    }

    //registration___________________________________________________________________________________________________________
    private void doRegister(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        String userId = user.getUid();
                        String personName = mPersonName.getText().toString().trim();

                        // Create a HashMap to store user data
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("name", personName);

                        // Add user data to Firestore
                        usersCollection.document(userId)
                                .set(userData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                        sendVerificationEmail();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "Error registering user: " + e.getMessage());
                                        mRegisterBtn.setEnabled(true);
                                        Toast.makeText(Register.this, "Oops! Failed to register user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                } else {
                    mRegisterBtn.setEnabled(true);
                    Toast.makeText(Register.this, "Oops! Failed to register user", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof FirebaseAuthUserCollisionException) { // Email already registered
                    mRegisterBtn.setEnabled(true);
                    mEmail.setError("Email Already Registered");
                    mEmail.requestFocus();
                } else {
                    mRegisterBtn.setEnabled(true);
                    Toast.makeText(Register.this, "Oops! something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //-------------------------------------------------------------------------------------------------------------------------------------


    //sending verification
    private void sendVerificationEmail() {
        if (mAuth.getCurrentUser() != null) {
            FirebaseUser user = mAuth.getCurrentUser();
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User profile updated.");
                            }
                        }
                    });

            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        mRegisterBtn.setEnabled(true);
                        Toast.makeText(Register.this, "Email has been sent to your email address", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(Register.this, Login.class);
                        startActivity(intent);
                        finish();
                    } else {
                        mRegisterBtn.setEnabled(true);
                        Toast.makeText(Register.this, "Oops! Failed to send verification email", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
}
