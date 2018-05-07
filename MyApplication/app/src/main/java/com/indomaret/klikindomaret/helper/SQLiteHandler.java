package com.indomaret.klikindomaret.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHandler extends SQLiteOpenHelper {
    private static final String TAG = SQLiteHandler.class.getSimpleName();
    Context context;

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "klikindomaret";
    private static final String PROFILE = "profile";
    private static final String DEFAULT_ADDRESS = "default_address";
    private static final String TABLE_NOTIF = "notification";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + PROFILE + "(ID INTEGER PRIMARY KEY, PROFILE_OBJECT TEXT)";
        String CREATE_DEFAULT_ADDRESS_TABLE = "CREATE TABLE " + DEFAULT_ADDRESS + "(ID INTEGER PRIMARY KEY, ADDRESS_OBJECT TEXT)";

        db.execSQL(CREATE_LOGIN_TABLE);
        db.execSQL(CREATE_DEFAULT_ADDRESS_TABLE);

        Log.d(TAG, "Database tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PROFILE);
        db.execSQL("DROP TABLE IF EXISTS " + DEFAULT_ADDRESS);
        onCreate(db);
    }

    public void insertProfile(String profile){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("PROFILE_OBJECT", profile);
        db.insert(PROFILE, null, values);
    }

    public String getProfile(){
        SQLiteDatabase db = this.getWritableDatabase();
        String userProfile = null;
        Cursor c = db.rawQuery("SELECT * FROM " + PROFILE + " ORDER BY ID DESC LIMIT 1", null);

        if(c != null){
            if(c.moveToFirst()){
                userProfile = c.getString(c.getColumnIndex("PROFILE_OBJECT"));
            }
        }

        db.close();
        return userProfile;
    }

    public void insertDefaultAddress(String defaultAddress){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ADDRESS_OBJECT", defaultAddress);
        db.insert(DEFAULT_ADDRESS, null, values);
    }

    public String getDefaultAddress(){
        SQLiteDatabase db = this.getWritableDatabase();
        String userProfile = null;
        Cursor c = db.rawQuery("SELECT * FROM " + DEFAULT_ADDRESS + " ORDER BY ID DESC LIMIT 1", null);

        if(c != null){
            if(c.moveToFirst()){
                userProfile = c.getString(c.getColumnIndex("ADDRESS_OBJECT"));
            }
        }

        db.close();
        return userProfile;
    }

    public int getDefaultAddressCount(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT Count(*) FROM " + DEFAULT_ADDRESS, null);
        c.moveToFirst();
        int count = c.getInt(0);

        return count;
    }

    public void deleteData(){
        context.deleteDatabase(DATABASE_NAME);
    }
}
