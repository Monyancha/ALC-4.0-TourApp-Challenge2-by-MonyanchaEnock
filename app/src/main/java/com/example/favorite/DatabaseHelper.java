package com.example.favorite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.item.ItemPlaceList;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "newsapp.db";
    public static final String TABLE_FAVOURITE_NAME = "favourite";

    public static final String KEY_ID = "id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_ADD = "address";
    public static final String KEY_AVG_RATE = "rate_avg";
    public static final String KEY_TOTAL_RATE = "total_rate";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_FAVOURITE_TABLE = "CREATE TABLE " + TABLE_FAVOURITE_NAME + "("
                + KEY_ID + " INTEGER,"
                + KEY_TITLE + " TEXT,"
                + KEY_IMAGE + " TEXT,"
                + KEY_ADD + " TEXT,"
                + KEY_AVG_RATE + " TEXT,"
                + KEY_TOTAL_RATE + " TEXT"
                + ")";
        db.execSQL(CREATE_FAVOURITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVOURITE_NAME);
        // Create tables again
        onCreate(db);
    }

    public boolean getFavouriteById(String story_id) {
        boolean count = false;
        SQLiteDatabase db = this.getWritableDatabase();
        String[] args = new String[]{story_id};
        Cursor cursor = db.rawQuery("SELECT id FROM favourite WHERE id=? ", args);
        if (cursor.moveToFirst()) {
            count = true;
        }
        cursor.close();
        db.close();
        return count;
    }

    public void removeFavouriteById(String _id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM  favourite " + " WHERE " + KEY_ID + " = " + _id);
        db.close();
    }

    public long addFavourite(String TableName, ContentValues contentvalues, String s1) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.insert(TableName, s1, contentvalues);
    }

    public ArrayList<ItemPlaceList> getFavourite() {
        ArrayList<ItemPlaceList> chapterList = new ArrayList<>();
        String selectQuery = "SELECT *  FROM "
                + TABLE_FAVOURITE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ItemPlaceList contact = new ItemPlaceList();
                contact.setPlaceId(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ID)));
                contact.setPlaceName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE)));
                contact.setPlaceImage(cursor.getString(cursor.getColumnIndexOrThrow(KEY_IMAGE)));
                contact.setPlaceAddress(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ADD)));
                contact.setPlaceRateAvg(cursor.getString(cursor.getColumnIndexOrThrow(KEY_AVG_RATE)));
                contact.setPlaceRateTotal(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TOTAL_RATE)));

                chapterList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return chapterList;
    }
}
