package com.example.funnypetfinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddPetActivity extends AppCompatActivity {

    private TextInputEditText petName, petColor, petWeight, petHeight;
    private MaterialButton btnSave;
    private ImageView petAvatar, addPetAvatar;
    private TextView petDOB;
    private FirebaseFirestore db;
    private AutoCompleteTextView petSearch, petGenderSearch;
    private RadioGroup petTypeRadioGroup;
    private ArrayAdapter<String> catBreedsAdapter, dogBreedsAdapter, petGenderAdapter;

    Uri imageUri;
    FirebaseStorage storage;
    StorageReference storageReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);

        // Инициализация элементов управления
        petAvatar = findViewById(R.id.petAvatar);
        addPetAvatar = findViewById(R.id.addPetAvatar);
        petName = findViewById(R.id.petName);
        petGenderSearch = findViewById(R.id.petGender);
        petColor = findViewById(R.id.petColor);
        btnSave = findViewById(R.id.btnSave);
        petDOB = findViewById(R.id.petDOB);
        petSearch = findViewById(R.id.petSearch);
        petWeight = findViewById(R.id.petWeight);
        petHeight = findViewById(R.id.petHeight);
        petTypeRadioGroup = findViewById(R.id.petTypeRadioGroup);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        addPetAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent avatarIntent = new Intent(Intent.ACTION_PICK);
                avatarIntent.setType("image/*");
                startActivityForResult(avatarIntent, 1);
            }
        });

        // Настройка DatePicker
        petDOB.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select date")
                    .build();

            datePicker.addOnPositiveButtonClickListener(selection -> {
                String selectedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(selection));
                petDOB.setText(selectedDate);
            });

            datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
        });

        // Сохранение информации о питомце
        btnSave.setOnClickListener(v -> savePetInfo());

        // Массивы пород для каждого типа животных
        String[] catBreeds = getResources().getStringArray(R.array.cat_breeds_array);
        String[] dogBreeds = getResources().getStringArray(R.array.dog_breeds_array);
        String[] gender = getResources().getStringArray(R.array.gender_array);

        // Адаптеры для AutoCompleteTextView
        catBreedsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, catBreeds);
        dogBreedsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, dogBreeds);
        petGenderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, gender);

        // Слушатель для RadioGroup
        petTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            petSearch.setText(""); // Очистка текста при смене выбора
            if (checkedId == R.id.radioButtonCat) {
                petSearch.setAdapter(catBreedsAdapter);
            } else if (checkedId == R.id.radioButtonDog) {
                petSearch.setAdapter(dogBreedsAdapter);
            }
        });
        petGenderSearch.setAdapter(petGenderAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            petAvatar.setImageURI(imageUri);
        }
    }

    private void savePetInfo() {
        String name = petName.getText().toString().trim();
        String dob = petDOB.getText().toString().trim();
        String gender = petGenderSearch.getText().toString().trim();
        String color = petColor.getText().toString().trim();
        String weight = petWeight.getText().toString().trim();
        String height = petHeight.getText().toString().trim();
        String breed = petSearch.getText().toString().trim();

        // Obtain the selected pet type
        int selectedPetTypeId = petTypeRadioGroup.getCheckedRadioButtonId();
        String petType;
        if (selectedPetTypeId == R.id.radioButtonCat) {
            petType = "Cat";
        } else if (selectedPetTypeId == R.id.radioButtonDog) {
            petType = "Dog";
        } else {
            petType = "";
        }

        if (!name.isEmpty() && !dob.isEmpty() && !gender.isEmpty() && !color.isEmpty() && !petType.isEmpty() && !breed.isEmpty() && !weight.isEmpty() && !height.isEmpty()) {
            if (imageUri != null) {
                // Get a reference to store the image in Firebase Storage
                StorageReference imageRef = storageReference.child("pet_avatars/" + name + "_avatar");

                // Upload the image to Firebase Storage
                imageRef.putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> {
                            // Image uploaded successfully, now get the download URL
                            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                // Image uploaded and URL retrieved successfully, now save pet info to Firestore
                                Map<String, Object> pet = new HashMap<>();
                                pet.put("PetAvatar", uri.toString());
                                pet.put("Name", name);
                                pet.put("DateOfBirth", dob);
                                pet.put("Gender", gender);
                                pet.put("Color", color);
                                pet.put("Type", petType);
                                pet.put("Breed", breed);
                                pet.put("Weight", weight);
                                pet.put("Height", height);

                                db.collection("pets").add(pet)
                                        .addOnSuccessListener(documentReference -> {
                                            Toast.makeText(AddPetActivity.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("Firestore Error", e.getMessage());
                                            Toast.makeText(AddPetActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            });
                        })
                        .addOnFailureListener(e -> {
                            // Handle unsuccessful image upload
                            Log.e("Firebase Storage Error", e.getMessage());
                            Toast.makeText(AddPetActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });

                // Redirect to MainActivity
                Intent intent = new Intent(AddPetActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            // If any required field is empty
            Toast.makeText(AddPetActivity.this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
        }
    }


}

