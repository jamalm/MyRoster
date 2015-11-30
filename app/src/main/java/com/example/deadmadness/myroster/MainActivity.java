package com.example.deadmadness.myroster;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import android.widget.ListView;

import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;

public class MainActivity extends Activity implements View.OnClickListener {

    //Ids for long clicks on listView
    private long deleteRow;
    private long updateRow;
    //integer for setting roster type view i.e. Full roster or Personal Roster
    private int view;

    //declare database, listview, textviews etc..
    DatabaseManager db;

    ListView rosterList;

    Button insert;

    TextView viewAll;
    TextView logout;

    //used to Set user session for personal roster display
    String user_session;

    // used for grabbing user preferences
    SharedPreferences preferences1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //grab user preferences i.e. session and roster view type
        preferences1 = this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        user_session = preferences1.getString("name", "");
        view = preferences1.getInt("view", 0);

        ///instance of database
        db = new DatabaseManager(this);

        //clickable text views
        logout = (TextView) findViewById(R.id.logout);
        viewAll = (TextView) findViewById(R.id.viewAll);

        //button for inserts
        insert = (Button) findViewById(R.id.insert);

        //set listeners to clickable objects
        insert.setOnClickListener(this);
        logout.setOnClickListener(this);
        viewAll.setOnClickListener(this);

        //select view; full roster view or personal roster view
        if(view ==0){
            viewAll.setText("View Full Roster");
        }
        else if(view == 1) {
            viewAll.setText("View Personal Roster");
        }

        // fix a variable to use ListView
        rosterList = (ListView) findViewById(R.id.rosterList);

        // allow menu pop up after long press
        registerForContextMenu(rosterList);

        //if personal view
        if (view == 0) {
            try {
                db.open();
                // attaching custom adapter to rosterList
                Cursor mainTable = db.getMyRows(user_session); // data into cursor from database
                ListCursor cursorAdapter = new ListCursor(MainActivity.this, mainTable);
                rosterList.setAdapter(cursorAdapter);       //listview
                Toast.makeText(this, "Roster loaded", Toast.LENGTH_SHORT).show();

                db.close();
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Failed to get Roster", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
        //if Public view
        else if(view == 1){
            try {
                db.open();
                //attaches custom adapter with all data from database; non-user specific
                Cursor mainTable = db.getAllRows(); // data into cursor from database
                ListCursor cursorAdapter = new ListCursor(MainActivity.this, mainTable);
                rosterList.setAdapter(cursorAdapter);       //listview
                Toast.makeText(this, "Roster loaded", Toast.LENGTH_SHORT).show();

                db.close();
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Failed to get Roster", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    // Context menu for long clicks on listView
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.context_menu, menu);
    }

    // when long pressed
    public boolean onContextItemSelected(MenuItem item) {
        //get information on long press ( mainly looking for ID of row, i.e. info.id )
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        //switch for options in context menu
        switch(item.getItemId()){
            case R.id.id_delete:    //delete selected
                if(view == 0) {     //user can write new data to personal roster
                    try {
                        db.open();
                        deleteRow = info.id;                            //set id to variable to pass into method
                        Log.w("Row selected: ", "" + deleteRow);
                        db.deleteDay(deleteRow);                        //delete row with id
                        db.close();
                        Toast.makeText(this, "Roster loaded", Toast.LENGTH_SHORT).show();
                        finish();                                       //refresh activity after changes made
                        startActivity(getIntent());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else if(view == 1){           //for public view, not allowed to edit, read only permissions
                    Toast.makeText(this,"Cannot edit full Roster", Toast.LENGTH_SHORT).show();
                }
                break;
            //for update selection
            case R.id.id_update:
                    if(view ==0) {          //for personal view
                        updateRow = info.id;
                        EditDialog updateDialog = new EditDialog(MainActivity.this, updateRow); // open dialog to enter updated data
                        updateDialog.show();
                    } else if(view == 1) {  //for public view
                        Toast.makeText(this,"Cannot edit full Roster", Toast.LENGTH_SHORT).show();
                    }
                break;
            default:
                return super.onContextItemSelected(item);
        }
        return false;
    }


    //for normal clickable items (buttons etc.)
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.insert:
                Log.w("Button","insert");
                EditDialog addDialog = new EditDialog(MainActivity.this,0);     //open dialog to enter new data and insert to database
                addDialog.show();
                break;
            case R.id.logout:
                SharedPreferences preferences2 = this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor= preferences2.edit();
                editor.clear().apply();                                     //clear preferences after logout so user_session reset

                Intent logOff = new Intent(MainActivity.this, Login.class); //call login method
                startActivity(logOff);
                break;

            case R.id.viewAll:                                              //switch views between personal view and public view
                if(view ==0) {
                    SharedPreferences.Editor editor2 = preferences1.edit();
                    editor2.putInt("view", 1);
                    editor2.apply();
                    finish();                                               //refresh activity after changes
                    startActivity(getIntent());
                }
                else if(view == 1) {
                    SharedPreferences.Editor editor2 = preferences1.edit();
                    editor2.putInt("view", 0);
                    editor2.apply();
                    finish();                                               //refresh activity
                    startActivity(getIntent());
                }
                break;
        }
    }
}
