package com.example.smartair;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartair.MainActivity;
import com.example.smartair.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ConnectActivity extends MainActivity implements ConnectDialog.ConnectDialogListener {

    private TextView name, number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Current language of the app will be set.
        loadLocale();

        @SuppressLint("InflateParams")
        View contentView = inflater.inflate(R.layout.activity_connect, null, false);
        drawerLayout.addView(contentView, 0);
        navigationView.setCheckedItem(R.id.nav_connect);


        name = findViewById(R.id.textView_connect);
        number = findViewById(R.id.textView3_connect);

        //When connect button is clicked a dialg box will open
        ImageButton connect = findViewById(R.id.imageButton2);
        connect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                openDiolog();
                Snackbar.make(view, getString(R.string.deviceSearching), Snackbar.LENGTH_LONG)
                        .setAction(R.string.action, null).show();
            }
        });

    }

    // Open Dialog Box
    public void openDiolog(){
        ConnectDialog connectDialog = new ConnectDialog();
        connectDialog.show(getSupportFragmentManager(), getString(R.string.connectDialog));

    }

    //Function to apply text to the shared preference
    @Override
    public void applyTexts(String dName, String modelNo) {
        name.setText(dName);
        number.setText(modelNo);
        SharedPreferences sharedPref = getSharedPreferences("Profile", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("name",dName);
        editor.putString("mNumber", modelNo);
        editor.apply();
    }

}