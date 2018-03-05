package com.codepath.apps.restclienttemplate.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.codepath.apps.restclienttemplate.models.DraftTweet;

import java.util.ArrayList;

/**
 * Created by anushree on 9/29/2017.
 */

public class DraftTweetDAO {

    public static final String TITLE = "draft";
    public static final String ID = "_id";
    public static final String DRAFT_TABLE = "DraftTable";

    DraftDatabase helper;
    Context mCtx;

    public DraftTweetDAO(Context ctx){
        helper = DraftDatabase.getInstance(ctx);
        mCtx = ctx;
    }


    public Boolean addDraft(DraftTweet dt){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TITLE,dt.getDraft());
        long id = db.insert(DRAFT_TABLE,null,cv);

        if(id==-1)
            return false;
        else
        {
            dt.set_id((int)id);
            return true;
        }
    }


    public ArrayList<DraftTweet> getDrafts(){
        ArrayList<DraftTweet> list = new ArrayList<>();

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(DRAFT_TABLE,null,null,null,null,null,null);
        if(c.getCount()>0){
            c.moveToFirst();
            do{
                DraftTweet draft = new DraftTweet(c.getLong(c.getColumnIndex(ID)),c.getString(c.getColumnIndex(TITLE)));
                list.add(draft);
            }while(c.moveToNext());
        }

        c.close();
        return list;
    }


    public Boolean deleteDraft(DraftTweet dt){
        SQLiteDatabase db = helper.getWritableDatabase();
        String [] whereArgs = new String[]{String.valueOf(dt.get_id())};
        int noofrows = db.delete(DRAFT_TABLE,ID+"=?",whereArgs);
        if(noofrows>0)
            return true;
        return false;
    }




}
