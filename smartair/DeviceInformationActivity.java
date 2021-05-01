package com.example.smartair;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

public class DeviceInformationActivity extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Current language of the app will be set.
        loadLocale();
        @SuppressLint("InflateParams")
        View contentView = inflater.inflate(R.layout.activity_device_info, null, false);
        drawerLayout.addView(contentView, 0);
        navigationView.setCheckedItem(R.id.nav_profile);

        TextView deviceName = findViewById(R.id.profile_device_name);
        TextView modelNo = findViewById(R.id.profile_modelNo);

        //Set shared preferences for the device profile.
        SharedPreferences sharedPreferences = getSharedPreferences("Profile",MODE_PRIVATE);
        String name = sharedPreferences.getString("name","");
        String number = sharedPreferences.getString("mNumber", "");
        deviceName.setText(name);
        modelNo.setText(number);
    }

}
