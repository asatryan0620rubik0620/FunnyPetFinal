package com.example.funnypetfinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.funnypetfinal.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button btnClinics, btnParks, btnShops, btnEstablishment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnClinics = view.findViewById(R.id.btnClinics);
        btnParks = view.findViewById(R.id.btnParks);
        btnEstablishment = view.findViewById(R.id.btnEstablishment);
        btnShops = view.findViewById(R.id.btnShops);

        btnClinics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMarker(new LatLng(40.1792, 44.4991), "Yerevan");
            }
        });

        btnParks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMarker(new LatLng(55.7558, 37.6176), "Moscow");
            }
        });

        btnEstablishment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMarker(new LatLng(48.8566, 2.3522), "Paris");
            }
        });

        btnShops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMarker(new LatLng(51.5074, -0.1278), "London");
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void addMarker(LatLng position, String title) {
        if (mMap != null) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(position).title(title));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 10));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}
