package com.example.deadmadness.myroster;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;

public class Login extends Activity implements View.OnClickListener {

    //Buttons, Fields and database declaration
    Button logButton, regButton;
    EditText userName, password, name;
    DatabaseManager db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // instance of a database
        db = new DatabaseManager(this);

        //taking out from previous preferences to check if user logged in before
        SharedPreferences loginPref = this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);

        if(loginPref.getInt("LOGGEDIN",0) == 1) {
            //if if user logged in previously, go straight to MainActivity
            Intent i = new Intent(Login.this, MainActivity.class);
            startActivity(i);
        }
        // if not logged in, load login layout
        setContentView(R.layout.activity_login);



        //Text fields being fixed to variables
        userName = (EditText) findViewById(R.id.userName);
        password = (EditText) findViewById(R.id.password);
        name = (EditText) findViewById(R.id.name);

        //Buttons being fixed to variables
        logButton = (Button) findViewById(R.id.logButton);
        regButton = (Button) findViewById(R.id.regButton);

        //fixing onClick listeners on the buttons
        logButton.setOnClickListener(this);
        regButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //switch cases to check which button was clicked
        switch(v.getId()) {
            case R.id.logButton:
                //grabbing information entered
                String uname = userName.getText().toString();
                String passwd = password.getText().toString();
                String session_name = name.getText().toString();

                //if fields are not empty,
                if(uname.length() > 0 && passwd.length() > 0)
                {
                    try {
                        db.open();
                        //validate username and password
                        if(db.login(uname, passwd)) {

                            //adding the key values to preferences i.e. username, session and roster_view type
                            SharedPreferences preferences = this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putInt("LOGGEDIN", 1);
                            editor.putInt("view", 0);
                            editor.putString("name", session_name);
                            editor.apply();

                            Toast.makeText(Login.this, "Successfully Logged in!", Toast.LENGTH_SHORT).show();
                            // go to MainActivity
                            Intent mainIntent = new Intent(Login.this, MainActivity.class);
                            startActivity(mainIntent);
                        }
                        else {
                            //otherwise if username/password incorrect
                            Toast.makeText(Login.this, "Username or password incorrect", Toast.LENGTH_SHORT).show();
                        }
                        db.close();
                    } catch (SQLException e) {
                        Log.e("LOGIN ISSUE", "login function went wrong");
                        e.printStackTrace();
                    }

                }
                else    //if fields are empty..
                {
                    Log.e("EMPTY FIELDS", "Username and password are empty");
                    Toast.makeText(Login.this, "Missing Field", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.regButton:        //start registration activity
                Intent regIntent = new Intent(Login.this, Register.class);
                startActivity(regIntent);

                break;
        }
    }
}
