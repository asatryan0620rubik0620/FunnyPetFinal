package com.example.funnypetfinal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.funnypetfinal.R;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        // Найдите TextView для имени профиля
        TextView profileNameTextView = rootView.findViewById(R.id.profileName);

        // Найдите TextView для адреса электронной почты профиля
        TextView profileEmailTextView = rootView.findViewById(R.id.profileEmail);

        // Установите текст для имени профиля
        profileNameTextView.setText("Leo DiCaprio");

        // Установите текст для адреса электронной почты профиля
        profileEmailTextView.setText("leo777@gmail.com");

        return rootView;
    }
}
