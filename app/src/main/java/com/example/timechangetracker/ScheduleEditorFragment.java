package com.example.timechangetracker;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class ScheduleEditorFragment extends Fragment {

    String name, address, city, state, time, arrayValue;
    EditText nameInput, addressInput, cityInput, stateInput;
    Button finishButton, cancelButton;
    ImageButton muteButton, backButton;
    RadioButton oneDay, twoDays, threeDays, fourDays, fiveDays, sixDays;
    Integer[] checkboxes;
    TextView question;
    SQLHelper dataBase;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_schedule_editor, container, false);

        dataBase = new SQLHelper(getActivity());

        nameInput = view.findViewById(R.id.businessExample);
        addressInput = view.findViewById(R.id.addressExample);
        cityInput = view.findViewById(R.id.cityExample);
        stateInput = view.findViewById(R.id.stateExample);

        findAllCheckboxViews(view);

        question = view.findViewById(R.id.question_when_to_notify);

        finishButton = view.findViewById(R.id.button_finish_edit_schedule);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean inputsSatisfied = true;

                if (nameInput.length() == 0) {
                    inputsSatisfied = false;
                    nameInput.setError("Name of a business or location required");
                }
                if (addressInput.length() == 0) {
                    inputsSatisfied = false;
                    addressInput.setError("Address required to ensure correct location");
                }
                if (cityInput.length() == 0) {
                    inputsSatisfied = false;
                    cityInput.setError("City required to ensure correct location");
                }
                if (stateInput.length() == 0) {
                    inputsSatisfied = false;
                    stateInput.setError("State required to ensure correct location");
                }

                if (!oneDay.isChecked() && !twoDays.isChecked() && !threeDays.isChecked() && !fourDays.isChecked()
                        && !fiveDays.isChecked() && !sixDays.isChecked()) {
                    inputsSatisfied = false;
                    question.setTextColor(Color.RED);
                    question.setError("Please choose at least 1 option below");
                    alertNotificationMissing();
                }

                if(inputsSatisfied){
                    checkboxes = new Integer[6];

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

                    name = nameInput.getText().toString();
                    address = addressInput.getText().toString();
                    city = cityInput.getText().toString();
                    state = stateInput.getText().toString();

                    arrayValue = name + " " + address + " " + city + " " + state;

                    webScrape scrape = new webScrape();
                    scrape.execute();

                    time = null;
                    for(int i = 0; i < 3 && time == null; i++){
                        giveScrapeTime();
                        time = scrape.getVal();
                    }

                    boolean checkDataInserted = false;
                    if(time != null && !time.equals("")){
                        checkDataInserted = dataBase.insertUserData(name, address, city, state,
                                time, checkboxes[0], checkboxes[1], checkboxes[2], checkboxes[3],
                                checkboxes[4], checkboxes[5]);
                    }

                    if(checkDataInserted){
                        Toast.makeText(getActivity(), "New Entry Inserted", Toast.LENGTH_SHORT).show();

                        HomePageFragment homeFragment = new HomePageFragment();
                        FragmentTransaction transition = getParentFragmentManager().beginTransaction();
                        transition.replace(R.id.fragmentContainer, homeFragment).commit();
                    } else {
                        alertTimeNotRetrievable();
                        Toast.makeText(getActivity(), "New Entry Not Inserted", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        cancelButton = view.findViewById(R.id.button_cancel_edit_schedule);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transition = getParentFragmentManager().beginTransaction();
                transition.replace(R.id.fragmentContainer, new HomePageFragment()).commit();
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






    private void alertTimeNotRetrievable(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Sorry, the hours of operation for your location could not be collected."
                + " Your internet connection could be the issue or the hours of operation"
                + " are not retrievable for this location.")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog checkboxAlert = builder.create();
        checkboxAlert.show();
    }

    private void findAllCheckboxViews(View view){
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

        AlertDialog checkboxAlert = builder.create();
        checkboxAlert.setTitle("Oops, you forgot to choose an option to be notified.");
        checkboxAlert.show();
    }

    class webScrape extends AsyncTask<Void, Void, Void> {

        String val;
        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Document doc = Jsoup.connect("https://www.google.com/search" + "?q=" + arrayValue).get();

                Elements element = doc.getElementsByClass("WgFkxc");

                val = element.text();

            } catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }
        public String getVal(){
            return val;
        }
    }

    public void giveScrapeTime(){
        try {
            java.util.concurrent.TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
