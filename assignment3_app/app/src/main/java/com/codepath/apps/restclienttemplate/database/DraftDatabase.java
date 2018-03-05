package com.codepath.apps.restclienttemplate.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by anushree on 9/29/2017.
 */

public class DraftDatabase extends SQLiteOpenHelper {


    public static DraftDatabase helper;
    public static final String DRAFT_DATABASE = "draftDatabase";
    public static final int VERSION = 1;
    public static final String TITLE = "draft";
    public static final String ID = "_id";
    public static final String DRAFT_TABLE = "DraftTable";
    public static final String CREATE_TABLE = "create table "+DRAFT_TABLE+"( "+ ID +" integer primary key autoincrement, "+TITLE+" text)";

    private DraftDatabase(Context context) {
        super(context, DRAFT_DATABASE, null, VERSION);
    }


    public static DraftDatabase getInstance(Context ctx){
        if(helper==null){
            helper = new DraftDatabase(ctx);
        }
        return helper;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop DraftTable if exists");
        onCreate(sqLiteDatabase);
    }
}
