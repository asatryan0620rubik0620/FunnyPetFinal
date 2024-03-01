package com.example.funnypetfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.transition.Transition;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    TextView mName, mEmail;
    Button btnSignOut;
    private BottomNavigationView bottomNavigationView;

    private Fragment petsFragment;
    private Fragment mapFragment;
    private Fragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //findViewById___________________________________________________________________________________
        mName = findViewById(R.id.inputName);
        mEmail = findViewById(R.id.inputEmail);
        btnSignOut = findViewById(R.id.btnSignOut);
        //////////////////////////////////////////////////////////////////////////////////////////////////



        //fragments_____________________________________________________________________________________
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        petsFragment = new PetsFragment();
        mapFragment = new MapFragment();
        profileFragment = new ProfileFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, petsFragment)
                .commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.pets) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, petsFragment)
                            .commit();
                    return true;
                } else if (item.getItemId() == R.id.map) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, mapFragment)
                            .commit();
                    return true;
                } else if (item.getItemId() == R.id.profile) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, profileFragment)
                            .commit();
                    return true;
                }
                return false;
            }
        });

        //////////////////////////////////////////////////////////////////////////////////////////////////

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso); // Инициализация GoogleSignInClient

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userName = currentUser.getDisplayName();
            String userEmail = currentUser.getEmail();
            mName.setText(userName);
            mEmail.setText(userEmail);
        }

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }


    private void signOut() {
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
    }

//    public void logout(View view) {
//        FirebaseAuth.getInstance().signOut();//logout
//        startActivity(new Intent(getApplicationContext(), Login.class));
//        finish();
//    }
}