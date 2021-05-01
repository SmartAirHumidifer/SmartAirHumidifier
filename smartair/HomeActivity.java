package com.example.smartair;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

import static com.example.smartair.Notif.CHANNEL_HUMIDITY_ID;
import static com.example.smartair.Notif.CHANNEL_TEMPERATURE_ID;
import static com.example.smartair.Notif.CHANNEL_WATER_ID;
import static com.example.smartair.Page1.extractInt;

public class HomeActivity extends MainActivity {

    ViewPager viewPager;
    LinearLayout sliderDotspanel;
    private int dotscount;
    private ImageView[] dots;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference, fabReference;
    private FirebaseUser user;
    private String userID;
    private NotificationManagerCompat notificationManager;
    int relayState = 0;
    private int notifTemp;
    private int notifHumid;
    private long notifWater = 0;
    private TextView header;
    private ImageView headerImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        loadLocale();

        notificationManager = NotificationManagerCompat.from(this);

        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        }

        //inflate your activity layout here!
        @SuppressLint("InflateParams")
        View contentView = inflater.inflate(R.layout.activity_home, null, false);
        drawerLayout.addView(contentView, 0);
        navigationView.setCheckedItem(R.id.nav_home);

        fabReference = FirebaseDatabase.getInstance().getReference().child("Relay");

        header = findViewById(R.id.text_home);
        headerImage = findViewById(R.id.imageView2);



        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(relayState != 1) {
                    relayState = 1;
                    insertRelay(relayState);
                    header.setText(R.string.humidifierOFF);
                    headerImage.setImageDrawable(getDrawable(R.drawable.ic_baseline_highlight_off_24));
                    fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                    Toast.makeText(getApplicationContext(),getString(R.string.humidifierOFF),Toast.LENGTH_LONG).show();
                }else{
                    relayState = 0;
                    insertRelay(relayState);
                    header.setText(R.string.humidifierON);
                    headerImage.setImageDrawable(getDrawable(R.drawable.icon));
                    fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                    Toast.makeText(getApplicationContext(),getString(R.string.humidifierON),Toast.LENGTH_LONG).show();
                }
            }


        });

        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("userdata");
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        reference.child(userID).addValueEventListener(new ValueEventListener() {
            private static final String TAG = "HomeActivity" ;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot childSnapshot: snapshot.getChildren()) {
                    Readings readings = childSnapshot.getValue(Readings.class);

                    String temp = readings.getTemperature();
                    String humid = readings.getHumidity();
                    String water = readings.getWeight();


                    String extractTemp = extractInt(temp);
                    notifTemp = Integer.parseInt(extractTemp);
                    System.out.println(temp + ", " + extractTemp + ", " + notifTemp);

                    String extractHumid = extractInt(humid);
                    notifHumid = Integer.parseInt(extractHumid);
                    System.out.println(humid + ", " + extractHumid + ", " + notifHumid);

                    String extractWater = extractInt(water);
                    notifWater = Long.parseLong(extractWater);
                    System.out.println(water + "," + extractWater + ", " + notifWater);
                }


                if(notifTemp <= 140 || notifTemp >= 300) {
                    sendOnChannelTemperature(notifTemp);
                }
                if(notifHumid <=250 || notifHumid >= 500){
                    sendOnChannelHumidity(notifHumid);
                }
                if(notifWater <= 300){
                    sendOnChannelWater(notifWater);
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        });



        viewPager = (ViewPager) findViewById(R.id.viewPager);
        sliderDotspanel = (LinearLayout) findViewById(R.id.SliderDots);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.add(new Page1());
        viewPagerAdapter.add(new Page2());


        viewPager.setAdapter(viewPagerAdapter);
        dotscount = viewPagerAdapter.getCount();
        dots = new ImageView[dotscount];



        for(int i = 0; i < dotscount; i++) {

            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            sliderDotspanel.addView(dots[i], params);
        }
        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for(int i = 0; i< dotscount; i++){
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void insertRelay(int state){
        int stateR = state;

        Relay relay = new Relay(stateR);
        fabReference.setValue(relay);
    }

    public void sendOnChannelWater(long water){

        String title = getString(R.string.waterNotifTitle);
        String message = "";
        int priority = 0;

        if(water >= 300  && water <= 500 ){
            message = getString(R.string.waterLevelLow);
            priority = NotificationCompat.PRIORITY_LOW;
        }
        if(water < 300){
            message = getString(R.string.waterLevelEmpty);
            priority = NotificationCompat.PRIORITY_HIGH;
        }

        Intent activityIntent = new Intent(this, HomeActivity.class);


        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, activityIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_WATER_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(message)
                .setColor(Color.BLUE)
                .setPriority(priority)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .build();

        notificationManager.notify(1, notification);
    }
    public void sendOnChannelTemperature(int temperature){
        String title = getString(R.string.temperatureNotifTitle);
        String message = "";
        int priority = 0;

        if (temperature < 140) {
            message = getString(R.string.temperatureLow);
            priority = NotificationCompat.PRIORITY_DEFAULT;
        }else if(temperature > 300 ){
            message = getString(R.string.temperatureHigh);
            priority = NotificationCompat.PRIORITY_HIGH;
        }

        Intent activityIntent = new Intent(this, HomeActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, activityIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_TEMPERATURE_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(priority)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build();

        notificationManager.notify(2, notification);
    }

    public void sendOnChannelHumidity(int humidity){
        String title = getString(R.string.humidityNotifTitle);
        String message = getString(R.string.humidNotifMessage);
        int priority = 0;

        if(humidity <= 250 ){
            message = getString(R.string.humidityLow);
            priority = NotificationCompat.PRIORITY_DEFAULT;
        }else if(humidity >= 500){
            message = getString(R.string.humidityHigh);
            priority = NotificationCompat.PRIORITY_HIGH;
        }

        Intent activityIntent = new Intent(this, HomeActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, activityIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_HUMIDITY_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(priority)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build();

        notificationManager.notify(3, notification);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_settings:
                Toast.makeText(getApplicationContext(), getString(R.string.settings),Toast.LENGTH_LONG).show();
                Intent intent = new Intent(HomeActivity.this,SettActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}