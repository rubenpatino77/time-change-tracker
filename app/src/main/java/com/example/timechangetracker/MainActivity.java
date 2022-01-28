package com.example.timechangetracker;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        String newString;
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            newString= null;
            checkOnlineInit();
        } else {
            newString= extras.getString("parameters");

            Bundle bundle = new Bundle();
            String[] tokenHelper = newString.split("\n");

            bundle.putString("name", tokenHelper[0]);
            bundle.putString("address", tokenHelper[1]);
            bundle.putString("city", tokenHelper[2]);
            bundle.putString("state", tokenHelper[3]);
            bundle.putString("time", tokenHelper[4]);

            EditExistingScheduleFragment frag = new EditExistingScheduleFragment();
            frag.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, frag).addToBackStack(null).commit();
        }
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