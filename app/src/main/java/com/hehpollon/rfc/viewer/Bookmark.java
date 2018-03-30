package com.hehpollon.rfc.viewer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;

/**
 * Created by han on 2018-03-29.
 */

public class Bookmark {
    Context mContext;
    private MySQLiteOpenHelper helper;
    String dbName = "bookmark_file.db";
    int dbVersion = 1;
    private SQLiteDatabase db;
    String tag = "SQLite";


    public Bookmark(Context context) {
        this.mContext = context;
        helper = new MySQLiteOpenHelper(mContext, dbName, null, dbVersion);
        db = helper.getWritableDatabase();
    }

    public void insertBookmark(String url) {
        db.execSQL("INSERT INTO bookmark (url) VALUES('" + url + "');");
    }

    public boolean isBookmark(String url) {
        SQLiteStatement s = db.compileStatement( "SELECT count(*) FROM bookmark WHERE url='" + url + "';");
        long count = s.simpleQueryForLong();
        if(count > 0) {
            return true;
        }else{
            return false;
        }
    }

    public ArrayList<String> getAllBookmark() {
        ArrayList<String> urlList = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT url FROM bookmark;", null);
        while(c.moveToNext()) {
            String url = c.getString(0);
            urlList.add(url);
        }
        return urlList;
    }

    public void removeBookmark(String url) {
        db.execSQL("DELETE FROM bookmark WHERE url='" + url + "';");
    }

    public void removeAllBookmark() {
        db.execSQL("DELETE FROM bookmark;");
        db.execSQL("VACUUM;");
    }
}
