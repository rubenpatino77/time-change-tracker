package com.example.timechangetracker;

import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class EditExistingScheduleFragment extends Fragment {

    String name, address, city, state, time;
    TextView nameInput, addressInput, cityInput, stateInput, timeFound;
    Button finishButton, deleteButton;
    ImageButton muteButton, backButton;
    RadioButton oneDay, twoDays, threeDays, fourDays, fiveDays, sixDays;
    Integer[] checkboxes;
    SQLHelper sql;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_existing_schedule, container, false);

        SQLHelper dataBase = new SQLHelper(getActivity());

        nameInput = view.findViewById(R.id.businessExample);
        addressInput = view.findViewById(R.id.addressExample);
        cityInput = view.findViewById(R.id.cityExample);
        stateInput = view.findViewById(R.id.stateExample);
        timeFound = view.findViewById(R.id.time_textview);

        name = requireArguments().getString("name");
        address = requireArguments().getString("address");
        city = requireArguments().getString("city");
        state = requireArguments().getString("state");
        time = requireArguments().getString("time");

        checkboxes = new Integer[6];

        getSQLCheckboxes(view, checkboxes);

        nameInput.setText(name);
        addressInput.setText(address);
        cityInput.setText(city);
        stateInput.setText(state);

        timeFound.setText(organizeTimes(time.toLowerCase()));

        assignCheckboxObjects(view);

        TextView question = view.findViewById(R.id.question_when_to_notify);

        for(int i = 0; i<checkboxes.length; i++){
            if (checkboxes[i].equals(1)){
                switch (i){
                    case 0: oneDay.setChecked(true);
                        break;
                    case 1: twoDays.setChecked(true);
                        break;
                    case 2: threeDays.setChecked(true);
                        break;
                    case 3: fourDays.setChecked(true);
                        break;
                    case 4: fiveDays.setChecked(true);
                        break;
                    case 5: sixDays.setChecked(true);
                        break;
                    default:
                        break;
                }
            }
        }

        finishButton = view.findViewById(R.id.button_finish_edit_schedule);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean inputsSatisfied = true;

                if (!oneDay.isChecked() && !twoDays.isChecked() && !threeDays.isChecked() && !fourDays.isChecked()
                        && !fiveDays.isChecked() && !sixDays.isChecked()) {
                    inputsSatisfied = false;
                    question.setTextColor(Color.RED);
                    question.setError("Please choose at least 1 option below");
                    alertNotificationMissing();
                }


                if(inputsSatisfied){

                    if(oneDay.isChecked()){
                        checkboxes[0] = 1;
                    } else {
                        checkboxes[0] = 0;
                    }
                    if(twoDays.isChecked()){
                        checkboxes[1] = 1;
                    } else {
                        checkboxes[1] = 0;
                    }
                    if(threeDays.isChecked()){
                        checkboxes[2] = 1;
                    } else {
                        checkboxes[2] = 0;
                    }
                    if(fourDays.isChecked()){
                        checkboxes[3] = 1;
                    } else {
                        checkboxes[3] = 0;
                    }
                    if(fiveDays.isChecked()){
                        checkboxes[4] = 1;
                    } else {
                        checkboxes[4] = 0;
                    }
                    if(sixDays.isChecked()){
                        checkboxes[5] = 1;
                    } else {
                        checkboxes[5] = 0;
                    }

                    boolean checkDataUpdated = dataBase.updateUserData(name, address, city, state,
                            checkboxes[0], checkboxes[1], checkboxes[2], checkboxes[3], checkboxes[4],
                            checkboxes[5]);

                    if(checkDataUpdated){
                        Toast.makeText(getActivity(), "New Entry Updated", Toast.LENGTH_SHORT).show();

                        HomePageFragment homeFragment = new HomePageFragment();
                        FragmentTransaction transition = getParentFragmentManager().beginTransaction();
                        transition.replace(R.id.fragmentContainer, homeFragment).commit();
                    } else {
                        Toast.makeText(getActivity(), "New Entry Not Updated", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        deleteButton = view.findViewById(R.id.button_cancel_edit_schedule);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean checkDataDeleted = dataBase.deleteUserData(name, address, city, state);

                if(checkDataDeleted){
                    Toast.makeText(getActivity(), "Entry Deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Entry Not Deleted", Toast.LENGTH_SHORT).show();
                }

                FragmentTransaction transition = getParentFragmentManager().beginTransaction();
                transition.replace(R.id.fragmentContainer, new HomePageFragment()).commit();
            }
        });

        muteButton = view.findViewById(R.id.button_mute_schedule);
        muteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        backButton = view.findViewById(R.id.button_go_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transition = getParentFragmentManager().beginTransaction();
                transition.replace(R.id.fragmentContainer, new HomePageFragment()).commit();
            }
        });

        return view;
    }


    private void getSQLCheckboxes(View view, Integer[] checkboxes){
        //todo fix so it is not so inefficient. Use sql syntax in getCheckboxes()
        sql = new SQLHelper(view.getContext());
        Cursor cursor = sql.getData();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                if(cursor.getString(0).equals(name) && cursor.getString(1).equals(address) &&
                        cursor.getString(2).equals(city) && cursor.getString(3).equals(state)){
                    for( int i = 0; i < checkboxes.length; i++){
                        int indexHolder = 5 + i;
                        checkboxes[i] = cursor.getInt(indexHolder);
                    }
                }
            }}
    }

    private void assignCheckboxObjects(View view){
        oneDay = view.findViewById(R.id.day_before);
        twoDays = view.findViewById(R.id.two_days_before);
        threeDays = view.findViewById(R.id.three_days_before);
        fourDays = view.findViewById(R.id.four_days_before);
        fiveDays = view.findViewById(R.id.five_days_before);
        sixDays = view.findViewById(R.id.six_days_before);
    }


    private void alertNotificationMissing(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("If you would like to temporarily turn off all notifications from this schedule, press the sound button in the top right corner of the page.")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Oops, you forgot to choose an option to be notified.");
        alert.show();
    }

    private String organizeTimes(String times){
        //parse times by spaces " " then organize according to when we find a weekday. when we find a weekday(skip 1st one) first print new line then print weekday with time until we find another weekday in array.
        String organizedTime, mon = null, tue = null, wed = null, thu = null, fri = null, sat = null, sun = null;
        String[] organizeHelper = times.split(" ");

        int i = 0;
        while(i < organizeHelper.length){
            StringBuilder stringBuilder = new StringBuilder();
            switch (organizeHelper[i].toLowerCase()){
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
                    mon = stringBuilder.toString();
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
                    tue = stringBuilder.toString();
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
                    wed = stringBuilder.toString();
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
                    thu = stringBuilder.toString();
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
                    fri = stringBuilder.toString();
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
                    sat = stringBuilder.toString();
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
                    sun = stringBuilder.toString();
                    break;
                default:
                    break;
            }
        }
        organizedTime = mon + "\n" + tue + "\n" + wed + "\n" + thu + "\n" + fri + "\n" + sat + "\n" + sun;
        return organizedTime;
    }
}
