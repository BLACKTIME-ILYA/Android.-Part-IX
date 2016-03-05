package com.sourceit.task21.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.sourceit.task21.utils.L;

import java.util.Calendar;

import static com.sourceit.task21.ui.MainActivity.COUNTACTIVEMEMORY;
import static com.sourceit.task21.ui.MainActivity.DAY;
import static com.sourceit.task21.ui.MainActivity.LOCALRECEIVER;

/**
 * Created by User on 04.03.2016.
 */
public class MyReceiver extends BroadcastReceiver {

    public static final double GO = 10.5;
    public static final float STOP = 9;
    public static final String CONNECT = "connect";

    Calendar calendar;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    SensorManager sensorManager;
    SensorEventListener sensorEventListener;
    Context context;

    LocalBroadcastManager localBroadcastManager;
    Intent i;

    float ax;
    float ay;
    float az;
    double factor;
    public static final int POWER = 2;

    @Override
    public void onReceive(final Context context, Intent intent) {
        L.d("onReceive");

        sp = context.getSharedPreferences(CONNECT, Context.MODE_PRIVATE);
        editor = sp.edit();
        calendar = Calendar.getInstance();

        if (sp.getInt(DAY, 0) != calendar.get(calendar.DAY_OF_YEAR)) {
            editor.putInt(DAY, calendar.get(calendar.DAY_OF_YEAR));
            editor.putInt(COUNTACTIVEMEMORY, 0);
            editor.apply();
        }

        sensorManager = (SensorManager) context.getApplicationContext().getSystemService(context.SENSOR_SERVICE);
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    ax = event.values[0];
                    ay = event.values[1];
                    az = event.values[2];
                    factor = Math.sqrt(Math.pow(ax, POWER) + Math.pow(ay, POWER) + Math.pow(az, POWER));
                    Log.d("factor", String.valueOf(factor));

                    if (factor > GO || factor < STOP) {
                        editor.apply();
                        if (sp.getBoolean(LOCALRECEIVER, false)) {
                            sendMessage();
                        } else {
                            int memory = sp.getInt(COUNTACTIVEMEMORY, 0);
                            memory++;
                            editor.putInt(COUNTACTIVEMEMORY, memory);
                            editor.apply();
                            L.d("memory: " + memory);
                        }
                    }
                    sensorManager.unregisterListener(sensorEventListener);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void sendMessage() {
        i = new Intent();
        i.setAction("my.customlocal.INTENT");
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        localBroadcastManager.sendBroadcast(i);
    }
}
