package com.example.administrator.zhihu.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**缓存新闻
 * Created by Administrator on 2016/9/3 0003.
 * @laoqiang
 */
public class CacheOpenHelper extends SQLiteOpenHelper {
    public CacheOpenHelper(Context context) {
        super(context, "cache.txt", null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists CacheList (id integer primary key autoincrement,date text,json text )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS CacheList");
        onCreate(db);
        db.execSQL("DROP TABLE IF EXISTS CacheList");

    }
}
