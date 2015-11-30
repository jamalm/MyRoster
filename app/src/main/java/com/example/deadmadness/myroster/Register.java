package com.example.deadmadness.myroster;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Register extends Activity implements View.OnClickListener {

    //Buttons, fields, textViews and database declarations
    Button regButton;

    TextView cancel;

    EditText userName, firstname, password;

    DatabaseManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //instance of database
        db = new DatabaseManager(this);

        // fixed a variabvle for the button
        regButton = (Button) findViewById(R.id.regButton);
        // fixed variables for the Text fields
        userName = (EditText) findViewById(R.id.userName);
        firstname = (EditText) findViewById(R.id.firstname);
        password = (EditText) findViewById(R.id.password);
        //variable fixed to textview
        cancel = (TextView)findViewById(R.id.cancel);

        //Set listeners to buttons/textviews
        cancel.setOnClickListener(this);
        regButton.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        //switch cases for clicks
        switch(v.getId()) {
            case R.id.regButton:
                //grab info from user-entered fields
                String uname = userName.getText().toString();
                String passwd = password.getText().toString();
                String name = firstname.getText().toString();

                //if they are not empty, publish to database
                if(uname.length() > 0 && passwd.length() > 0 && name.length() > 0)
                {
                    try{
                        db.open();
                        db.registerUser(uname, passwd, name);
                        db.close();
                        Toast.makeText(Register.this, "User Added", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(Register.this, Login.class);
                        i.putExtra("name", name);
                        startActivity(i);
                    }
                    catch(Exception e)
                    {
                        Log.e("REGISTER FAILED", "The registration threw an exception");
                        Toast.makeText(Register.this, "Couldn't open database", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }   //if they are empty, appropriate prompt supplied to user
                else {
                    Toast.makeText(this, "Missing Fields", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.cancel:
                //if user cancels registration, go back to Login activity
                Intent i = new Intent(Register.this,Login.class);
                startActivity(i);
                break;
        }
    }
}
