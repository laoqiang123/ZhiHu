package com.example.administrator.zhihu.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/9/3 0003.
 */
public class CacheOpenHelper extends SQLiteOpenHelper {
    public CacheOpenHelper(Context context) {
        super(context, "cache.txt", null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists CacheList (id integer primary key autoincrement,date text,json text )");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS CacheList");
        onCreate(db);

    }
}
