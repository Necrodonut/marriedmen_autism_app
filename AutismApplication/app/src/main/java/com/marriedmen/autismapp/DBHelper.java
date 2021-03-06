package com.marriedmen.autismapp;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import static android.content.ContentValues.TAG;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "clients";

    private static final String DATABASE_TABLE = "profiles";
    private static final String DATABASE_TABLE_BEHV = "behaviors";
    private static final String DATABASE_TABLE_ACTIVITY = "activity";
    private static final String DATABASE_TABLE_LOGS = "logs";

    private static final String KEY_PROFILE_ID = "_id";
    private static final String KEY_PROFILE_ID2 = "_id2";
    private static final String KEY_PROFILE_ID3 = "_id3";

    private static final String KEY_NAME = "name";
    private static final String KEY_INFORMATION = "information";
    private static final String KEY_BEHVS = "behaviors";
    private static final String KEY_BEHV_ID = "behv_id";
    //log table
    private static final String KEY_LOG_ID = "_logId";
    private static final String KEY_DATE = "date";
    private static final String KEY_START_TIME = "startTime";
    private static final String KEY_END_TIME = "endTime";

    private static final String KEY_ACTIVITY_ID = "_logId";
    private static final String KEY_ACTIVITY_ID2 = "_logId2";

    private static final String KEY_BEHV_COUNTER = "behvCounter";

    private static final String KEY_ACTIVITIES = "activities";

    public DBHelper(Context context){
        super (context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String profileTable = "CREATE TABLE " + DATABASE_TABLE + "("
                + KEY_PROFILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_NAME + " TEXT NOT NULL, "
                + KEY_INFORMATION + " TEXT"
                + ", UNIQUE ("+ KEY_NAME +")"
                + ")";

        String activityTable = "CREATE TABLE " + DATABASE_TABLE_ACTIVITY + "("
                + KEY_ACTIVITY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_ACTIVITIES + " TEXT NOT NULL"
                + ", UNIQUE ("+ KEY_ACTIVITIES +")"
                +  ")";

        String behaviorTable = "CREATE TABLE " + DATABASE_TABLE_BEHV + "("
                + KEY_BEHV_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_BEHVS + " TEXT NOT NULL"
                + ", UNIQUE ("+ KEY_BEHVS +")"
                + ")";

        String logTable = "CREATE TABLE " + DATABASE_TABLE_LOGS + "("
                + KEY_LOG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_PROFILE_ID3 + " INTEGER NOT NULL, "
                + KEY_ACTIVITY_ID2 + " INTEGER NOT NULL, "
                + KEY_DATE + " TEXT, "
                + KEY_START_TIME + " TEXT, "
                + KEY_END_TIME + " TEXT, "
                //an string "1,2,3" with , as parser, will have to convert string 1 to int in analytics
                + KEY_BEHV_COUNTER + " TEXT, "
                + "FOREIGN KEY ("+ KEY_PROFILE_ID3
                + ") REFERENCES "+ DATABASE_TABLE + "("+ KEY_PROFILE_ID+"), "
                + "FOREIGN KEY ("+ KEY_ACTIVITY_ID2
                + ") REFERENCES "+ DATABASE_TABLE_ACTIVITY + "("+ KEY_ACTIVITY_ID+")"
                +")";

        db.execSQL(profileTable);
        db.execSQL(activityTable);

        db.execSQL(behaviorTable);
        db.execSQL(logTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        // DROP OLDER TABLE IF EXISTS
        database.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        database.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_BEHV);
        database.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_ACTIVITY);
        database.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_LOGS);

        // CREATE TABLE AGAIN
        onCreate(database);
    }


    public void addProfileObj(profileObj profile) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, profile.getName());
        values.put(KEY_INFORMATION, profile.getInfo());
        db.insert(DATABASE_TABLE, null, values);
        db.close();
    }

    public void addActivity(String activity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ACTIVITIES, activity);
        db.insert(DATABASE_TABLE_ACTIVITY, null, values);
        db.close();
    }


    public void addLog(Integer profile_id, Integer activity_id, String counterArray) {
        /*
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
        String formattedDate = date.format(cal.getTime());
        String formattedTime = time.format(cal.getTime());
        */

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();


        values.put(KEY_PROFILE_ID3, profile_id);
        //values.put(KEY_DATE, formattedDate);

        //values.put(KEY_START_TIME, formattedTime);

        //same start and end if fine for now
        //values.put(KEY_END_TIME, formattedTime);
        //this is saying the activity taking place has id 1, in this case its dinner (see init in mainactivity)
        values.put(KEY_ACTIVITY_ID2, activity_id);
        //just adding a string for now
        //this is saying first behv happened once, second twice, ect... There are 4 total behvs currently
        values.put(KEY_BEHV_COUNTER, counterArray);

        db.insert(DATABASE_TABLE_LOGS, null, values);
        db.close();
    }

    public void addBehavior(String behv) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_BEHVS, behv);
        db.insert(DATABASE_TABLE_BEHV, null, values);
        db.close();
    }

    public Integer getBehvId(String behv) {
        //get behv id from bev name
        SQLiteDatabase db = getReadableDatabase();

        String qury = "SELECT "+ KEY_BEHV_ID +" FROM "+ DATABASE_TABLE_BEHV
                +" WHERE "+ KEY_BEHVS+" = "+ '"'+behv+'"';

        Cursor mCursor = db.rawQuery(qury, null);
        mCursor.moveToFirst();
        Integer id = mCursor.getInt(0);
        return id;
    }

    public String[] getProfiles() {

        SQLiteDatabase db = getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, DATABASE_TABLE);

        //Log.d("test", "getProfiles: " + count);
        String[] profiles = new String[(int)count];
        Cursor mCursor = db.rawQuery("select * from " + DATABASE_TABLE, null);
        mCursor.moveToFirst();
        for (int i = 0; i < count; i++)
        {
            if (mCursor.isAfterLast() || mCursor == null){ break; }
            String name = mCursor.getString(mCursor.getColumnIndex(KEY_NAME));
            profiles[i] = name;
            mCursor.moveToNext();
        }

        return profiles;
    }
    public String[] getBehaviors() {
        SQLiteDatabase db = getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, DATABASE_TABLE_BEHV);

        //Log.d("test", "getProfiles: " + count);
        String[] behaviors = new String[(int)count];
        Cursor mCursor = db.rawQuery("select * from " + DATABASE_TABLE_BEHV, null);
        mCursor.moveToFirst();
        for (int i = 0; i < count; i++)
        {
            if (mCursor.isAfterLast() || mCursor == null){ break; }
            String name = mCursor.getString(mCursor.getColumnIndex(KEY_BEHVS));
            behaviors[i] = name;
            mCursor.moveToNext();
        }

        return behaviors;

    }

    public String[] getActivities() {
        SQLiteDatabase db = getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, DATABASE_TABLE_ACTIVITY);

        String[] activities = new String[(int)count];
        Cursor mCursor = db.rawQuery("select * from " + DATABASE_TABLE_ACTIVITY, null);
        mCursor.moveToFirst();
        for (int i = 0; i < count; i++)
        {
            if (mCursor.isAfterLast() || mCursor == null){ break; }
            String name = mCursor.getString(mCursor.getColumnIndex(KEY_ACTIVITIES));
            activities[i] = name;
            mCursor.moveToNext();
        }

        return activities;
    }

    public int[] getActIDs() {
        SQLiteDatabase db = getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, DATABASE_TABLE_ACTIVITY);

        int[] activityIDs = new int[(int)count];
        Cursor mCursor = db.rawQuery("select * from " + DATABASE_TABLE_ACTIVITY, null);
        mCursor.moveToFirst();
        for (int i = 0; i < count; i++)
        {
            if (mCursor.isAfterLast() || mCursor == null){ break; }
            int id = mCursor.getInt(mCursor.getColumnIndex(KEY_ACTIVITY_ID));
            activityIDs[i] = id;
            mCursor.moveToNext();
        }

        return activityIDs;
    }

    public int[] getIDs() {
        SQLiteDatabase db = getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, DATABASE_TABLE);

        int[] IDs = new int[(int)count];
        Cursor mCursor = db.rawQuery("select * from " + DATABASE_TABLE, null);
        mCursor.moveToFirst();
        for (int i = 0; i < count; i++)
        {
            if (mCursor.isAfterLast() || mCursor == null){ break; }
            int ID = mCursor.getInt(mCursor.getColumnIndex(KEY_PROFILE_ID));
            IDs[i] = ID;
            mCursor.moveToNext();
        }

        return IDs;
    }

    public void _profilesclearAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DATABASE_TABLE, null, new String[]{});
        db.close();
    }

    public void _logClearAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DATABASE_TABLE_LOGS, null, new String[]{});
        db.close();
    }

    public void _behvClearAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DATABASE_TABLE_BEHV, null, new String[]{});
        db.close();
    }

    public void _activityClearAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DATABASE_TABLE_ACTIVITY, null, new String[]{});
        db.close();
    }

    public int getBehvTableSize() {
        SQLiteDatabase db = this.getWritableDatabase();
        Long size = DatabaseUtils.queryNumEntries(db, DATABASE_TABLE_BEHV);
        Integer i = (int) (long) size;
        db.close();
        return i;
    }

    public void test(){
        //select * from DATABASE_TABLE inner join DATABASE_TABLE_LOG on DATABASE_TABLE_LOG.CHILD_ID = DATABASE_TABLE.ID
    }

    public String testingquery() {
        SQLiteDatabase db = this.getReadableDatabase();

        //doesn't work
        String test_query = "SELECT * FROM " +DATABASE_TABLE+  " INNER JOIN " +DATABASE_TABLE_LOGS+ " ON "
                + DATABASE_TABLE_LOGS+ "." +KEY_PROFILE_ID3+ " = " +DATABASE_TABLE+ "." + KEY_PROFILE_ID;

        String selectQuery = "SELECT " + KEY_BEHVS + " FROM " + DATABASE_TABLE_BEHV;
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        String str = cursor.getString(0);
        return str;
    }
}
