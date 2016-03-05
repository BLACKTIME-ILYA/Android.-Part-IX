package com.sourceit.task21.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sourceit.task21.R;
import com.sourceit.task21.utils.L;

import java.util.Calendar;

import static com.sourceit.task21.ui.MyReceiver.CONNECT;

public class MainActivity extends AppCompatActivity {

    public static final String COUNTACTIVEMEMORY = "countactivememory";
    public static final String ALARM = "alarm";
    public static final String LOCALRECEIVER = "localreceiver";
    public static final int TWO_MINUTES = 120000;
    public static final String DAY = "day";
    public static final int ONE = 1;

    LinearLayout container;
    View line;

    int countActive;
    int countActiveMemory;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    Calendar calendar;
    AlarmManager alarm;
    PendingIntent pendingIntent;

    BroadcastReceiver localReceiver = new MyLocalReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        L.d("on create activity");

        container = (LinearLayout) findViewById(R.id.container);

        calendar = Calendar.getInstance();
        sp = getSharedPreferences(CONNECT, Context.MODE_PRIVATE);
        editor = sp.edit();

        if (!sp.getBoolean(ALARM, false)) {
            alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
            pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent("my.custom.INTENT"), 0);
            alarm.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0, TWO_MINUTES, pendingIntent);
            editor.putBoolean(ALARM, true);
            editor.putInt(DAY, calendar.get(calendar.DAY_OF_YEAR));
            editor.apply();
            Toast.makeText(getApplicationContext(), "Первый результат через 2 минуты!", Toast.LENGTH_SHORT).show();
        } else {
            setColor(sp.getInt(COUNTACTIVEMEMORY, 0));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter iff = new IntentFilter("my.customlocal.INTENT");
        LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver, iff);
        editor.putBoolean(LOCALRECEIVER, true);
        editor.apply();

        editor.putInt(COUNTACTIVEMEMORY, 0);
        editor.apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(localReceiver);
        editor.putBoolean(LOCALRECEIVER, false);
        editor.apply();
    }

    @Override
    protected void onStop() {
        super.onStop();
        editor.putInt(COUNTACTIVEMEMORY, container.getChildCount());
        editor.apply();
    }

    private void setColor(int value) {
        L.d("setColor");
        synchronized (this) {
            for (int i = 0; i < value; i++) {
                L.d("countActive: " + countActive);
                L.d("activeMemory: " + countActiveMemory);
                line = new View(this);
                line.setBackgroundColor(Color.parseColor("#4CAF50"));
                container.addView(line, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 1));
            }
        }
    }

    public class MyLocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("MyReceiver", "onReceive");
            setColor(ONE);
        }
    }
}
