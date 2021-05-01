 package com.example.smartair;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Locale;

 public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {
    DrawerLayout drawerLayout;
    FloatingActionButton fab;
    NavigationView navigationView;
    GoogleSignInClient googleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            startAnimatedActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }else if (id == R.id.nav_connect) {
            startAnimatedActivity(new Intent(getApplicationContext(), ConnectActivity.class));
        }else if (id == R.id.nav_profile) {
            startAnimatedActivity(new Intent(getApplicationContext(), DeviceInformationActivity.class));
        }else if (id == R.id.nav_tutorial) {
            startAnimatedActivity(new Intent(getApplicationContext(), TutorialActivity.class));
        }else if (id == R.id.nav_logout) {
            logOut();
        }
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void logOut(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        googleSignInClient = GoogleSignIn.getClient(MainActivity.this, GoogleSignInOptions.DEFAULT_SIGN_IN);
        builder.setMessage(getString(R.string.logoutConfirm))
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Sign out from Google
                        googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //Check Condition
                                if (task.isSuccessful()){
                                    //When task is successful
                                    //Sign out from firebase
                                    FirebaseAuth.getInstance().signOut();
                                    Toast.makeText(getApplicationContext(), getString(R.string.logoutSuccessful), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    protected void startAnimatedActivity(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @Override
    public void onBackPressed() {
        drawerLayout = findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

     private void setLocale(String lang) {
         Locale locale = new Locale(lang);
         Locale.setDefault(locale);
         Configuration config = new Configuration();
         config.locale = locale;
         getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
//        save data to shared preferences
         SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
         editor.putString("My_Lang", lang);
         editor.apply();
     }

     public void loadLocale(){
         SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
         String language = prefs.getString("My_Lang", "");
         setLocale(language);
     }



}
