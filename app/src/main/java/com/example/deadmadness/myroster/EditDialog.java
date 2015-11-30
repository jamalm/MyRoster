package com.example.deadmadness.myroster;

/**
 * Created by deadmadness on 25/11/15.
 */
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;

public class EditDialog extends Dialog {

    //fields for dialog, Activity in question, spinners for selection, buttons
    public Activity mainActivity;
    Spinner spinner, spinner2, spinner3, spinnerAmPm, spinnerAmPm2;
    public Button addBtn;

    //user preferences
    String user_session;
    SharedPreferences preferences;

    //database declaration
    DatabaseManager db;
    //id variable declaration
    long mID;

    //ArrayLists for spinner data
    ArrayList<String> days;
    ArrayList<String> startTimes;
    ArrayList<String> endTimes;
    ArrayList<String> time;

    //constructor sets activity and id from MainActivity
    public EditDialog(Activity activity, long id) {
        super(activity);
        this.mainActivity = activity;
        mID = id;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_day);

        //instances of spinner data
        days = new ArrayList<>();
        days.add("Monday");
        days.add("Tuesday");
        days.add("Wednesday");
        days.add("Thursday");
        days.add("Friday");
        days.add("Saturday");
        days.add("Sunday");

        startTimes = new ArrayList<>();
        endTimes = new ArrayList<>();

        time = new ArrayList<>();
        time.add("am");
        time.add("pm");

        //1 to 12 hours
        for(int j = 0; j < 12; j++){
            startTimes.add(Integer.toString(j+1));
            endTimes.add(Integer.toString(j+1));
        }

        //pulling preferences
        preferences = mainActivity.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        user_session = preferences.getString("name", "");

        //spinners receiving data through ArrayAdapter
        spinner = (Spinner)findViewById(R.id.addDay);
        ArrayAdapter dayArrayAdapter;
        dayArrayAdapter = new ArrayAdapter<>(mainActivity,android.R.layout.simple_spinner_item, days );
        dayArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(dayArrayAdapter);

        spinner2 = (Spinner)findViewById(R.id.addStartTime);
        ArrayAdapter startArrayAdapter;
        startArrayAdapter = new ArrayAdapter<>(mainActivity,android.R.layout.simple_spinner_item, startTimes);
        startArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner2.setAdapter(startArrayAdapter);

        spinner3 = (Spinner)findViewById(R.id.addEndTime);
        ArrayAdapter endArrayAdapter;
        endArrayAdapter = new ArrayAdapter<>(mainActivity,android.R.layout.simple_spinner_item, endTimes);
        endArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner3.setAdapter(endArrayAdapter);

        spinnerAmPm = (Spinner)findViewById(R.id.addtime);
        ArrayAdapter timeArrayAdapter;
        timeArrayAdapter = new ArrayAdapter<>(mainActivity,android.R.layout.simple_spinner_item, time);
        timeArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinnerAmPm.setAdapter(timeArrayAdapter);

        spinnerAmPm2 = (Spinner)findViewById(R.id.addtime2);
        ArrayAdapter time2ArrayAdapter;
        time2ArrayAdapter = new ArrayAdapter<>(mainActivity,android.R.layout.simple_spinner_item, time);
        time2ArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinnerAmPm2.setAdapter(time2ArrayAdapter);

        //instance of database for read/write
        db = new DatabaseManager(mainActivity);

        //instance of button
        addBtn = (Button)findViewById(R.id.addBtn);

        //change button label for updating/adding
        if(mID != 0) {
            addBtn.setText("Update");
        }

        //when edit button clicked
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //grab user selected options form spinners
                String day = spinner.getSelectedItem().toString();
                String start = spinner2.getSelectedItem().toString() + spinnerAmPm.getSelectedItem().toString();        //concatenate hour to am/pm
                String end = spinner3.getSelectedItem().toString() + spinnerAmPm2.getSelectedItem().toString();         // strcat  ->   ^   +   ^

                Log.w("Day", day);  //error check for data input

                try {
                    db.open();

                    if(mID == 0) {  //for adding new row
                        db.insertDay(user_session, day, start, end);
                    } else {        //for updating existing row
                        db.updateRow(mID, user_session, day, start, end);
                    }


                    db.close();
                    cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mainActivity.finish();                  //refresh activity after editing database
                mainActivity.startActivity(mainActivity.getIntent());
            }
        });

    }
}