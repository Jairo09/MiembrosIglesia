package com.example.miembrosiglesia.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.miembrosiglesia.Miembro;

import java.util.ArrayList;
import java.util.HashMap;
public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "android_api";

    // Login table name
    private static final String TABLE_MIEMBRO = "miembro";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_LASTNAME = "lastname";
    private static final String KEY_SOCIEDAD = "sociedad";


    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_MIEMBRO + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_LASTNAME + " TEXT UNIQUE," + KEY_SOCIEDAD + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MIEMBRO);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addMiembro(String name, String lastname, String sociedad) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Name
        values.put(KEY_LASTNAME, lastname); // lastname
        values.put(KEY_SOCIEDAD, sociedad); // sociedad


        // Inserting Row
        long id = db.insert(TABLE_MIEMBRO, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     * */
    public ArrayList<Miembro> getMiembrosDetails() {
        ArrayList<Miembro> miembrosSQLite = new ArrayList<>();


        String selectQuery = "SELECT  * FROM " + TABLE_MIEMBRO;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        //cursor.moveToFirst();
        while(cursor.moveToNext()) {
            Miembro miembro = new Miembro();
           miembro.setNombre(cursor.getString(1));
           miembro.setApellido(cursor.getString(2));
           miembro.setSociedad(cursor.getString(3));
           miembrosSQLite.add(miembro);


        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + miembrosSQLite.toString());

        return miembrosSQLite;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_MIEMBRO, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }
}
