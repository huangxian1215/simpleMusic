package com.example.simplemusic.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.simplemusic.bean.dbMusicInfo;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class UserDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "UserDBHelper";
    private static UserDBHelper mHelp = null;
    private static final String DB_NAME = "user.db";
    private static final int DB_VERSION = 1;
    private static UserDBHelper mHelper = null;
    private SQLiteDatabase mDB = null;

    private static final String TAB_MUSIC_INFO = "tabMusicInfo";

    private UserDBHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    private UserDBHelper(Context context, int version) {
        super(context, DB_NAME, null, version);
    }

    public static UserDBHelper getInstance(Context context, int version) {
        if (version > 0 && mHelper == null) {
            mHelper = new UserDBHelper(context, version);
        } else if (mHelper == null) {
            mHelper = new UserDBHelper(context);
        }
        return mHelper;
    }
    public static UserDBHelper getmHelper(){
        return mHelper;
    }

    public SQLiteDatabase openReadLink() {
        if (mDB == null || mDB.isOpen() != true) {
            mDB = mHelper.getReadableDatabase();
        }
        return mDB;
    }

    public SQLiteDatabase openWriteLink() {
        if (mDB == null || mDB.isOpen() != true) {
            mDB = mHelper.getWritableDatabase();
        }
        return mDB;
    }

    public void closeLink() {
        if (mDB != null && mDB.isOpen() == true) {
            mDB.close();
            mDB = null;
        }
    }

    public String getDBName() {
        if (mHelper != null) {
            return mHelper.getDatabaseName();
        } else {
            return DB_NAME;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        Log.d(TAG, "onCreate");
        String drop_sql = "DROP TABLE IF EXISTS " + TAB_MUSIC_INFO + ";";
        Log.d(TAG, "drop_sql:" + drop_sql);
        db.execSQL(drop_sql);
        String create_sql = "CREATE TABLE IF NOT EXISTS " + TAB_MUSIC_INFO + " ("
                + "_id INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL,"
                + "songid VARCHAR NOT NULL,"
                + "name VARCHAR NOT NULL,"
                + "singer VARCHAR NOT NULL,"
                + "album VARCHAR NOT NULL,"
                + "url VARCHAR NOT NULL,"
                + "time VARCHAR NOT NULL,"
                + "packname VARCHAR NOT NULL,"
                + "love INTEGER NOT NULL"
                + ");";
        Log.d(TAG, "create_sql:" + create_sql);
        db.execSQL(create_sql);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        Log.d(TAG, "onUpgrade oldVersion="+oldVersion+", newVersion="+newVersion);
        if (newVersion > 1) {

        }
    }
    public int delete(String condition) {
        openReadLink();
        //int count = mDB.delete(tabname, "1=1", null); 删除所有
        int count = mDB.delete(TAB_MUSIC_INFO, condition, null);
        return count;
    }

    public long insert(dbMusicInfo song) {
        openWriteLink();
        long result = -1;
        ContentValues cv = new ContentValues();
        cv.put("songid", song.msongid);
        cv.put("name", song.mname);
        cv.put("singer", song.msinger);
        cv.put("album", song.malbum);
        cv.put("url", song.murl);
        cv.put("time", song.mtime);
        cv.put("packname", song.mpackname);
        cv.put("love",song.love);
        result = mDB.insert(TAB_MUSIC_INFO, "", cv);
        // 添加成功后返回行号，失败后返回-1
        return result;
    }
    public ArrayList<dbMusicInfo> query_tab(String condition) {
        openReadLink();
        String sql = String.format("select _id,songid,name,singer,album,url,time,packname,love" + " from %s where %s;", TAB_MUSIC_INFO, condition);
        Log.d(TAG, "query sql: " + sql);
        ArrayList<dbMusicInfo> infoArray = new ArrayList<dbMusicInfo>();
        Cursor cursor = mDB.rawQuery(sql, null);
        if(cursor.moveToFirst()){
            for(;; cursor.moveToNext()){
                dbMusicInfo info = new dbMusicInfo("","");
                info._id = Integer.parseInt(cursor.getString(0));
                info.msongid = cursor.getString(1);
                info.mname = cursor.getString(2);
                info.msinger = cursor.getString(3);
                info.malbum = cursor.getString(4);
                info.murl = cursor.getString(5);
                info.mtime = cursor.getString(6);
                info.mpackname = cursor.getString(7);
                info.love = cursor.getInt(8);
                infoArray.add(info);
                if (cursor.isLast() == true) {
                    break;
                }
            }
            cursor.close();
        }
        return infoArray;
    }

    public long queryAllNum(int type){
        long count = 0;
        String sql = "";
        if(type == 1){
            sql =  "select count(*) from " + TAB_MUSIC_INFO +" where love = 1;" ;
        }else{
            sql = "select count(*) from " + TAB_MUSIC_INFO +";" ;
        }
        Cursor cursor = mDB.rawQuery(sql, null);
        cursor.moveToFirst();
        count = cursor.getLong(0);
        cursor.close();
        return count;
    }

    public int update(String condition, int islove) {
        openReadLink();
        ContentValues cv = new ContentValues();
        cv.put("love", islove);
        int count = mDB.update(TAB_MUSIC_INFO, cv, condition, null);
        return count;
    }

}
