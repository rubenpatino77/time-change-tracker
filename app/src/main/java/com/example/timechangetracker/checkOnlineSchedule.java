package com.example.timechangetracker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

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
        if (null != getOnlineSchedule) {
            if (!getOnlineSchedule.isCancelled()) {
                getOnlineSchedule.cancel(true);
            }
        }
        return false;
    }

    private class getOnlineSchedule extends AsyncTask<Void, Void, Void> {
        SQLHelper sql;

        @Override
        protected Void doInBackground(Void... voids) {

            ArrayList<String> locations, sqlTimes, notificationParams;

            notificationParams = getNewParams();
            locations = getLocationValues(notificationParams);
            sqlTimes = getSqlTimes(notificationParams);

            for (int i = 0; i < locations.size(); i++) {

                String onlineSchedule = null;
                String[] onlineDayByDay = new String[7];
                String[] sqlDayByDay;

                try {
                    Document doc = Jsoup.connect("https://www.google.com/search" + "?q=" + locations.get(i)).get();

                    Elements element = doc.getElementsByClass("WgFkxc");

                    onlineSchedule = element.text();
                } catch (Exception e) {
                    e.printStackTrace(); //todo stackoverflow people say dont use this. still need to catch but not with printstacktrace
                }

                if (onlineSchedule != null) {
                    onlineDayByDay = organizeTimes(onlineSchedule);


                    sqlDayByDay = organizeTimes(sqlTimes.get(i));

                    if (!Arrays.equals(onlineDayByDay, sqlDayByDay)) {
                        Calendar calendar = Calendar.getInstance();
                        int today = calendar.get(Calendar.DAY_OF_WEEK); //Day 1 starts on sunday


                        Integer[] checkboxes = new Integer[6];
                        getSQLCheckboxes(checkboxes, locations.get(i));

                        int checkedDaysBefore= 0;
                        for(int x = 0; x < checkboxes.length; x++){
                            if(checkboxes[x] == 1){
                                checkedDaysBefore = x; //Don't add 1 because sql & online data has day 1 on monday
                            }
                        }

                        if(checkedRadioButtonIsToday(today, checkedDaysBefore, sqlDayByDay, onlineDayByDay)){
                            try {
                                timeChangeNotification(notificationParams.get(i), i);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }


            return null;
        }

        private ArrayList<String> getLocationValues(ArrayList<String> fullDetails) {
            ArrayList<String> locationList = new ArrayList<>();

            for(int i=0; i < fullDetails.size(); i++){
                String[] tokenHelper = fullDetails.get(i).split("\n");
                locationList.add(tokenHelper[0] + " " + tokenHelper[1] + " " + tokenHelper[2] + " "
                            + tokenHelper[3]);
            }

            return locationList;
        }

        private ArrayList<String> getSqlTimes(ArrayList<String> fullDetails) {
            ArrayList<String> sqlTimesList = new ArrayList<>();

            for(int i=0; i < fullDetails.size(); i++){
                String[] tokenHelper = fullDetails.get(i).split("\n");
                sqlTimesList.add(tokenHelper[4]);
            }

            return sqlTimesList;
        }

        private String[] organizeTimes(String times) {
            times = times.toLowerCase();
            String[] organizeHelper = times.split(" ");
            String[] dayByday = new String[7];

            int i = 0;
            while (i < organizeHelper.length) {
                StringBuilder stringBuilder = new StringBuilder();
                switch (organizeHelper[i]) {
                    case "monday":
                        stringBuilder.append("Monday");
                        i++;
                        while (i < organizeHelper.length && organizeHelper[i] != null && !organizeHelper[i].equals("tuesday") &&
                                !organizeHelper[i].equals("wednesday") && !organizeHelper[i].equals("thursday") &&
                                !organizeHelper[i].equals("friday") && !organizeHelper[i].equals("saturday") &&
                                !organizeHelper[i].equals("sunday")) {
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
                                !organizeHelper[i].equals("sunday")) {
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
                                !organizeHelper[i].equals("sunday")) {
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
                                !organizeHelper[i].equals("sunday")) {
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
                                !organizeHelper[i].equals("sunday")) {
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
                                !organizeHelper[i].equals("sunday")) {
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
                                !organizeHelper[i].equals("monday")) {
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

        private void timeChangeNotification(String locationDetails, int id) throws InterruptedException {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("TimeChange", "Time change", NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription("test");
                channel.setShowBadge(true);

                NotificationManager manager = getSystemService(NotificationManager.class);
                manager.createNotificationChannel(channel);

            }

            Intent intent = new Intent(checkOnlineSchedule.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("parameters", locationDetails);
            PendingIntent pendingIntent = PendingIntent.getActivity(checkOnlineSchedule.this, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(checkOnlineSchedule.this, "TimeChange")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("TIME CHANGED")
                    .setContentText("The hours of operation has been changed for a day.")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setChannelId("TimeChange");

            NotificationManagerCompat notify = NotificationManagerCompat.from(checkOnlineSchedule.this);
            notify.notify(id, builder.build());
        }

        public boolean checkedRadioButtonIsToday(int today, int checkedDaysBefore,
                                                 String[] sqlDayByDay, String[] onlineDayByDay) {
            circularLinkedList daysOfWeek = new circularLinkedList();
            for(int i = 1; i < 8; i++){
                daysOfWeek.add(i);
            }

            circularLinkedList.Node current = daysOfWeek.head;

            while (current.data != today){
                current = current.nextNode;
            }

            for(int x = 0; x < checkedDaysBefore; x++){
                current = current.nextNode;
            }

            if(!onlineDayByDay[current.data - 1].equals(sqlDayByDay[current.data - 1])){
                return true;
            }
            return false;
        }

        private void getSQLCheckboxes(Integer[] checkboxes, String location){
            //todo fix so it is not so inefficient. Use sql syntax in getCheckboxes()
            sql = new SQLHelper(getApplicationContext());
            Cursor cursor = sql.getData();
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    if((cursor.getString(0) + " " + cursor.getString(1) + " " +
                            cursor.getString(2) + " " + cursor.getString(3)).equals(location) ){
                        for( int i = 0; i < checkboxes.length; i++){
                            int indexHolder = 5 + i;
                            checkboxes[i] = cursor.getInt(indexHolder);
                        }
                        break;
                    }
                }
            }
        }

        public ArrayList<String> getNewParams(){
            ArrayList<String> params = new ArrayList<>();

            sql = new SQLHelper(getApplicationContext());
            Cursor values = sql.getData();
            int index = 0;
            if (values.getCount() > 0) {
                while (values.moveToNext()) {
                    String stringBuffer = values.getString(0) + "\n" +
                            values.getString(1) + "\n" + values.getString(2) + "\n" +
                            values.getString(3) + "\n" + values.getString(4);
                    params.add(index, stringBuffer);
                    index++;
                }
            }

            return params;
        }
    }

    public class circularLinkedList {
        public class Node {
            int data;
            Node nextNode;

            public Node(int data) {
                this.data = data;
            }
        }

        public Node head = null;
        public Node tail = null;

        public void add(int data) {
            Node newNode = new Node(data);
            if (head == null) {
                head = newNode;
                tail = newNode;
                newNode.nextNode = head;
            } else {
                tail.nextNode = newNode;
                tail = newNode;
                tail.nextNode = head;
            }
        }
    }
}