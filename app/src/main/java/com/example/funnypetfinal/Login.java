package com.example.funnypetfinal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    EditText mEmail, mPassword;
    Button mLoginBtn;
    TextView createNewAccount, forgotPassword;
    ImageView btnGoogle;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;


    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //findViewById___________________________________________________________________________________
        mEmail = findViewById(R.id.inputEmail);
        mPassword = findViewById(R.id.inputConformPassword);
        mLoginBtn = findViewById(R.id.btnLogin);
        createNewAccount = findViewById(R.id.createNewAccount);
        forgotPassword = findViewById(R.id.forgotPassword);
        btnGoogle = findViewById(R.id.btnGoogle);
        //////////////////////////////////////////////////////////////////////////////////////////////////


        //Firebase________________________________________________________________________________________
        mAuth = FirebaseAuth.getInstance();
        //////////////////////////////////////////////////////////////////////////////////////////////////


        //redirection to Register email&password______________________________________________________________________
        createNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
                finish();
            }
        });
        //////////////////////////////////////////////////////////////////////////////////////////////////


        //redirection to ForgotPassword email&password______________________________________________________________________
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, ForgotPassword.class);
                startActivity(intent);
                finish();
            }
        });
        //////////////////////////////////////////////////////////////////////////////////////////////////


        //email&password login____________________________________________________________________________
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

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

                loginUser(email, password);
            }
        });
        //////////////////////////////////////////////////////////////////////////////////////////////////


        //sign in with Google_______________________________________________________________________________
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount accountGoogle = GoogleSignIn.getLastSignedInAccount(this);
        if (accountGoogle != null){
            navigateToMainActivity();
        }

        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        //////////////////////////////////////////////////////////////////////////////////////////////////
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }



    private void signIn() {
        Intent signInInten = gsc.getSignInIntent();
        startActivityForResult(signInInten, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    navigateToMainActivity();
                }
            } catch (ApiException e) {
                e.printStackTrace();
                Log.e("GoogleSignIn", "signInResult:failed code=" + e.getStatusCode());
                if (e.getStatusCode() == GoogleSignInStatusCodes.SIGN_IN_CANCELLED) {
                    Toast.makeText(this, "Sign-in cancelled", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }



    private void navigateToMainActivity() {
        Intent intent = new Intent(Login.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null && user.isEmailVerified()) {
                                Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(Login.this, "Please verify your email", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Login.this, "Login failed. Please check your credentials", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
