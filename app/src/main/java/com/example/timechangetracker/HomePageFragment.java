package com.example.timechangetracker;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.PrecomputedText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.w3c.dom.Element;

import java.util.ArrayList;

import javax.xml.transform.Result;


public class HomePageFragment extends Fragment {

    ArrayList<String> arrayList, arrayListHelper;
    ArrayAdapter<String> arrayAdapter;
    ListView listView;
    SQLHelper sql;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home_page, container, false);

        arrayList = new ArrayList<>();
        arrayListHelper = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, arrayList);

        sql = new SQLHelper(view.getContext());
        Cursor values = sql.getData();
        if (values.getCount() > 0) {
            while (values.moveToNext()) {
                String arrayListBuffer = values.getString(0) + "\n" +
                        values.getString(1) + " " + values.getString(2) + " " + values.getString(3);
                arrayList.add(arrayListBuffer);

                String timesBuffer = values.getString(0) + "\n" +
                        values.getString(1) + "\n" + values.getString(2) + "\n" +
                        values.getString(3) + "\n" + values.getString(4);
                arrayListHelper.add(timesBuffer);
            }

            listView = view.findViewById(R.id.list_view);
            listView.setAdapter(arrayAdapter);
        }

        if(arrayList.size() > 0){
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Bundle bundle = new Bundle();

                    String[] tokenHelper = arrayListHelper.get(i).split("\n");

                    bundle.putString("name", tokenHelper[0]);
                    bundle.putString("address", tokenHelper[1]);
                    bundle.putString("city", tokenHelper[2]);
                    bundle.putString("state", tokenHelper[3]);
                    bundle.putString("time", tokenHelper[4]);
                    EditExistingScheduleFragment existingScheduleFragment = new EditExistingScheduleFragment();
                    existingScheduleFragment.setArguments(bundle);
                    FragmentTransaction transition = getParentFragmentManager().beginTransaction();
                    transition.replace(R.id.fragmentContainer, existingScheduleFragment).commit();
                }
            });
        }

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button newScheduleButton = view.findViewById(R.id.button_new_schedule);

        newScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transition = getParentFragmentManager().beginTransaction();
                transition.replace(R.id.fragmentContainer, new ScheduleEditorFragment()).commit();
            }
        });
    }
}
