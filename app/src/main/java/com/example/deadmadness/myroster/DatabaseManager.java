package com.example.deadmadness.myroster;

/**
 * Created by deadmadness on 19/11/15.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;


public class DatabaseManager {


    //static variables for columns, tables, etc.

    private static final int DATABASE_VERSION 	= 1;

    private static final String DATABASE        = "MyRoster";

    private static final String DATABASE_USER_TABLE  = "Users";
    private static final String DATABASE_ROSTER_TABLE= "Roster";

    private static final String KEY_ROWID 		= "_id";   	//typical first column in SQLlite
    private static final String KEY_USERNAME	= "user_name"; //sample column - could be anything
    private static final String KEY_PASSWORD    = "password";
    private static final String KEY_NAME        = "first_name";
    private static final String KEY_DAY         = "day";
    private static final String KEY_START       = "start_time";
    private static final String KEY_END         = "end_time";


    //create tables

    private static final String USER_TABLE_CREATE =
            "CREATE TABLE Users(_id integer primary key autoincrement, " +
                    "user_name not null," +
                    "password not null," +
                    "first_name not null)";
    private static final String ROSTER_TABLE_CREATE =
            "CREATE TABLE Roster(_id integer primary key autoincrement, " +
                    "first_name not null, " +
                    "day null, " +
                    "start_time null, " +
                    "end_time null)";

    //other attributes
    final Context context ;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    //constructor
    public DatabaseManager(Context ctx){
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
        Log.w("CONSTRUCTOR","DBManager constructed");
    }

    //inner class for DBHelper (onCreate, onUpgrade )
    private static class DatabaseHelper extends SQLiteOpenHelper {

        //constructor
        DatabaseHelper(Context context) {
            super(context, DATABASE, null, DATABASE_VERSION);
            Log.w("CONSTRUCTOR", "DBHelper Constructed");
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            //execute SQL to create table
            db.execSQL(USER_TABLE_CREATE);
            db.execSQL(ROSTER_TABLE_CREATE);
            Log.e("DATABASE:", "Inside Create Method");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            //execute SQL to update database structure db.execSQL();
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_USER_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_ROSTER_TABLE);
            onCreate(db);
        }
    }// End inner class

    //opens database for reading/writing
    public DatabaseManager open() throws SQLException {
        db  = DBHelper.getWritableDatabase();
        db  = DBHelper.getReadableDatabase();
        return this;
    }

    //insert statements go here

    //registration of user into user table
    public void registerUser(String user_name, String password, String first_name) throws SQLException{

        //inserts values into columns
        db.execSQL("INSERT INTO " + DATABASE_USER_TABLE +
                "(" + KEY_USERNAME + ", " + KEY_PASSWORD + ", " + KEY_NAME + ") VALUES ('" +
                user_name + "', '" + password + "', '" + first_name + "')");
        db.execSQL("INSERT INTO " + DATABASE_ROSTER_TABLE +
                "(" + KEY_NAME + ") VALUES ('" +
                first_name + "')");

    }

    //adding a day to roster table
    public void insertDay(String name, String day, String start, String end) throws SQLException {
        //adds day to the personal roster
        db.execSQL("INSERT INTO " + DATABASE_ROSTER_TABLE +
                " (" + KEY_NAME + ", " + KEY_DAY + ", " + KEY_START + ", " + KEY_END + ") VALUES ('" + name + "', '" +
                day + "', '" + start + "', '" + end + "');");
    }

    //update statements

    //update row in roster
    public void updateRow(Long rowId, String name ,String day ,String start, String end){
        ContentValues selectionArgs = new ContentValues();
        String rowid = Long.toString(rowId);

        selectionArgs.put(KEY_NAME, name);
        selectionArgs.put(KEY_DAY, day);
        selectionArgs.put(KEY_START, start);
        selectionArgs.put(KEY_END, end);
        try{
            //db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null);
            db.update("Roster",selectionArgs, KEY_ROWID + "=" + rowid, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //delete statements
    //deleting specific row
    public void deleteDay(long rowId) {
        String[] selectionArgs = new String[]{Long.toString(rowId)};        //grab specific row id form list view
        try {
            db.delete(DATABASE_ROSTER_TABLE, KEY_ROWID + "=?", selectionArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //select statements
    //select public rows
    public Cursor getAllRows() throws SQLException
    {
        Cursor cursor =
                db.rawQuery("SELECT * FROM " + DATABASE_ROSTER_TABLE, null );
        if (cursor != null)
        {
            cursor.moveToFirst();
        }
        return cursor;
    }

    //select personal roster rows
    public Cursor getMyRows(String fname) {
        String[] uname = new String[] {fname};
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + DATABASE_ROSTER_TABLE + " WHERE first_name=?", uname);

            if(cursor != null) {
                return cursor;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //login validation sql injection protection
    public boolean login (String uname, String passwd) throws SQLException {
        String[] query = new String[] {uname,passwd};
        Cursor c = null;
        //cursor reads database for username and password
        try {
            c = db.rawQuery("SELECT * FROM " + DATABASE_USER_TABLE + " WHERE user_name=? AND password=?", query);

        if(c != null) {
            if(c.getCount() > 0) {
                return true;
            }
        }
        } catch (Exception e){
            Log.e("SELECT ISSUE", "Something wrong with select statement");
            e.printStackTrace();
        }
        return false;
    }



    //close the database
    public void close() {
        DBHelper.close();
    }

}
