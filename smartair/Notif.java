package com.example.smartair;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import static com.example.smartair.R.string;

public class Notif extends Application {
    public  static  final  String CHANNEL_WATER_ID = "Water Level";
    public  static  final  String CHANNEL_TEMPERATURE_ID = "Temperature";
    public  static  final  String CHANNEL_HUMIDITY_ID = "Humidity";
    @Override
    public void onCreate(){
        super.onCreate();

        createNotificationChannels();
    }
    private  void createNotificationChannels(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel waterLevel = new NotificationChannel(
                    CHANNEL_WATER_ID,
                    getString(R.string.waterLevel),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            waterLevel.setDescription(getString(string.waterDescription));

            NotificationChannel temp = new NotificationChannel(
                    CHANNEL_TEMPERATURE_ID,
                    getString(string.temperatureLevel),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            temp.setDescription(getString(string.tempDescription));

            NotificationChannel humidity = new NotificationChannel(
                    CHANNEL_HUMIDITY_ID,
                    getString(string.humidLevel),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            temp.setDescription(getString(string.humidDescription));

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(waterLevel);
            manager.createNotificationChannel(temp);
            manager.createNotificationChannel(humidity);
        }
    }
}
