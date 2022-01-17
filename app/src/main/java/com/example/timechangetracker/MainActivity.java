package com.example.timechangetracker;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Toolbar actionBar;
    private final static int DOWNLOAD_JOB_KEY = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = findViewById(R.id.toolBar);
        setSupportActionBar(actionBar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(null);

        HomePageFragment homePage = new HomePageFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, homePage).commit();

        checkOnlineInit();
    }

    private void checkOnlineInit(){
        ComponentName component = new ComponentName(this, checkOnlineSchedule.class);
        JobInfo.Builder builder = new JobInfo.Builder(DOWNLOAD_JOB_KEY + 1, component)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true).setPeriodic(13*60*60*1000);

        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }
}