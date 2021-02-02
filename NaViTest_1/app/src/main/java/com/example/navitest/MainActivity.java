package com.example.navitest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.amap.api.maps.MapView;

public class MainActivity extends AppCompatActivity {

    private MapView mMapView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapView = findViewById(R.id.MapView);
        mMapView.onCreate(savedInstanceState);
    }

}