package com.example.yomac_000.rsrpechhulp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Timer timer = new Timer();
        //Through the schedule() we execute a task after 1.5 seconds
        timer.schedule(new TimerTask() {
            public void run() {
                intent = new Intent(MainActivity.this, RehabilitationMenu.class);
                startActivity(intent);
                finish();
            }
        }, 1500);
    }
}
