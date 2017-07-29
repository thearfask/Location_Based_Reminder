package ajayverma26.com.ajaylbrfordelete;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by ajay on 24/6/17.
 */


public class DBHelper extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "db_reminders";

    // Contacts table name
    private static final String TABLE_REMINDERS = "reminders";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_TYPE = "type";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LNG = "lng";
    private static final String KEY_RADIUS = "radius";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_REMINDERS + "("
                + KEY_ID  + " INTEGER PRIMARY KEY," + KEY_TITLE + " TEXT,"
                + KEY_TYPE + " TEXT," + KEY_LAT + " DOUBLE,"  + KEY_LNG + " DOUBLE,"+ KEY_RADIUS + " DOUBLE" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMINDERS);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new contact
    void addReminder(Reminder reminder) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_TITLE, reminder.getTitle()); // Contact Name
        values.put(KEY_TYPE, reminder.getType()); // Contact Phone
        values.put(KEY_LAT, reminder.getLat());
        values.put(KEY_LNG, reminder.getLng());
        values.put(KEY_RADIUS, reminder.getRadius());

        // Inserting Row
        db.insert(TABLE_REMINDERS, null, values);
        db.close(); // Closing database connection
    }

    // Getting single contact
    Reminder getReminder(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_REMINDERS, new String[] { KEY_ID,
                        KEY_TITLE, KEY_TYPE, KEY_LAT, KEY_LNG, KEY_RADIUS }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Reminder reminder = new Reminder(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getDouble(3), cursor.getDouble(4), cursor.getDouble(5));
        // return contact
        return reminder;
    }

    // Getting All Contacts
    public ArrayList<Reminder> getAllReminders() {
        ArrayList<Reminder> reminderList = new ArrayList<Reminder>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_REMINDERS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Reminder reminder = new Reminder();
                reminder.setId(Integer.parseInt(cursor.getString(0)));
                reminder.setTitle(cursor.getString(1));
                reminder.setType(cursor.getString(2));
                reminder.setLat(cursor.getDouble(3));
                reminder.setLng(cursor.getDouble(4));
                reminder.setRadius(cursor.getDouble(5));

                // Adding contact to list
                reminderList.add(reminder);
            } while (cursor.moveToNext());
        }

        // return contact list
        return reminderList;
    }

    // Updating single contact
    public int updateReminder(Reminder reminder) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_TITLE,reminder.getTitle());
        values.put(KEY_TYPE,reminder.getType());
        values.put(KEY_LAT,reminder.getLat());
        values.put(KEY_LNG,reminder.getLng());
        values.put(KEY_RADIUS,reminder.getRadius());

        // updating row
        return db.update(TABLE_REMINDERS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(reminder.getId()) });
    }

    // Deleting single contact
    public void deleteReminder(Reminder reminder) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_REMINDERS, KEY_ID + " = ?",
                new String[] { String.valueOf(reminder.getId()) });
        db.close();
    }


    // Getting contacts Count
    public int getRemindersCount() {
        String countQuery = "SELECT  * FROM " + TABLE_REMINDERS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

}
/*
public class DBHelper extends SQLiteOpenHelper {


    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table reminders( rid integer primary key auto_increment, rtitle text,  rtype text, rlat double, rlng double)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ArrayList<Reminder> getAllRemindersFromDB(){

        SQLiteDatabase db = getWritableDatabase();

        ArrayList<Reminder> reminders = new ArrayList<>();
        Cursor c = db.query("reminders",null,null,null,null,null,null);

        while (c.moveToNext()){
            //reminders.add(new Reminder(c.getInt(0),c.getString(1),c.getString(2),c.getString(3),c.getInt(4)));

            reminders.add(new Reminder(c.getInt(c.getColumnIndex("rid")),c.getString(c.getColumnIndex("rtitle")),c.getString(c.getColumnIndex("rtype")),c.getDouble(c.getColumnIndex("rlat")),c.getDouble(c.getColumnIndex("rlng"))));


        }
        db.close();
        return reminders;
    }

    public  long addReminder( Reminder r){

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("rid",r.getId());
        values.put("rtitle",r.getTitle());
        values.put("raddress",r.getAddress());
        values.put("rtype",r.getType());
        values.put("rradius",r.getRadius());
        values.put("rlat",r.getLat());
        values.put("rlng",r.getLng());

        long rowNum = db.insert("reminders",null,values);

        db.close();
        return rowNum;
    }

    public int updateReminder( Reminder r ) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("rid",r.getId());
        values.put("rtitle",r.getTitle());
        values.put("raddress",r.getAddress());
        values.put("rtype",r.getType());
        values.put("rradius",r.getRadius());
        values.put("rlat",r.getLat());
        values.put("rlng",r.getLng());

        int count = db.update("reminders",
                values,
                "rid = ?",
                new String[] { r.getId()+"" }
        );
        db.close();

        return count;
    }

    public int deleteReminder( Reminder r ) {
        SQLiteDatabase db = getWritableDatabase();
        int count =
                db.delete("reminders", "rid = ?", new String[] { r.getId()+"" } );
        db.close();

        return count;
    }

    public int getNextReminderId() {
        SQLiteDatabase db = getWritableDatabase();

        Cursor c  = db.rawQuery("select max(rid)+1 nextreminderid from reminders", null );
        if( c.getCount() == 1 ) {
            c.moveToNext();
            return c.getInt(0);
        }
        return 1;
    }

}*/
