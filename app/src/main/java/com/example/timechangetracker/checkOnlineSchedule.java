package com.example.timechangetracker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;

public class checkOnlineSchedule extends JobService {

    getOnlineSchedule getOnlineSchedule;
    JobParameters parameters;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        this.parameters = jobParameters;
        getOnlineSchedule = new getOnlineSchedule();
        getOnlineSchedule.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        if(null != getOnlineSchedule){
            if(!getOnlineSchedule.isCancelled()){
                getOnlineSchedule.cancel(true);
            }
        }
        return false;
    }

    private class getOnlineSchedule extends AsyncTask<Void, Void, Void>{
        public static final String CHANNEL_1 = "Time Changed";

        @Override
        protected Void doInBackground(Void... voids) {

            ArrayList<String> strings, sqlTimes;
            strings = getLocationValues();
            sqlTimes = getSqlTimes();


            for(int i = 0; i < strings.size(); i++) {

                String onlineSchedule = null;
                String[] onlineDayByDay;
                String[] sqlDayByDay;

                try {
                    Document doc = Jsoup.connect("https://www.google.com/search" + "?q=" + strings.get(i)).get();

                    Elements element = doc.getElementsByClass("WgFkxc");

                    onlineSchedule = element.text();
                } catch (Exception e) {
                    e.printStackTrace(); //todo stackoverflow people say dont use this. still need to catch but not with printstacktrace
                }

                if(onlineSchedule != null) {
                    onlineDayByDay = organizeTimes(onlineSchedule);
                    sqlDayByDay = organizeTimes(sqlTimes.get(i));
                    if(!Arrays.equals(onlineDayByDay, sqlDayByDay)){
                        timeChangeNotification();
                    }
                }
            }


            return null;
        }

        private ArrayList<String> getLocationValues(){
            ArrayList<String> locationList;
            SQLHelper sql;
            //HomePageFragment home = new HomePageFragment();
            locationList = new ArrayList<>();
            sql = new SQLHelper(checkOnlineSchedule.this);

            Cursor values = sql.getData();

            if (values.getCount() > 0) {
                while (values.moveToNext()) {
                    String arrayListBuffer = values.getString(0) + " " + values.getString(1) +
                            " " + values.getString(2) + " " + values.getString(3);
                    locationList.add(arrayListBuffer);
                }
            }
            return locationList;
        }

        private ArrayList<String> getSqlTimes(){
            ArrayList<String> sqlTimesList;
            SQLHelper sql;
            HomePageFragment home = new HomePageFragment();
            sqlTimesList = new ArrayList<>();
            sql = new SQLHelper(checkOnlineSchedule.this);

            Cursor values = sql.getData();

            if (values.getCount() > 0) {
                while (values.moveToNext()) {
                    String arrayListBuffer = values.getString(4);
                    sqlTimesList.add(arrayListBuffer);
                }
            }
            return sqlTimesList;
        }

        private String[] organizeTimes(String times){
            times = times.toLowerCase();
            String[] organizeHelper = times.split(" ");
            String[] dayByday = new String[7];

            int i = 0;
            while(i < organizeHelper.length){
                StringBuilder stringBuilder = new StringBuilder();
                switch (organizeHelper[i]){
                    case "monday":
                        stringBuilder.append("Monday");
                        i++;
                        while (i < organizeHelper.length && organizeHelper[i] != null && !organizeHelper[i].equals("tuesday") &&
                                !organizeHelper[i].equals("wednesday") && !organizeHelper[i].equals("thursday") &&
                                !organizeHelper[i].equals("friday") && !organizeHelper[i].equals("saturday") &&
                                !organizeHelper[i].equals("sunday")){
                            stringBuilder.append(" ");
                            stringBuilder.append(organizeHelper[i]);
                            i++;
                        }
                        dayByday[0] = stringBuilder.toString();
                        break;
                    case "tuesday":
                        stringBuilder.append("Tuesday");
                        i++;
                        while (i < organizeHelper.length && organizeHelper[i] != null && !organizeHelper[i].equals("monday") &&
                                !organizeHelper[i].equals("wednesday") && !organizeHelper[i].equals("thursday") &&
                                !organizeHelper[i].equals("friday") && !organizeHelper[i].equals("saturday") &&
                                !organizeHelper[i].equals("sunday")){
                            stringBuilder.append(" ");
                            stringBuilder.append(organizeHelper[i]);
                            i++;
                        }
                        dayByday[1] = stringBuilder.toString();
                        break;
                    case "wednesday":
                        stringBuilder.append("Wednesday");
                        i++;
                        while (i < organizeHelper.length && organizeHelper[i] != null && !organizeHelper[i].equals("tuesday") &&
                                !organizeHelper[i].equals("monday") && !organizeHelper[i].equals("thursday") &&
                                !organizeHelper[i].equals("friday") && !organizeHelper[i].equals("saturday") &&
                                !organizeHelper[i].equals("sunday")){
                            stringBuilder.append(" ");
                            stringBuilder.append(organizeHelper[i]);
                            i++;
                        }
                        dayByday[2] = stringBuilder.toString();
                        break;
                    case "thursday":
                        stringBuilder.append("Thursday");
                        i++;
                        while (i < organizeHelper.length && organizeHelper[i] != null && !organizeHelper[i].equals("tuesday") &&
                                !organizeHelper[i].equals("wednesday") && !organizeHelper[i].equals("monday") &&
                                !organizeHelper[i].equals("friday") && !organizeHelper[i].equals("saturday") &&
                                !organizeHelper[i].equals("sunday")){
                            stringBuilder.append(" ");
                            stringBuilder.append(organizeHelper[i]);
                            i++;
                        }
                        dayByday[3] = stringBuilder.toString();
                        break;
                    case "friday":
                        stringBuilder.append("Friday");
                        i++;
                        while (i < organizeHelper.length && organizeHelper[i] != null && !organizeHelper[i].equals("tuesday") &&
                                !organizeHelper[i].equals("wednesday") && !organizeHelper[i].equals("thursday") &&
                                !organizeHelper[i].equals("monday") && !organizeHelper[i].equals("saturday") &&
                                !organizeHelper[i].equals("sunday")){
                            stringBuilder.append(" ");
                            stringBuilder.append(organizeHelper[i]);
                            i++;
                        }
                        dayByday[4] = stringBuilder.toString();
                        break;
                    case "saturday":
                        stringBuilder.append("Saturday");
                        i++;
                        while (i < organizeHelper.length && organizeHelper[i] != null && !organizeHelper[i].equals("tuesday") &&
                                !organizeHelper[i].equals("wednesday") && !organizeHelper[i].equals("thursday") &&
                                !organizeHelper[i].equals("friday") && !organizeHelper[i].equals("monday") &&
                                !organizeHelper[i].equals("sunday")){
                            stringBuilder.append(" ");
                            stringBuilder.append(organizeHelper[i]);
                            i++;
                        }
                        dayByday[5] = stringBuilder.toString();
                        break;
                    case "sunday":
                        stringBuilder.append("Sunday");
                        i++;
                        while (i < organizeHelper.length && organizeHelper[i] != null && !organizeHelper[i].equals("tuesday") &&
                                !organizeHelper[i].equals("wednesday") && !organizeHelper[i].equals("thursday") &&
                                !organizeHelper[i].equals("friday") && !organizeHelper[i].equals("saturday") &&
                                !organizeHelper[i].equals("monday")){
                            stringBuilder.append(" ");
                            stringBuilder.append(organizeHelper[i]);
                            i++;
                        }
                        dayByday[6] = stringBuilder.toString();
                        break;
                    default:
                        break;
                }
            }
            return dayByday;
        }

        private void timeChangeNotification(){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                NotificationChannel channel = new NotificationChannel("TimeChange", "Time change", NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription("test");
                channel.setShowBadge(true);

                NotificationManager manager = getSystemService(NotificationManager.class);
                manager.createNotificationChannel(channel);

            }

            //todo change so it opens a specific fragment which corresponds to the location that contains the time change
            Intent intent = new Intent(checkOnlineSchedule.this, HomePageFragment.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(checkOnlineSchedule.this, 0, intent, 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(checkOnlineSchedule.this, "TimeChange")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("TIME CHANGED")
                    .setContentText("The hours of operation has been changed for a day.")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setChannelId("TimeChange");

            NotificationManagerCompat notify = NotificationManagerCompat.from(checkOnlineSchedule.this);
            notify.notify(1, builder.build());
        }
    }
}