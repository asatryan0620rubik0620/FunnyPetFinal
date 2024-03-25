package com.example.funnypetfinal;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class ProfileFragment extends Fragment {

    TextView profileName, profileEmail;
    Button signOut;
    ImageView editProfile, profileAvatar;
    FirebaseUser user;
    SharedPreferences sharedPreferences;
    Uri imageUri;
    FirebaseStorage storage;
    StorageReference storageReference;
    private static final String TAG = "ProfileFragment";

    @SuppressLint("WrongViewCast")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize SharedPreferences
        sharedPreferences = getActivity().getSharedPreferences("SharedPreferencesCash", Context.MODE_PRIVATE);

        // findViewById
        profileName = rootView.findViewById(R.id.profileName);
        profileEmail = rootView.findViewById(R.id.profileEmail);
        signOut = rootView.findViewById(R.id.singOut);
        editProfile = rootView.findViewById(R.id.editProfile);
        profileAvatar = rootView.findViewById(R.id.profileAvatar);

        // Firebase
        user = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        if (user != null) {
            // Set email
            String userEmail = user.getEmail();
            profileEmail.setText(userEmail);

            // Set name and profile image
            updateUserInfo();

            // Sign out
            signOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(requireActivity(), Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    getActivity().finish();
                }
            });
        } else {
            Log.e(TAG, "User is not authenticated");
        }

        // Dialog sheet for editing the profile________________________________________________________________
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog  = new Dialog(getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.bottom_sheet_layout);
                LinearLayout profileEditingItem1 = dialog.findViewById(R.id.profileEditingItem1);
                LinearLayout profileEditingItem2 = dialog.findViewById(R.id.profileEditingItem2);
                LinearLayout profileEditingItem3 = dialog.findViewById(R.id.profileEditingItem3);
                LinearLayout profileEditingItem4 = dialog.findViewById(R.id.profileEditingItem4);
                LinearLayout profileEditingItem5 = dialog.findViewById(R.id.profileEditingItem5);

                profileEditingItem1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Intent avatarIntent = new Intent(Intent.ACTION_PICK);
                        avatarIntent.setType("image/*");
                        startActivityForResult(avatarIntent, 1);
                    }
                });
                profileEditingItem2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Toast.makeText(getContext(), "Edit is Clicked", Toast.LENGTH_SHORT).show();
                    }
                });
                profileEditingItem3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Toast.makeText(getContext(), "Edit is Clicked", Toast.LENGTH_SHORT).show();
                    }
                });
                profileEditingItem4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Toast.makeText(getContext(), "Edit is Clicked", Toast.LENGTH_SHORT).show();
                    }
                });
                profileEditingItem5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Toast.makeText(getContext(), "Edit is Clicked", Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable((new ColorDrawable(Color.TRANSPARENT)));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.getWindow().setGravity(Gravity.BOTTOM);
            }
        });
        //________________________________________________________________________________________________________________________

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileAvatar.setImageURI(imageUri);

            // Получаем ссылку на Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Создаем документ для текущего пользователя
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DocumentReference userDocRef = db.collection("users").document(userId);

            // Сохраняем URL изображения в поле "profileImage" в Firestore
            userDocRef.update("profileImage", imageUri.toString())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Успешно сохранено в Firestore
                            Toast.makeText(getContext(), "Изображение сохранено в Firestore", Toast.LENGTH_SHORT).show();

                            // Теперь сохраняем изображение в Firebase Storage
                            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                            StorageReference imageRef = storageRef.child("profile_images/image.jpg");

                            imageRef.putFile(imageUri);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Ошибка при сохранении в Firestore
                            Toast.makeText(getContext(), "Не удалось сохранить изображение в Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }




    private void saveImageToFirestore(String imageUrl) {
        // Проверяем, что пользователь не равен null
        if (user != null) {
            // Сохраняем ссылку на изображение в Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String userId = user.getUid();
            DocumentReference userDocRef = db.collection("users").document(userId);
            userDocRef.update("profileImage", imageUrl)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Успешно сохранено в Firestore
                            Toast.makeText(getContext(), "Изображение сохранено в Firestore", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Ошибка при сохранении в Firestore
                            Toast.makeText(getContext(), "Не удалось сохранить изображение в Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Log.e(TAG, "User is null, cannot save image to Firestore");
        }
    }


    private void updateUserInfo() {
        // Update user name and profile image
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String userName = documentSnapshot.getString("name");
                            profileName.setText(userName);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("userName", userName);
                            editor.apply();

                            // Load profile image from Firestore
                            String imageUrl = documentSnapshot.getString("profileImage");
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                Glide.with(this)
                                        .load(imageUrl)
                                        .into(profileAvatar);
                            }
                        } else {
                            Log.e(TAG, "User document does not exist");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error fetching user document: " + e.getMessage());
                    });
        } else {
            Log.e(TAG, "User is not authenticated");
        }
    }
}
